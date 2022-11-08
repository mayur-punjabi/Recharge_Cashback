package amazon.login.orderproduct;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import framework.constants.WaitType;
import framework.input.Configuration;

public class OrderFresh extends OrderProduct implements OrderFresh_OR {

	public String launchAndOrderProduct(String name, String mobile, String pincode, String flat, String area,
			String gv) {

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

		failure = addLocation(name, mobile, pincode, flat, area);
		if (!failure.isEmpty()) {
			return failure;
		}

		for (int i = 0; i < links.size(); i++) {

			String link = links.get(i);
			String quantity = quantities.get(i);

			launchApplication(link);
			waitForPageLoad(120);

			if (!waitForElement(freshItemOrOption, 5, WaitType.visibilityOfElementLocated)) {
				failure = "Fresh option not present";
				reportFailure(failure);
				return failure;
			}

			if (isElementDisplayed(freshOption)) {
				click(freshOption);
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

			By currentQuantity = getLocator(quantityOption, quantity);
			if (isElementExists(currentQuantity)) {
				javaScriptClick(currentQuantity);
			} else {
				failure = "Quantity - " + quantity + " isn't present";
				reportFailure(failure);
				return failure;
			}

			if (isElementDisplayed(done)) {
				javaScriptClick(done);
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
			click(addToCart);
		}

		// close shop fresh popup
		if (waitForElement(closeIcon, 5, WaitType.visibilityOfElementLocated)) {
			click(closeIcon);
		}

		javaScriptClick(cart);
		waitForPageLoad(120);

		// check all items added or not
		if (!waitForElement(cartItems, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Failed to open cart";
			reportFailure(failure);
			return failure;
		}

		int cartCount = getList(cartItems).size();
		if (cartCount != links.size()) {
			failure = "Failed to add all items in cart. Cart items count - " + cartCount;
			reportFailure(failure);
			return failure;
		}

		if (!isElementDisplayed(buyFreshItems)) {
			failure = "Buy fresh items button not present";
			reportFailure(failure);
			return failure;
		}
		click(buyFreshItems);
		waitForPageLoad(120);

		if (!waitForElement(continueButton, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Continue button not present";
			reportFailure(failure);
			return failure;
		}
		click(continueButton);
		waitForPageLoad(120);

		if (!waitForElement(continueButton2, 5, WaitType.visibilityOfElementLocated)) {
			failure = "2nd Continue button not present";
			reportFailure(failure);
			return failure;
		}
		click(continueButton2);
		waitForPageLoad(120);

		if (!gv.trim().toLowerCase().equals("skip")) {
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

			if (isElementDisplayed(continueButton2)) {
				click(continueButton2);
				waitForPageLoad(120);
			}

			if (isElementDisplayed(useThisPaymentMethod)) {
				click(useThisPaymentMethod);
			}
		} else {
			log.debug("GV was skipped");
		}

		if (!waitForElement(placeYourOrderAndPayOrPlaceYourOrder, 30, WaitType.visibilityOfElementLocated)) {
			failure = "Place your order button not present";
			reportFailure(failure);
			return failure;
		}
		pause(2000);
		jsScrollToElement(placeYourOrderAndPayOrPlaceYourOrder);
		pause(3000);
		jsScrollToElement(placeYourOrderAndPayOrPlaceYourOrder);
		waitForElement(placeYourOrderAndPayOrPlaceYourOrder, 5, WaitType.elementToBeClickable);
		click(placeYourOrderAndPayOrPlaceYourOrder);

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

		if (!waitForElement(freshOrderPlaced, 30, WaitType.visibilityOfElementLocated)) {
			failure = "Failed to order product";
			reportFailure(failure);
			return failure;
		}

		String deliveryDateTime = getText(deliveryDateTimeLoc);
		failure = "Done - " + deliveryDateTime;

		return failure;
	}

	public String addLocation(String name, String mobile, String pincode, String flat, String area) {

		String failure = "";

		javaScriptClick(addLocation);

		if (!waitForElement(manageAddress, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Add address popup not present";
			reportFailure(failure);
			return failure;
		}

		By existingAddress = getLocator(addressExist, name, pincode, flat, area);
		if (isElementDisplayed(existingAddress)) {
			log.debug("Address exist");
			click(existingAddress);
			if (!waitForElement(manageAddress, 10, WaitType.invisibilityOfElementLocated)) {
				failure = "Failed to select address";
				reportFailure(failure);
				return failure;
			}
			return failure;
		}

		click(manageAddress);
		waitForPageLoad(120);

		if (!waitForElement(addNewAddress, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Add new address link not present";
			reportFailure(failure);
			return failure;
		}
		click(addNewAddress);
		waitForPageLoad(120);

		failure = addAddressDetails(name, mobile, pincode, flat, area);
		if (!failure.isEmpty()) {
			return failure;
		}

		click(addOrUseAddress);
		waitForPageLoad(120);

		if (!waitForElement(addressSaved, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Failed to add address";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

}
