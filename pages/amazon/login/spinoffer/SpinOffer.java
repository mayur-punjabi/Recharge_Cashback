package amazon.login.spinoffer;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class SpinOffer extends CommonFunctions implements SpinOffer_OR {

	public String collectSpinOffer() {

		String failure = "";

		String spinOfferURL = Configuration.getProperty("spinOfferURL");
		if (spinOfferURL == null || spinOfferURL.trim().isEmpty()) {
			failure = "Spin Offer url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(spinOfferURL)) {
			failure = "Failed to launch Spin Offer url - " + spinOfferURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		pause(1000);
		if (!waitForElement(spinBanner, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Spin Now isn't present";
			reportFailure(failure);
			return failure;
		}

		click(spinBanner);

		boolean handleSpin = true;
		if (!waitForElement(spinButton, 7, WaitType.visibilityOfElementLocated)) {
			if (isElementDisplayed(answer) || waitForElement(collectOffer, 5, WaitType.visibilityOfElementLocated)) {
				handleSpin = false;
			} else {
				failure = "Spin button isn't present";
				reportFailure(failure);
				return failure;
			}
		}

		if (handleSpin) {
			click(spinButton);

			if (!waitForElement(claimYourPrizeButton, 30, WaitType.visibilityOfElementLocated)) {
				failure = "Claim your prize button isn't present";
				reportFailure(failure);
				return failure;
			}
			click(claimYourPrizeButton);

			if (!waitForElement(answerQuestionButton, 30, WaitType.visibilityOfElementLocated)) {
				failure = "Answer question button isn't present";
				reportFailure(failure);
				return failure;
			}
			click(answerQuestionButton);

			if (!waitForElement(answer, 30, WaitType.visibilityOfElementLocated)) {
				failure = "Answer 30 isn't present";
				reportFailure(failure);
				return failure;
			}
		}

		if (isElementDisplayed(answer)) {
			click(answer);
		}

		waitForPageLoad(60);

		if (!waitForElement(collectOffer, 30, WaitType.visibilityOfElementLocated)) {
			failure = "Collect offer isn't present";
			reportFailure(failure);
			return failure;
		}

		String offerText = getText(offer);
		log.debug("Offer text - " + offerText);

		String offerValue = offerText.replaceAll("[^0-9]", "");
		failure = "Done - " + offerValue;

		click(collectOffer);

		return failure;
	}
}
