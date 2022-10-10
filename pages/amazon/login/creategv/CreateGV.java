package amazon.login.creategv;

import java.time.Duration;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class CreateGV extends CommonFunctions implements CreateGV_OR {

	public String createGV(String amount, String quantity, String email, boolean purchase) {

		String failure = "";

		// check if data is provided properly or not
		if (amount.trim().isEmpty() || email.trim().isEmpty() || quantity.trim().isEmpty()) {
			failure = "Data is empty for GV. GV amount - '" + amount + "'. Quantity - '" + quantity + "'. Email - '"
					+ email + "'";
			return failure;
		}

		String createGVURL = Configuration.getProperty("createGVURL");
		if (createGVURL == null || createGVURL.trim().isEmpty()) {
			failure = "Create GV url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(createGVURL)) {
			failure = "Failed to launch create GV url - " + createGVURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		pause(1000);
		if (!waitForElement(amountField, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Amount Field isn't present";
			reportFailure(failure);
			return failure;
		}

		if (!setValueMultipleTimes(amountField, amount)) {
			failure = "Failed to set amount - " + amount;
			reportFailure(failure);
			return failure;
		}

		if (!isElementDisplayed(emailButton)) {
			failure = "Email button not present";
			reportFailure(failure);
			return failure;
		}
		click(emailButton);

		pause(1000);
		if (!waitForElement(emailField, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Email Field isn't present";
			reportFailure(failure);
			return failure;
		}
		jsScrollToElement(emailField);
		if (!setValueMultipleTimes(emailField, email)) {
			failure = "Failed to set email - " + email;
			reportFailure(failure);
			return failure;
		}

		pause(1000);
		if (!waitForElement(quantityField, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Quantity Field isn't present";
			reportFailure(failure);
			return failure;
		}
		jsScrollToElement(quantityField);
		if (!setValueMultipleTimes(quantityField, quantity)) {
			jsScrollToElement(quantityField);
			failure = "Failed to set quantity - " + quantity;
			reportFailure(failure);
			return failure;
		}

		if (purchase) {

			jsScrollToElement(buyNowButton);
			pause(1000);
			click(buyNowButton);
			waitForPageLoad(60);

			return failure;
		} else {
			jsScrollToElement(addToCartButton);
			pause(1000);
			click(addToCartButton);

			waitForPageLoad(60);

			pause(1000);
			if (!waitForElement(addedToCartMessage, 7, WaitType.visibilityOfElementLocated)) {
				failure = "Failed to create GV. Amount - " + amount;
				reportFailure(failure);
				return failure;
			} else {
				log.debug("GV created. Amount - " + amount);
			}
		}

		return failure;
	}

	public String addCard(String cardName, String cardNo, String month, String year, String cvv) {

		String failure = "";

		// click card label
		click(cardLabel);

		// click add card
		click(addCardLink);

		// wait for yellow loading invisibility
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking add card link");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking add card link";
			reportFailure(failure);
			return failure;
		}

		// wait for iframe visibility
		if (!waitForElement(addCardIframe, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Card detail poup isn't present";
			reportFailure(failure);
			return failure;
		}

		// switch to iframe
		switchToFrame(addCardIframe);

		// add card number
		cardNo = cardNo.replaceAll("[^0-9]", "");
		setValue(cardNumberInput, cardNo);

		// add name on card
		setValue(cardNameInput, cardName);

		// select month
		if (month.trim().length() == 1) {
			month = "0" + month;
		}
		click(monthSpan);
		By monthOption = getLocator(monthYearOption, month);
		if (!waitForElement(monthOption, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Month options list didn't open";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(monthOption);
		click(monthOption);
		if (!waitForElement(monthOption, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "Month options list didn't close after selecting - '" + month + "'";
			reportFailure(failure);
			return failure;
		}

		// select year
		click(yearSpan);
		By yearOption = getLocator(monthYearOption, year);
		if (!waitForElement(yearOption, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Year options list didn't open";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(yearOption);
		click(yearOption);
		if (!waitForElement(yearOption, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "Year options list didn't close after selecting - '" + year + "'";
			reportFailure(failure);
			return failure;
		}

		// add cvv
		if (isElementDisplayed(cvvInput)) {
			try {

				WebElement cvvInputElement = getList(cvvInput).stream().filter(cvvInput -> cvvInput.isDisplayed())
						.collect(Collectors.toList()).get(0);
				// clear the text field
				cvvInputElement.clear();

				// Enter keys in the text field
				cvvInputElement.sendKeys(cvv);

				// get the entered value
				String getText = cvvInputElement.getAttribute("value");
				if (getText.equalsIgnoreCase(cvv)) {
					log.debug(cvv + " is entered in '" + cvvInput + "' Text Field");
				} else {
					log.debug(getText + " is entered in '" + cvvInput + "' Text Field instead of " + cvv);
				}
			} catch (WebDriverException e) {
				log.error("Exception occurred in entering " + cvv + " in '" + cvvInput, e);
			}
		}

		// click add card
		click(addCardButton);

		// wait for yellow loading invisibility
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking add card button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking add card button";
			reportFailure(failure);
			return failure;
		}

		// wait for cvv input
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(10)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(cvvInput).stream().anyMatch(cvvInput -> cvvInput.isDisplayed()));
			log.debug("CVV input is present");
		} catch (TimeoutException e) {
			failure = "CVV input isn't present";
			reportFailure(failure);
			return failure;
		}

		try {

			WebElement cvvInputElement = getList(cvvInput).stream().filter(cvvInput -> cvvInput.isDisplayed())
					.collect(Collectors.toList()).get(0);
			// clear the text field
			cvvInputElement.clear();

			// Enter keys in the text field
			cvvInputElement.sendKeys(cvv);

			// get the entered value
			String getText = cvvInputElement.getAttribute("value");
			if (getText.equalsIgnoreCase(cvv)) {
				log.debug(cvv + " is entered in '" + cvvInput + "' Text Field");
			} else {
				log.debug(getText + " is entered in '" + cvvInput + "' Text Field instead of " + cvv);
			}
		} catch (WebDriverException e) {
			log.error("Exception occurred in entering " + cvv + " in '" + cvvInput, e);
		}

		if (!isElementDisplayed(saveCard)) {
			failure = "Failed to save card";
			reportFailure(failure);
			return failure;
		}

		// check save card
		handleCheckbox(saveCard, true);

		if (isElementDisplayed(usePaymentMethod)) {
			click(usePaymentMethod);
		} else if (isElementDisplayed(continueButton)) {
			click(continueButton);
		}
		waitForPageLoad(60);

		return failure;
	}

	public String purchaseGV(String name, String mobile, String pincode, String flat, String area, String pin) {

		String failure = "";

		boolean enterAddress = false;
		boolean handleAddress = true;

		if (waitForElement(nameOrYourAddressOrPlaceOrder, 30, WaitType.visibilityOfElementLocated)) {
			if (isElementDisplayed(nameInput)) {
				enterAddress = true;
			} else if (isElementDisplayed(placeYourOrderAndPay)) {
				handleAddress = false;
			}
		} else {
			failure = "Name input isn't present";
			reportFailure(failure);
			return failure;
		}

		if (handleAddress) {
			if (enterAddress) {
				failure = addAddressDetails(name, mobile, pincode, flat, area);
				if (!failure.isEmpty()) {
					return failure;
				}
			}

			// click add address button
			if (!waitForElement(firstAddOrUseAddress, 2, WaitType.presenceOfElementLocated)) {
				failure = "Add/Use Address button isn't present";
				reportFailure(failure);
				return failure;
			}

			jsScrollToElement(firstAddOrUseAddress);
			if (!waitForElement(firstAddOrUseAddress, 2, WaitType.elementToBeClickable)) {
				failure = "Add/Use Address button isn't clickable";
				reportFailure(failure);
				return failure;
			}
			click(firstAddOrUseAddress);

			// wait for loading
			try {
				pause(3000);
				wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(yellowLoading).stream()
								.allMatch(loading -> !loading.isDisplayed()));
				log.debug("Loading disappeared after clicking Add Address button");
			} catch (TimeoutException e) {
				failure = "Loading didn't complete after clicking Add Address button";
				reportFailure(failure);
				return failure;
			}

			boolean skipSaveAdress = false;

			// click save address button
			if (!isElementDisplayed(saveAddressButton)) {

				if (isElementDisplayed(firstAddOrUseAddress)) {
					log.debug("Use this address apppeared instead of save address");
					skipSaveAdress = true;
				} else if (isElementDisplayed(placeYourOrderButton) || isElementDisplayed(contentNotAvailable)) {
					skipSaveAdress = true;
				} else {
					failure = "Save Address button isn't present";
					reportFailure(failure);
					return failure;
				}
			}

			if (!skipSaveAdress) {
				if (!waitForElement(saveAddressButton, 2, WaitType.elementToBeClickable)) {
					failure = "Save Address button isn't clickable";
					reportFailure(failure);
					return failure;
				}
				click(saveAddressButton);

				// wait for loading
				try {
					pause(3000);
					wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
							.until(driver -> driver.findElements(yellowLoading).stream()
									.allMatch(loading -> !loading.isDisplayed()));
					log.debug("Loading disappeared after clicking Save Address button");
				} catch (TimeoutException e) {
					failure = "Loading didn't complete after clicking Save Address button";
					reportFailure(failure);
					return failure;
				}
			}

			waitForPageLoad(60);

			boolean clickUseThisAddress = true;

			// click use this address button
			if (!waitForElement(firstAddOrUseAddress, 5, WaitType.visibilityOfElementLocated)) {

				if (isElementDisplayed(placeYourOrderButton) || isElementDisplayed(contentNotAvailable)) {
					log.debug("Place your order button displayed instead of use this address button");
					clickUseThisAddress = false;
				} else {
					failure = "Use this address button isn't present";
					reportFailure(failure);
					return failure;
				}
			}

			if (clickUseThisAddress) {
				if (!waitForElement(firstAddOrUseAddress, 2, WaitType.elementToBeClickable)) {
					failure = "Use this address button isn't clickable";
					reportFailure(failure);
					return failure;
				}
				click(firstAddOrUseAddress);

				// wait for loading
				try {
					pause(3000);
					wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
							.until(driver -> driver.findElements(yellowLoading).stream()
									.allMatch(loading -> !loading.isDisplayed()));
					log.debug("Loading disappeared after clicking Use this address button");
				} catch (TimeoutException e) {
					failure = "Loading didn't complete after clicking Use this address button";
					reportFailure(failure);
					return failure;
				}
			}
		}

		if (isElementDisplayed(contentNotAvailable)) {
			driver.navigate().refresh();
			waitForPageLoad(60);
		}

		// click place your order button
		if (!waitForElement(placeYourOrderAndPay, 30, WaitType.visibilityOfElementLocated)) {
			failure = "Place your order button isn't present";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(placeYourOrderAndPay, 2, WaitType.elementToBeClickable)) {
			failure = "Place your order button isn't clickable";
			reportFailure(failure);
			return failure;
		}
		click(placeYourOrderAndPay);

		// wait for loading
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking Place your order button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking Place your order button";
			reportFailure(failure);
			return failure;
		}

		// wait for page load
		waitForPageLoad(120);

		if (isElementDisplayed(contentNotAvailable)) {
			driver.navigate().refresh();
			waitForPageLoad(60);
			click(placeYourOrderAndPay);
		}

		// wait for loading
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking Place your order button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking Place your order button";
			reportFailure(failure);
			return failure;
		}

		// wait for page load
		waitForPageLoad(120);

		// check if otp button is present
		failure = otpRequired();
		if (!failure.isEmpty()) {
			return failure;
		}

		// wait for order placed
		failure = waitForPaymentProcessing(30, false, true, pin);
		if (failure.equals("Recharge was successful")) {
			failure = "";
		}

		return failure;
	}

	public String addNetBanking(String bank, String name, String mobile, String pincode, String flat, String area,
			String pin, String netBankingID, String netBankingPwd, String netBankingTransPwd, String account) {

		String failure = "";

		click(netBankingLabel);

		click(chooseAnOptionButton);
		By bankOptionLoc = getLocator(bankOption, bank);
		if (!waitForElement(bankOptionLoc, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Bank options list didn't open";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(bankOptionLoc);
		click(bankOptionLoc);
		if (!waitForElement(bankOptionLoc, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "Bank options list didn't close after selecting - '" + bank + "'";
			reportFailure(failure);
			return failure;
		}

		if (isElementDisplayed(usePaymentMethod)) {
			click(usePaymentMethod);
		} else if (isElementDisplayed(continueButton)) {
			click(continueButton);
		}

		waitForPageLoad(60);

		boolean enterAddress = false;
		boolean handleAddress = true;

		if (waitForElement(nameOrYourAddressOrPlaceOrder, 30, WaitType.visibilityOfElementLocated)) {
			if (isElementDisplayed(nameInput)) {
				enterAddress = true;
			} else if (isElementDisplayed(placeYourOrderAndPay)) {
				handleAddress = false;
			}
		} else {
			failure = "Name input isn't present";
			reportFailure(failure);
			return failure;
		}

		if (handleAddress) {
			if (enterAddress) {
				failure = addAddressDetails(name, mobile, pincode, flat, area);
				if (!failure.isEmpty()) {
					return failure;
				}
			}

			// click add address button
			if (!waitForElement(firstAddOrUseAddress, 2, WaitType.presenceOfElementLocated)) {
				failure = "Add/Use Address button isn't present";
				reportFailure(failure);
				return failure;
			}

			jsScrollToElement(firstAddOrUseAddress);
			if (!waitForElement(firstAddOrUseAddress, 2, WaitType.elementToBeClickable)) {
				failure = "Add/Use Address button isn't clickable";
				reportFailure(failure);
				return failure;
			}
			click(firstAddOrUseAddress);

			// wait for loading
			try {
				pause(3000);
				wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(yellowLoading).stream()
								.allMatch(loading -> !loading.isDisplayed()));
				log.debug("Loading disappeared after clicking Add Address button");
			} catch (TimeoutException e) {
				failure = "Loading didn't complete after clicking Add Address button";
				reportFailure(failure);
				return failure;
			}

			boolean skipSaveAdress = false;

			// click save address button
			if (!isElementDisplayed(saveAddressButton)) {

				if (isElementDisplayed(firstAddOrUseAddress)) {
					log.debug("Use this address apppeared instead of save address");
					skipSaveAdress = true;
				} else if (isElementDisplayed(placeYourOrderButton) || isElementDisplayed(contentNotAvailable)) {
					skipSaveAdress = true;
				} else {
					failure = "Save Address button isn't present";
					reportFailure(failure);
					return failure;
				}
			}

			if (!skipSaveAdress) {
				if (!waitForElement(saveAddressButton, 2, WaitType.elementToBeClickable)) {
					failure = "Save Address button isn't clickable";
					reportFailure(failure);
					return failure;
				}
				click(saveAddressButton);

				// wait for loading
				try {
					pause(3000);
					wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
							.until(driver -> driver.findElements(yellowLoading).stream()
									.allMatch(loading -> !loading.isDisplayed()));
					log.debug("Loading disappeared after clicking Save Address button");
				} catch (TimeoutException e) {
					failure = "Loading didn't complete after clicking Save Address button";
					reportFailure(failure);
					return failure;
				}
			}

			waitForPageLoad(60);

			boolean clickUseThisAddress = true;

			// click use this address button
			if (!waitForElement(firstAddOrUseAddress, 5, WaitType.visibilityOfElementLocated)) {

				if (isElementDisplayed(placeYourOrderButton) || isElementDisplayed(contentNotAvailable)) {
					log.debug("Place your order button displayed instead of use this address button");
					clickUseThisAddress = false;
				} else {
					failure = "Use this address button isn't present";
					reportFailure(failure);
					return failure;
				}
			}

			if (clickUseThisAddress) {
				if (!waitForElement(firstAddOrUseAddress, 2, WaitType.elementToBeClickable)) {
					failure = "Use this address button isn't clickable";
					reportFailure(failure);
					return failure;
				}
				click(firstAddOrUseAddress);

				// wait for loading
				try {
					pause(3000);
					wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
							.until(driver -> driver.findElements(yellowLoading).stream()
									.allMatch(loading -> !loading.isDisplayed()));
					log.debug("Loading disappeared after clicking Use this address button");
				} catch (TimeoutException e) {
					failure = "Loading didn't complete after clicking Use this address button";
					reportFailure(failure);
					return failure;
				}
			}
		}

		if (isElementDisplayed(contentNotAvailable)) {
			driver.navigate().refresh();
			waitForPageLoad(60);
		}

		// click place your order button
		if (!waitForElement(placeYourOrderAndPay, 15, WaitType.visibilityOfElementLocated)) {
			failure = "Place your order button isn't present";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(placeYourOrderAndPay, 2, WaitType.elementToBeClickable)) {
			failure = "Place your order button isn't clickable";
			reportFailure(failure);
			return failure;
		}
		click(placeYourOrderAndPay);

		// wait for loading
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking Place your order button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking Place your order button";
			reportFailure(failure);
			return failure;
		}

		// wait for page load
		waitForPageLoad(120);

		if (isElementDisplayed(contentNotAvailable)) {
			driver.navigate().refresh();
			waitForPageLoad(60);
			click(placeYourOrderAndPay);
		}

		// wait for loading
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking Place your order button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking Place your order button";
			reportFailure(failure);
			return failure;
		}

		// wait for page load
		waitForPageLoad(120);

		// check if otp button is present
		failure = otpRequired();
		if (!failure.isEmpty()) {
			return failure;
		}

		if (!waitForElement(iAgreeButton, 10, WaitType.visibilityOfElementLocated)) {
			failure = "I Agree button isn't present";
			reportFailure(failure);
			return failure;
		}
		click(iAgreeButton);

		// wait for page load
		waitForPageLoad(120);

		if (!waitForElement(netBankingIDInput, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Net banking login isn't present";
			reportFailure(failure);
			return failure;
		}
		setValue(netBankingIDInput, netBankingID);

		setValue(netBankingPwdInput, netBankingPwd);
		click(netBankingLogin);

		// wait for page load
		waitForPageLoad(120);

		if (!waitForElement(accDropdown, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Acc select dropdown isn't present";
			reportFailure(failure);
			return failure;
		}
		selectDropDownOption(accDropdown, account);

		setValue(transPwdInput, netBankingTransPwd);

		click(submit);

		// wait for page load
		waitForPageLoad(120);

		if (!waitForElement(otpInput, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Otp input isn't present";
			reportFailure(failure);
			return failure;
		}

		int otpWait = 15;
		try {
			otpWait = Integer.parseInt(Configuration.getProperty("otpWait"));
		} catch (Exception e) {
			log.error("Failed to parse otp wait", e);
		}

		if (!waitForElement(otpInput, otpWait, WaitType.invisibilityOfElementLocated)) {
			failure = "Otp isn't entered";
			reportFailure(failure);
			return failure;
		}

		try {
			// wait for page load
			waitForPageLoad(120);
		} catch (Exception e) {
			handleAlert();
		}

		// wait for page load
		waitForPageLoad(120);

		// wait for order placed
		failure = waitForPaymentProcessing(30, false, false, "");
		if (failure.equals("Recharge was successful")) {
			failure = "";
		}

		return failure;
	}
}
