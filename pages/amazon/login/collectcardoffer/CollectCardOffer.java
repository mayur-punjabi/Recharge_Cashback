package amazon.login.collectcardoffer;

import java.time.Duration;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import framework.constants.WaitType;
import framework.input.Configuration;

public class CollectCardOffer extends VerifyCardOffer implements CollectCardOffer_OR {

	public String collectCardOffer(String cardNo, String cardName, String month, String year, String cvv, String email,
			String pin) {

		String failure = "";

		// check if data is provided properly or not
		if (cardNo.trim().isEmpty() || cardName.trim().isEmpty() || month.trim().isEmpty() || year.trim().isEmpty()
				|| cvv.trim().isEmpty() || email.trim().isEmpty() || pin.trim().isEmpty()) {
			failure = "Data is empty for collecting card offer. Card number - '" + cardNo + "'. Card name - '"
					+ cardName + "'. Month - '" + month + "'" + "'. Year - '" + year + "'" + "'. CVV - '" + cvv + "'"
					+ "'. Email - '" + email + "'" + "'. Pin - '" + pin + "'";
			return failure;
		}

		failure = verifyCardOfferPresent();
		if (!failure.isEmpty() && !failure.contains("Done -")) {
			return failure;
		}
		// reset failure if failure contains Done
		failure = "";

		click(offerPresent);
		waitForPageLoad(60);

		if (!waitForElement(cardLabel, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Add card not present";
			reportFailure(failure);
			return failure;
		}

		failure = addCard(cardNo, cardName, month, year, cvv);
		if (!failure.isEmpty()) {
			return failure;
		}

		click(payNowButton);

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

		// wait for amazon loading/please wait
		waitForPageLoad(120);
		if (!waitForElement(amazonImgBlackLoading, 120, WaitType.invisibilityOfElementLocated)) {
			failure = "Loading didn't complete after clicking Continue to Pay button/Place and order button";
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(120);

		// wait for otp input
		if (!waitForElement(otpInput, 30, WaitType.visibilityOfElementLocated)) {
			failure = "OTP input isn't present";
			reportFailure(failure);
			return failure;
		}

		// default otp wait
		int otpWaitTime = 60;

		String otpWait = Configuration.getProperty("otpWait");
		try {
			if (otpWait == null || otpWait.trim().isEmpty()) {
				log.error("OTP wait not provided");
			} else {
				otpWaitTime = Integer.parseInt(otpWait.trim());
			}
		} catch (Exception e) {
			log.error("Failed to parse OTP wait", e);
		}

		// wait for otp to be filled
		if (!waitForElement(otpInput, otpWaitTime, WaitType.invisibilityOfElementLocated)) {
			failure = "OTP not entered";
			reportFailure(failure);
			return failure;
		}

		// wait for amazon loading/please wait
		waitForPageLoad(120);
		if (!waitForElement(amazonImgBlackLoading, 120, WaitType.invisibilityOfElementLocated)) {
			failure = "Loading didn't complete after clicking Continue to Pay button/Place and order button";
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(120);

		if (waitForElement(cardAddedSuccessfully, 30, WaitType.visibilityOfElementLocated)) {
			log.debug("Card added successfully");
		} else {
			failure = "Failed to add card";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

	private String addCard(String cardNo, String cardName, String month, String year, String cvv) {

		String failure = "";

		// click card label
		click(cardLabel);

		// add card number
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

		return failure;
	}

}
