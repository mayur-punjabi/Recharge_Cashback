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

}
