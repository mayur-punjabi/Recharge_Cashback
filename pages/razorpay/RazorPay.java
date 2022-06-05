package razorpay;

import java.util.List;

import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WindowType;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class RazorPay extends CommonFunctions implements RazorPay_OR {

	public String checkOfferAvailable(String phoneNo, String emailId, String amount) {

		String failure = "";

		if (phoneNo.trim().isEmpty() || emailId.trim().isEmpty() || amount.trim().isEmpty()) {
			failure = "Data is empty for razor pay. Phone - '" + phoneNo + "'. Email id - '" + emailId + "'. Amount - '"
					+ amount + "'";
			return failure;
		}

		driver.switchTo().newWindow(WindowType.TAB);
		String razorPayURL = Configuration.getProperty("razorPayURL");
		if (razorPayURL == null || razorPayURL.trim().isEmpty()) {
			failure = "Razor pay url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(razorPayURL)) {
			failure = "Failed to launch razor pay url - " + razorPayURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		if (!waitForElement(razorPayVerification, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Razor pay page didn't open properly. Razor Pay URL url might be wrong";
			reportFailure(failure);
			return failure;
		}

		if (waitForElement(paymentCompleted, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Payment completed for url - " + razorPayURL + ". Please enter another URL";
			reportFailure(failure);
			return failure;
		}

		// old razorpay
		if (waitForElement(frame, 5, WaitType.visibilityOfElementLocated)) {
			switchToFrame(frame);
			waitForPageLoad(60);
		}

		if (!waitForElement(phone, 5, WaitType.presenceOfElementLocated)) {
			failure = "Phone field isn't present on razorpay";
			reportFailure(failure);
			return failure;
		}
		if (!setValueMultipleTimes(phone, phoneNo)) {
			failure = "Failed to set phone on razorpay";
			reportFailure(failure);
			return failure;
		}

		if (!isElementDisplayed(emailField)) {
			failure = "Email field isn't present on razorpay";
			reportFailure(failure);
			return failure;
		}
		if (!setValueMultipleTimes(emailField, emailId)) {
			failure = "Failed to set email on razorpay";
			reportFailure(failure);
			return failure;
		}

		// old razorpay
		if (isElementDisplayed(paymentInParts)) {

			click(paymentInParts);

			if (!waitForElement(amountField, 5, WaitType.presenceOfElementLocated)) {
				failure = "Amount field isn't present after clicking Payment in parts";
				reportFailure(failure);
				return failure;
			}
			if (!setValueMultipleTimes(amountField, amount)) {
				failure = "Failed to set amount on razorpay";
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(nextButton, 2, WaitType.presenceOfElementLocated)) {
				failure = "Next button isn't present after entering phone number, email and amount";
				reportFailure(failure);
				return failure;
			}

			// trying to click next button 3 times so that amazon pay option is present
			boolean amazonPayOptionPresent = false;
			for (int i = 0; i < 3; i++) {
				click(nextButton);
				if (waitForElement(wallet, 5, WaitType.visibilityOfElementLocated)) {
					amazonPayOptionPresent = true;
					break;
				} else {
					log.debug("Wallet option not present for try number - " + i);
				}
			}

			if (!amazonPayOptionPresent) {
				failure = "Wallet option isn't present after clicking next button";
				reportFailure(failure);
				return failure;
			}

			// new razorpay
		} else if (isElementDisplayed(newAmountField)) {
			if (!setValueMultipleTimes(newAmountField, amount)) {
				failure = "Failed to set amount on razorpay";
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(newPayButton, 2, WaitType.presenceOfElementLocated)) {
				failure = "Pay button isn't present after entering phone number, email and amount";
				reportFailure(failure);
				return failure;
			}

			click(newPayButton);

			waitForElement(loading, 10, WaitType.visibilityOfElementLocated);

			if (!waitForElement(loading, 30, WaitType.invisibilityOfElementLocated)) {
				failure = "Loading isn't completed after clicking pay button";
				reportFailure(failure);
				return failure;
			}

			if (waitForElement(frame, 10, WaitType.visibilityOfElementLocated)) {
				switchToFrame(frame);
				waitForPageLoad(60);
			} else {
				failure = "Payment popup didn't open after clicking pay button";
				reportFailure(failure);
				return failure;
			}
		} else {
			failure = "Amount field or payment in parts not present";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(wallet);
		click(wallet);

		if (!waitForElement(amazonPayWallet, 10, WaitType.presenceOfElementLocated)) {
			log.error("Amazon pay wallet not present");
			jsScrollToElement(amazonPayWallet);
		}

		if (!waitForElement(amazonPayWallet, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Amazon pay wallet option is not present";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(amazonPayWallet, 2, WaitType.elementToBeClickable)) {
			failure = "Amazon pay wallet option is not clickable";
			reportFailure(failure);
			return failure;
		}

		List<String> winHandles = getWindowHandles();
		int noOfWindows = winHandles.size();

		// clicking amazon pay wallet 3 times so that it gets selected
		boolean amazonOptionSelected = false;
		for (int i = 0; i < 3; i++) {
			click(amazonPayWallet);
			if (waitForElement(amazonPayWalletSelected, 5, WaitType.visibilityOfElementLocated)) {
				amazonOptionSelected = true;
				break;
			} else {
				log.debug("Amazon pay wallet option isn't selected for try number - " + i);
			}
		}

		if (!amazonOptionSelected) {
			failure = "Failed to select Amazon pay wallet option";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(payButton, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Pay button isn't present after selecting Amazon pay wallet option";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(payButton, 5, WaitType.elementToBeClickable)) {
			failure = "Pay button isn't clickable after selecting Amazon pay wallet option";
			reportFailure(failure);
			return failure;
		}

		try {
			click(payButton);
		} catch (ElementClickInterceptedException e) {
			log.error("Element click intercepted exception for pay button. Trying js click");
			javaScriptClick(payButton);
		}

		if (waitForNumberOfWindowsToBe(noOfWindows + 1, 5)) {
			log.debug("New window opened after clicking pay button");
			switchToNewWindow(winHandles);

			if (!waitForElement(walletPageLoading, 20, WaitType.invisibilityOfElementLocated)) {
				failure = "New window loading isn't completed";
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(payNow, 10, WaitType.visibilityOfElementLocated)) {
				failure = "Pay Now button isn't present in new window";
				reportFailure(failure);
				return failure;
			}

			// check offer is available or not
			if (isElementDisplayed(offer)) {
				log.debug("Offer available");
			} else {
				failure = "Offer not available";
				reportFailure(failure);
				return failure;
			}
		} else {
			failure = "New window didn't open after clicking Pay button";
			reportFailure(failure);
			return failure;
		}

		if (getWindowHandles().size() > 1) {
			closeAllwindowExcept(globalWinHandle);
		}

		return failure;
	}

	public String getCashback(String phoneNo, String emailId, String amount) {

		String failure = "";

		if (phoneNo.trim().isEmpty() || emailId.trim().isEmpty() || amount.trim().isEmpty()) {
			failure = "Data is empty for razor pay. Phone - '" + phoneNo + "'. Email id - '" + emailId + "'. Amount - '"
					+ amount + "'";
			return failure;
		}

		driver.switchTo().newWindow(WindowType.TAB);
		String razorPayURL = Configuration.getProperty("razorPayURL");
		if (razorPayURL == null || razorPayURL.trim().isEmpty()) {
			failure = "Razor pay url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(razorPayURL)) {
			failure = "Failed to launch razor pay url - " + razorPayURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		if (!waitForElement(razorPayVerification, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Razor pay page didn't open properly. Razor Pay URL url might be wrong";
			reportFailure(failure);
			return failure;
		}

		if (waitForElement(paymentCompleted, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Payment completed for url - " + razorPayURL + ". Please enter another URL";
			reportFailure(failure);
			return failure;
		}

		// old razorpay
		if (waitForElement(frame, 5, WaitType.visibilityOfElementLocated)) {
			switchToFrame(frame);
			waitForPageLoad(60);
		} else {
			log.debug("Frame not visible. Might be new razorpay");
		}

		if (!waitForElement(phone, 5, WaitType.presenceOfElementLocated)) {
			failure = "Phone field isn't present on razorpay";
			reportFailure(failure);
			return failure;
		}
		if (!setValueMultipleTimes(phone, phoneNo)) {
			failure = "Failed to set phone on razorpay";
			reportFailure(failure);
			return failure;
		}

		if (!isElementDisplayed(emailField)) {
			failure = "Email field isn't present on razorpay";
			reportFailure(failure);
			return failure;
		}
		if (!setValueMultipleTimes(emailField, emailId)) {
			failure = "Failed to set email on razorpay";
			reportFailure(failure);
			return failure;
		}

		// old razorpay
		if (isElementDisplayed(paymentInParts)) {
			log.debug("Old razorpay");
			click(paymentInParts);

			if (!waitForElement(amountField, 5, WaitType.presenceOfElementLocated)) {
				failure = "Amount field isn't present after clicking Payment in parts";
				reportFailure(failure);
				return failure;
			}
			if (!setValueMultipleTimes(amountField, amount)) {
				failure = "Failed to set amount on razorpay";
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(nextButton, 2, WaitType.presenceOfElementLocated)) {
				failure = "Next button isn't present after entering phone number, email and amount";
				reportFailure(failure);
				return failure;
			}

			// trying to click next button 3 times so that amazon pay option is present
			boolean amazonPayOptionPresent = false;
			for (int i = 0; i < 3; i++) {
				click(nextButton);
				if (waitForElement(wallet, 5, WaitType.visibilityOfElementLocated)) {
					amazonPayOptionPresent = true;
					break;
				} else {
					log.debug("Wallet option not present for try number - " + i);
				}
			}

			if (!amazonPayOptionPresent) {
				failure = "Wallet option isn't present after clicking next button";
				reportFailure(failure);
				return failure;
			}

			// new razorpay
		} else if (isElementDisplayed(newAmountField)) {
			log.debug("New razorpay");
			if (!setValueMultipleTimes(newAmountField, amount)) {
				failure = "Failed to set amount on razorpay";
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(newPayButton, 2, WaitType.presenceOfElementLocated)) {
				failure = "Pay button isn't present after entering phone number, email and amount";
				reportFailure(failure);
				return failure;
			}

			click(newPayButton);

			if (!waitForElement(loading, 5, WaitType.visibilityOfElementLocated)) {
				log.error("Loading didn't appear after clicking pay button");
			}

			if (!waitForElement(loading, 30, WaitType.invisibilityOfElementLocated)) {
				failure = "Loading isn't completed after clicking pay button";
				reportFailure(failure);
				return failure;
			}

			if (waitForElement(frame, 10, WaitType.visibilityOfElementLocated)) {
				switchToFrame(frame);
				waitForPageLoad(60);
			}
		} else {
			failure = "Amount field or payment in parts not present";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(wallet);
		click(wallet);

		if (!waitForElement(amazonPayWallet, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Amazon pay wallet option is not present";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(amazonPayWallet, 2, WaitType.elementToBeClickable)) {
			failure = "Amazon pay wallet option is not clickable";
			reportFailure(failure);
			return failure;
		}

		List<String> winHandles = getWindowHandles();
		int noOfWindows = winHandles.size();

		// clicking amazon pay wallet 3 times so that it gets selected
		boolean amazonOptionSelected = false;
		for (int i = 0; i < 3; i++) {
			click(amazonPayWallet);
			if (waitForElement(amazonPayWalletSelected, 5, WaitType.visibilityOfElementLocated)) {
				amazonOptionSelected = true;
				break;
			} else {
				log.debug("Amazon pay wallet option isn't selected for try number - " + i);
			}
		}

		if (!amazonOptionSelected) {
			failure = "Failed to select Amazon pay wallet option";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(payButton, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Pay button isn't present after selecting Amazon pay wallet option";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(payButton, 5, WaitType.elementToBeClickable)) {
			failure = "Pay button isn't clickable after selecting Amazon pay wallet option";
			reportFailure(failure);
			return failure;
		}

		try {
			click(payButton);
		} catch (ElementClickInterceptedException e) {
			log.error("Element click intercepted exception for pay button. Trying js click");
			javaScriptClick(payButton);
		}

		if (waitForNumberOfWindowsToBe(noOfWindows + 1, 5)) {
			log.debug("New window opened after clicking pay button");
			switchToNewWindow(winHandles);

			if (!waitForElement(walletPageLoading, 20, WaitType.invisibilityOfElementLocated)) {
				failure = "New window loading isn't completed";
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(payNow, 10, WaitType.visibilityOfElementLocated)) {
				failure = "Pay Now button isn't present in new window";
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(availableBalanceRazorPay, 2, WaitType.visibilityOfElementLocated)) {
				failure = "Cashback might have been done already";
				reportFailure(failure);
				return failure;
			}

			click(payNow);

			String razorPayPaymentErrorText = "{\"error\":{\"code\":\"BAD_REQUEST_ERROR\",\"description\":\"The id provided does not exist\",\"source\":\"business\",\"step\":\"payment_initiation\",\"reason\":\"input_validation_failed\",\"metadata\":{}}}";
			boolean isCashbackDone = false;

			// checking for error or window is closed for 30 seconds
			log.debug("Start");
			for (int i = 1; i <= 30; i++) {
				try {

					// checking for error
					isCashbackDone = getText(body).contains(razorPayPaymentErrorText);
					if (isCashbackDone) {
						log.debug("Expected error text present in Razor Pay window text for i - " + i
								+ ". Considering cashback done. Text - " + getText(body));
						break;
					}

					// checking for window is closed
					isCashbackDone = getWindowHandles().size() == noOfWindows;
					if (isCashbackDone) {
						log.debug("Window closed for i - " + i + ". Considering cashback done");
						break;
					}
				} catch (Exception e) {
					log.error("Error occurred while waiting for error or window to close", e);
					// checking for window is closed
					isCashbackDone = getWindowHandles().size() == noOfWindows;
					if (isCashbackDone) {
						log.debug("Window closed for i - " + i + " in exception. Considering cashback done");
						break;
					}
				}

				pause(1000);
			}

			log.debug("End");
			if (isCashbackDone) {
				log.debug("Cashback successful");
			} else {
				failure = "Cashback might be unsuccessful. Please check manually";
				reportFailure(failure);
				log.debug("Page text:");
				log.debug(getText(body));
				return failure;
			}
		} else {
			failure = "New window didn't open after clicking Pay button";
			reportFailure(failure);
			return failure;
		}

		if (getWindowHandles().size() > 1) {
			closeAllwindowExcept(globalWinHandle);
		}

		return failure;
	}
}
