package amazon.login.creategv;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class CreateGV extends CommonFunctions implements CreateGV_OR {

	public String createGV(String amount, String quantity, String email) {

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
			failure = "Failed to set quantity - " + quantity;
			reportFailure(failure);
			return failure;
		}

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

		return failure;
	}
}
