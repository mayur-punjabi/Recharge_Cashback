package amazon.login.orderproduct;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class OrderProduct extends CommonFunctions implements OrderProduct_OR {

	public String launchAndOrderProduct(String subscribe, String quantity, String name, String mobile, String pincode,
			String flat, String area, String cashback) {

		String failure = "";

		// check if data is provided properly or not
		if (subscribe.trim().isEmpty() || quantity.trim().isEmpty() || name.trim().isEmpty() || mobile.trim().isEmpty()
				|| pincode.trim().isEmpty() || flat.trim().isEmpty() || area.trim().isEmpty()
				|| cashback.trim().isEmpty()) {
			failure = "Data is empty for ordering product. Subscribe - '" + subscribe + "'." + "'. Name - '" + name
					+ "'. No - '" + mobile + "'" + "'. Pincode - '" + pincode + "'" + "'. Flat - '" + flat + "'"
					+ "'. Area - '" + area + "'. Cashback - '" + cashback + "'. Quantity - '" + quantity + "'";
			return failure;
		}

		boolean subscribeOrNot = subscribe.trim().equalsIgnoreCase("yes");
		boolean cashbackCheck = cashback.trim().equalsIgnoreCase("yes");

		String collectOfferURL = Configuration.getProperty("collectOfferURL");
		if (collectOfferURL == null || collectOfferURL.trim().isEmpty()) {
			failure = "Collect offer url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(collectOfferURL)) {
			failure = "Failed to launch collect offer url - " + collectOfferURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(120);

		if (!waitForElement(collectNow, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Collect Now button not present";
			log.error(failure);
			return failure;
		}

		click(collectNow);

		if (!waitForElement(buyNow, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Failed to collect offer";
			log.error(failure);
			return failure;
		}

		String productURL = Configuration.getProperty("productURL");
		if (productURL == null || productURL.trim().isEmpty()) {
			failure = "Product url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(productURL)) {
			failure = "Failed to launch product url - " + productURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(120);

		if (!waitForElement(oneTimePurchase, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Product not available";
			log.error(failure);
			return failure;
		}

		if (isElementDisplayed(anotherWayToBuy)) {
			failure = "Another way to buy is present";
			log.error(failure);
			return failure;
		}

		if (subscribeOrNot && !isElementDisplayed(subscribeAndSave)) {
			failure = "Subscribe and save not present";
			log.error(failure);
			return failure;
		}

		click(quantitySpan);
		if (!waitForElement(quantityList, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Quantity options list didn't open";
			reportFailure(failure);
			return failure;
		}

		By currentQuantity = getLocator(quantityOption, quantity);
		if (!isElementDisplayed(currentQuantity)) {
			failure = "Quantity - '" + quantity + "' is not present in the list";
			reportFailure(failure);
			return failure;
		}
		jsScrollToElement(currentQuantity);
		click(currentQuantity);
		if (!waitForElement(quantityList, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "Quantity options list didn't close after selecting - '" + quantity + "'";
			reportFailure(failure);
			return failure;
		}

		if (!isElementDisplayed(addToCart)) {
			failure = "Add to cart button not present";
			log.error(failure);
			return failure;
		}

		click(addToCart);
		waitForPageLoad(120);

		if (subscribeOrNot) {

			click(subscribeAndSaveLink);
			if (!waitForElement(subscribeButton, 10, WaitType.visibilityOfElementLocated)) {
				failure = "Subscribe button not present";
				reportFailure(failure);
				return failure;
			}

			click(subscribeButton);

			// wait for loading
			try {
				pause(2000);
				wait.withTimeout(Duration.ofSeconds(60)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(blackLoading2).stream()
								.allMatch(loading -> !loading.isDisplayed()));
				log.debug("Loading disappeared after clicking Subscribe button");
			} catch (TimeoutException e) {
				failure = "Loading didn't complete after clicking Subscribe button";
				reportFailure(failure);
				return failure;
			}
		}

		click(proceedToBuy);
		waitForPageLoad(120);

		String bypassURL = Configuration.getProperty("bypassURL");
		if (bypassURL == null || bypassURL.trim().isEmpty()) {
			failure = "Bypass url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(bypassURL)) {
			failure = "Failed to launch bypass url - " + bypassURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(120);

		if (!waitForElement(nameInput, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Name field not present after clicking Proceed to buy button";
			reportFailure(failure);
			return failure;
		}

		failure = addAddressDetails(name, mobile, pincode, flat, area);
		if (!failure.isEmpty()) {
			return failure;
		}

		// click add address button
		if (!waitForElement(addOrUseAddress, 2, WaitType.presenceOfElementLocated)) {
			failure = "Add/Use Address button isn't present";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(addOrUseAddress);
		if (!waitForElement(addOrUseAddress, 2, WaitType.elementToBeClickable)) {
			failure = "Add/Use Address button isn't clickable";
			reportFailure(failure);
			return failure;
		}
		click(addOrUseAddress);

		// wait for loading
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking Add Address button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking Add Address button";
			reportFailure(failure);
			return failure;
		}

		boolean skipSaveAdress = false;

		// click save address button
		if (!isElementDisplayed(saveAddressButton)) {

			if (isElementDisplayed(useThisAddressButton)) {
				log.debug("Use this address apppeared instead of save address");
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

		waitForPageLoad(120);

		boolean clickUseThisAddress = true;

		// click use this address button
		if (!waitForElement(useThisAddressButton, 5, WaitType.visibilityOfElementLocated)) {

			if (isElementDisplayed(continueButton)) {
				log.debug("Continue button displayed instead of use this address button");
				clickUseThisAddress = false;
			} else {
				failure = "Use this address button isn't present";
				reportFailure(failure);
				return failure;
			}
		}

		if (clickUseThisAddress) {
			if (!waitForElement(useThisAddressButton, 2, WaitType.elementToBeClickable)) {
				failure = "Use this address button isn't clickable";
				reportFailure(failure);
				return failure;
			}
			click(useThisAddressButton);

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

		waitForPageLoad(120);

		if (!waitForElement(payAtStore, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Pay at store not present";
			reportFailure(failure);
			return failure;
		}

		click(payAtStore);

		click(continueButton);

		// wait for loading
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking Continue button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking Continue button";
			reportFailure(failure);
			return failure;
		}

		waitForPageLoad(120);

		if (cashbackCheck && !isElementDisplayed(cashBackPresent)) {
			failure = "Cashback not present";
			reportFailure(failure);
			return failure;
		}

		// click place your order button
		if (!isElementDisplayed(placeYourOrderButton)) {
			failure = "Place your order button isn't present";
			reportFailure(failure);
			return failure;
		}
		if (!waitForElement(placeYourOrderButton, 2, WaitType.elementToBeClickable)) {
			failure = "Place your order button isn't clickable";
			reportFailure(failure);
			return failure;
		}
		click(placeYourOrderButton);

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

		if (!waitForElement(tokenNumber, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Token number not generated";
			reportFailure(failure);
			return failure;
		}

		failure = "Done - " + getText(tokenNumber);

		return failure;
	}

}
