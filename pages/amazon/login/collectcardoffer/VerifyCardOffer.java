package amazon.login.collectcardoffer;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class VerifyCardOffer extends CommonFunctions implements VerifyCardOffer_OR {

	public String verifyCardOfferPresent() {

		String failure = "";

		String collectCardOfferURL = Configuration.getProperty("collectCardOfferURL");
		if (collectCardOfferURL == null || collectCardOfferURL.trim().isEmpty()) {
			failure = "Collect Card Offer url not present in config";
			log.error(failure);
			return failure;
		}

		// launch offer url
		if (!launchApplication(collectCardOfferURL)) {
			failure = "Failed to launch Collect Card Offer url - " + collectCardOfferURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		if (!waitForElement(offerPresent, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Offer not present";
			reportFailure(failure);
			return failure;
		}

		failure = "Done - Card offer is present";

		return failure;
	}
}
