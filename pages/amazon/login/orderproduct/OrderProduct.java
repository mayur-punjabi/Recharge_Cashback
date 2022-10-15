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

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class OrderProduct extends CommonFunctions implements OrderProduct_OR {

	public String launchAndOrderProduct(String gv) {

		String failure = "";

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

			if (!waitForElement(freshItemOrOption, 5, WaitType.visibilityOfElementLocated)) {
				failure = "Fresh option not present";
				reportFailure(failure);
				return failure;
			}

			if (isElementDisplayed(freshOption)) {
				click(freshOption);
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
				click(currentQuantity.get());
			} else {
				failure = "Quantity - " + quantity + " isn't present";
				reportFailure(failure);
				return failure;
			}

			if (isElementDisplayed(done)) {
				click(done);
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

			// wait for all items to add in cart
			try {
				wait.withTimeout(Duration.ofSeconds(10)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(cartItems).size() == itemCount);
				log.debug("All items added");
			} catch (TimeoutException e) {
				failure = "Failed to add item to cart";
				reportFailure(failure);
				return failure;
			}

		}

		javaScriptClick(cart);
		waitForPageLoad(120);

		if (!waitForElement(buyFreshItems, 5, WaitType.visibilityOfElementLocated)) {
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
				click(addGV);
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

		if (!waitForElement(placeYourOrderButton, 30, WaitType.visibilityOfElementLocated)) {
			failure = "Place your order button not present";
			reportFailure(failure);
			return failure;
		}
		waitForElement(placeYourOrderButton, 5, WaitType.elementToBeClickable);
		jsScrollToElement(placeYourOrderButton);
		javaScriptClick(placeYourOrderButton);

		// wait for yellow loading invisibility
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking apply button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking apply button";
			reportFailure(failure);
			return failure;
		}

		waitForPageLoad(120);

		if (!waitForElement(freshOrderPlaced, 30, WaitType.visibilityOfElementLocated)) {
			failure = "Failed to order product";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

}
