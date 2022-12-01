package amazon.login.orderproduct;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import framework.constants.WaitType;
import framework.input.Configuration;

public class OrderBeauty extends OrderProduct implements OrderBeauty_OR {

	public String launchAndOrderProduct(String name, String mobile, String pincode, String flat, String area,
			String subscribe, String gv) {

		String failure = "";

		failure = deleteCartItems();
		if (!failure.isEmpty()) {
			return failure;
		}

		List<String> quantities = new ArrayList<>();
		List<String> links = new ArrayList<>();

		try {
			quantities = Arrays.asList(Configuration.getProperty("quantities").split(",")).stream()
					.filter(link -> !link.trim().isEmpty()).collect(Collectors.toList());
			links = Arrays.asList(Configuration.getProperty("productlinks").split(",")).stream()
					.filter(link -> !link.trim().isEmpty()).collect(Collectors.toList());

			if (quantities.isEmpty() || links.isEmpty()) {
				failure = "links or quantities are empty";
				reportFailure(failure);
				return failure;
			}

			if (quantities.size() != links.size()) {
				failure = "links or quantities are not equal";
				reportFailure(failure);
				return failure;
			}

		} catch (Exception e) {
			failure = "Failed to get links and quantities from config";
			reportFailure(failure);
			return failure;
		}

		for (int i = 0; i < links.size(); i++) {

			String link = links.get(i);
			String quantity = quantities.get(i);
			int itemCount = i + 1;

			launchApplication(link);
			waitForPageLoad(120);

			if (!waitForElement(quantityButton, 5, WaitType.visibilityOfElementLocated)) {
				failure = "Quantity button not present";
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(quantityButton, 10, WaitType.elementToBeClickable)) {
				failure = "Quantity button not clickable";
				reportFailure(failure);
				return failure;
			}
			click(quantityButton);

			// get the displayed quantity list
			try {
				wait.withTimeout(Duration.ofSeconds(10)).ignoring(StaleElementReferenceException.class).until(
						driver -> driver.findElements(quantityList).stream().anyMatch(list -> list.isDisplayed()));
				log.debug("Quantity options list opened");
			} catch (TimeoutException e) {
				failure = "Quantity options list didn't open";
				reportFailure(failure);
				return failure;
			}

			Optional<WebElement> currentQuantity = getList(quantityOptions).stream()
					.filter(option -> option.getText().trim().equals(quantity)).findFirst();
			if (currentQuantity.isPresent()) {
				pause(1500);
				click(currentQuantity.get());
			} else {
				failure = "Quantity - " + quantity + " isn't present";
				reportFailure(failure);
				return failure;
			}

			// wait for all quantity lists to close
			try {
				wait.withTimeout(Duration.ofSeconds(10)).ignoring(StaleElementReferenceException.class).until(
						driver -> driver.findElements(quantityList).stream().allMatch(list -> !list.isDisplayed()));
				log.debug("Quantity options list closed");
			} catch (TimeoutException e) {
				failure = "Quantity options list didn't close";
				reportFailure(failure);
				return failure;
			}

			if (!isElementDisplayed(addToCart)) {
				failure = "Add to cart button not present";
				log.error(failure);
				return failure;
			}

			pause(1500);
			waitForElement(addToCart, 5, WaitType.elementToBeClickable);
			jsScrollToElement(addToCart);
			javaScriptClick(addToCart);
			waitForPageLoad(120);

			// wait for all items to add in cart
			try {
				wait.withTimeout(Duration.ofSeconds(10)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(cartItems).size() == itemCount);
				log.debug("Item added");
			} catch (TimeoutException e) {
				failure = "Failed to add item to cart";
				reportFailure(failure);
				return failure;
			}

		}

		if (subscribe.trim().equalsIgnoreCase("yes")) {

			int subscribeAndSaveCount = getList(getLocator(subscribeAndSave, "")).size();

			if (subscribeAndSaveCount != links.size()) {
				failure = "Subscribe and save not present for some products";
				reportFailure(failure);
				return failure;
			}

			for (int i = 0; i < subscribeAndSaveCount; i++) {

				int itemCount = i + 1;

				click(getLocator(subscribeAndSave, "[" + (i + 1) + "]"));
				if (!waitForElement(subscribeButton, 10, WaitType.visibilityOfElementLocated)) {
					failure = "Subscribe button not present";
					reportFailure(failure);
					return failure;
				}
				click(subscribeButton);
				if (!waitForElement(subscribeButton, 30, WaitType.invisibilityOfElementLocated)) {
					failure = "Subscribe button didn't disappear";
					reportFailure(failure);
					return failure;
				}
			}
		} else {
			log.debug("Don't subscribe - " + subscribe);
		}

		if (!waitForElement(proceedToBuy, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Proceed to buy not present not present";
			reportFailure(failure);
			return failure;
		}
		click(proceedToBuy);
		waitForPageLoad(120);

		if (waitForElement(addMobileNo, 5, WaitType.visibilityOfElementLocated)) {
			String bypassURL = Configuration.getProperty("bypassURL");
			launchApplication(bypassURL);
			waitForPageLoad(120);
		} else {
			log.debug("Add mobile no didn't appear");
		}

		if (waitForElement(addNewAddress, 5, WaitType.visibilityOfElementLocated)) {
			click(addNewAddress);

			// wait for yellow loading invisibility
			try {
				pause(3000);
				wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(yellowLoading).stream()
								.allMatch(loading -> !loading.isDisplayed()));
				log.debug("Loading disappeared after clicking apply button");
			} catch (TimeoutException e) {
				failure = "Loading didn't complete after clicking add new address button";
				reportFailure(failure);
				return failure;
			}
		} else {
			log.debug("Add new address not present");
		}

		failure = addAddressDetails(name, mobile, pincode, flat, area);
		if (!failure.isEmpty()) {
			return failure;
		}

		click(addOrUseAddress);

		// wait for yellow loading invisibility
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking apply button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking use this address button";
			reportFailure(failure);
			return failure;
		}

		if (gv.trim().toLowerCase().equals("store")) {

			log.debug("store");

			if (!waitForElement(payAtStoreOrLink, 5, WaitType.visibilityOfElementLocated)) {
				failure = "Pay at store or Pay through link not present";
				reportFailure(failure);
				return failure;
			}
			javaScriptClick(payAtStoreOrLink);

		} else if (gv.contains("@")) {

			log.debug("upi");

			failure = upi(gv);
			if (!failure.isEmpty()) {
				return failure;
			}

		} else if (gv.trim().toLowerCase().equals("cod")) {

			log.debug("cod");

			click(codLabel);
		} else if (gv.trim().toLowerCase().equals("skip")) {
			log.debug("Skip gv");
		} else {

			log.debug("gv");

			if (!waitForElement(addGVorEnterCodeInput, 10, WaitType.visibilityOfElementLocated)) {
				failure = "Add gift card link or Enter code input not present";
				reportFailure(failure);
				return failure;
			}

			if (isElementDisplayed(addGV)) {
				waitForElement(addGV, 5, WaitType.elementToBeClickable);
				javaScriptClick(addGV);
			}
			if (!waitForElement(enterCodeInput, 10, WaitType.visibilityOfElementLocated)) {
				failure = "Enter code input not present";
				reportFailure(failure);
				return failure;
			}
			setValue(enterCodeInput, gv);
			click(applyButton);

			// wait for yellow loading invisibility
			try {
				pause(3000);
				wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(yellowLoading).stream()
								.allMatch(loading -> !loading.isDisplayed()));
				log.debug("Loading disappeared after clicking apply button");
			} catch (TimeoutException e) {
				failure = "Loading didn't complete after clicking apply button";
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(gvRedeemed, 10, WaitType.presenceOfElementLocated)) {
				failure = "Failed to redeem gv";
				reportFailure(failure);
				return failure;
			}
		}

		if (isElementDisplayed(continueButton2)) {
			click(continueButton2);
			waitForPageLoad(120);

			if (waitForElement(continueButton2, 5, WaitType.visibilityOfElementLocated)) {
				click(continueButton2);

				// wait for yellow loading invisibility
				try {
					pause(3000);
					wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
							.until(driver -> driver.findElements(yellowLoading).stream()
									.allMatch(loading -> !loading.isDisplayed()));
					log.debug("Loading disappeared after clicking apply button");
				} catch (TimeoutException e) {
					failure = "Loading didn't complete after clicking apply button";
					reportFailure(failure);
					return failure;
				}

				waitForPageLoad(120);

				if (waitForElement(continueButton, 5, WaitType.visibilityOfElementLocated)) {
					click(continueButton);
				} else {
					log.debug("Continue button not present 3rd time");
				}
			} else {
				log.debug("Continue button not present 2nd time");
			}
		}

		if (gv.contains("@")) {
			log.debug("Skipping place your order button click  in case of upi id");
		} else {

			if (!waitForElement(placeYourOrderAndPayOrPlaceYourOrder, 30, WaitType.visibilityOfElementLocated)) {
				failure = "Place your order button not present";
				reportFailure(failure);
				return failure;
			}
			waitForElement(placeYourOrderAndPayOrPlaceYourOrder, 5, WaitType.elementToBeClickable);
			click(placeYourOrderAndPayOrPlaceYourOrder);
		}

		// wait for yellow loading invisibility
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking place your order button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking place your order button";
			reportFailure(failure);
			return failure;
		}

		waitForPageLoad(120);

		if (gv.trim().toLowerCase().equals("store")) {
			if (!waitForElement(tokenNumber, 30, WaitType.visibilityOfElementLocated)) {
				failure = "Failed to order product";
				reportFailure(failure);
				return failure;
			}

			failure = "Done - " + getText(tokenNumber) + " ";
		} else {
			if (!waitForElement(orderPlaced, 30, WaitType.visibilityOfElementLocated)) {
				failure = "Failed to order product";
				reportFailure(failure);
				return failure;
			}
		}

		if (!failure.contains("Done - ")) {
			failure = "Done - ";
		}
		String deliveryDateTime = getText(deliveryDateTimeLoc);
		failure += deliveryDateTime;

		return failure;
	}

}
