package amazon.login.collectbeautyoffer;

import amazon.CommonFunctions;
import framework.constants.WaitType;

public class CollectBeautyOffer extends CommonFunctions implements CollectBeautyOffer_OR {

	public String collectOffer(String offerURL) {

		String failure = "";

		// check if data is provided properly or not
		if (offerURL.trim().isEmpty()) {
			failure = "Data is empty for collect beauty Offer. Offer URL - '" + offerURL + "'";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(offerURL)) {
			failure = "Failed to launch offer url - " + offerURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		if (waitForElement(offerAvailableOrNot, 7, WaitType.visibilityOfElementLocated)) {

			if (isElementDisplayed(collectNowButton)) {
				click(collectNowButton);
				if (!waitForElement(collectNowButton, 5, WaitType.invisibilityOfElementLocated)) {
					failure = "Failed to collect offer";
					reportFailure(failure);
					return failure;
				}
			} else if (isElementDisplayed(buyNowButton)) {
				failure = "Offer collected";
				reportFailure(failure);
				return failure;
			} else {
				failure = "Offer not available";
				log.error(failure);
				return failure;
			}
		} else {
			failure = "Failed to check offer is available or not";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

}
