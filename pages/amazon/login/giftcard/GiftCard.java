package amazon.login.giftcard;

import amazon.CommonFunctions;
import amazon.login.Login_OR;
import framework.constants.WaitType;
import framework.input.Configuration;

public class GiftCard extends CommonFunctions implements GiftCard_OR {

	public String launchAndAddGiftCard(String giftCard, String password) {

		String failure = "";

		if (giftCard.trim().isEmpty()) {
			failure = "Gift Card is empty.";
			return failure;
		}

		String giftCardURL = Configuration.getProperty("giftCardURL");
		if (giftCardURL == null || giftCardURL.trim().isEmpty()) {
			failure = "Gift card url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(giftCardURL)) {
			failure = "Failed to launch gift card url - " + giftCardURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		if (!waitForElement(giftCardInput, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Gift card field isn't present";
			reportFailure(failure);
			return failure;
		}

		if (!setValueMultipleTimes(giftCardInput, giftCard)) {
			failure = "Failed to set gift card";
			reportFailure(failure);
			return failure;
		}

		if (!isElementDisplayed(addToYourBalance)) {
			failure = "Add to your balance button isn't present";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(addToYourBalance, 5, WaitType.elementToBeClickable)) {
			failure = "Add to your balance button isn't clickable";
			reportFailure(failure);
			return failure;
		}

		click(addToYourBalance);
		waitForPageLoad(60);

		if (waitForElement(Login_OR.passwordField, 5, WaitType.visibilityOfElementLocated)) {

			setValue(Login_OR.passwordField, password);

			if (!isElementDisplayed(Login_OR.signInButton)) {
				failure = "Sign in button isn't present after entering password";
				reportFailure(failure);
				return failure;
			}
			click(Login_OR.signInButton);
			waitForPageLoad(60);
		} else {
			log.debug("Password field isn't present after adding gift card");
		}

		if (waitForElement(giftCardAdded, 5, WaitType.visibilityOfElementLocated)) {
			log.debug("Gift card - " + giftCard + " added");
		} else if (waitForElement(giftCardUsed, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Gift Card - " + giftCard + " has already been used";
			reportFailure(failure);
			return failure;
		} else {
			failure = "Failed to add gift card - " + giftCard;
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

}
