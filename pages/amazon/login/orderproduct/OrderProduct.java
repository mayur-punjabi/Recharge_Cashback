package amazon.login.orderproduct;

import java.time.Duration;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import amazon.CommonFunctions;
import framework.constants.WaitType;

public class OrderProduct extends CommonFunctions implements OrderProduct_OR {

	public String deleteCartItems() {

		String failure = "";

		javaScriptClick(cart);
		waitForPageLoad(60);

		if (!waitForElement(collapsedItemsOrCartItems, 5, WaitType.visibilityOfElementLocated)) {
			log.debug("No items found in cart");
			return failure;
		}

		if (isElementDisplayed(collapsedItemsList)) {
			click(collapsedItemsList);
		}

		if (!waitForElement(cartItems, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Failed to open cart";
			reportFailure(failure);
			return failure;
		}

		int cartItemsCount = getList(cartItems).size();
		log.debug("Items to delete - " + cartItemsCount);
		for (int i = 1; i <= cartItemsCount; i++) {
			click(deleteButton);
			final int count = i;

			// get the displayed quantity list
			try {
				wait.withTimeout(Duration.ofSeconds(20)).ignoring(StaleElementReferenceException.class)
						.until(driver -> getList(deleteButton).size() == cartItemsCount - count);
				log.debug("Item deleted");
			} catch (TimeoutException e) {
				failure = "Failed to delete item";
				reportFailure(failure);
				return failure;
			}
		}

		return failure;
	}

	public String upi(String upiId) {

		String failure = "";

		click(upiLabel);

		if (!waitForElement(upiInput, 5, WaitType.visibilityOfElementLocated)) {
			failure = "UPI input not present after selecting UPI payment";
			reportFailure(failure);
			return failure;
		}

		setValue(upiInput, upiId);
		click(verifyButton);

		// wait for yellow loading invisibility
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking verify button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking verify button";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(upiVerified, 15, WaitType.visibilityOfElementLocated)) {
			failure = "UPI isn't verified";
			reportFailure(failure);
			return failure;
		}

		click(placeYourOrderAndPayOrPlaceYourOrder);

		// wait for yellow loading invisibility
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking verify button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking verify button";
			reportFailure(failure);
			return failure;
		}

		waitForPageLoad(120);

		// wait for upi approve
		if (!waitForElement(completeUPIPayment, 5, WaitType.visibilityOfElementLocated)) {
			log.debug("Complete your payment message not present");
		}

		if (!waitForElement(completeUPIPayment, 15, WaitType.invisibilityOfElementLocated)) {
			failure = "UPI payment not completed within 15 seconds";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}
}
