package amazon.login.amazonpay;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class AmazonPay extends CommonFunctions implements AmazonPay_OR {

	public String getBalance() {

		String failure = "";

		String amazonPayURL = Configuration.getProperty("amazonPayURL");
		if (amazonPayURL == null || amazonPayURL.trim().isEmpty()) {
			failure = "Amazon Pay url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(amazonPayURL)) {
			failure = "Failed to launch Amazon Pay url - " + amazonPayURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		if (!waitForElement(amazonPayBalance, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Amazon pay balance isn't present";
			reportFailure(failure);
			return failure;
		}

		String amount = getText(amazonPayBalance).trim().replace("₹", "").replace("\\u20A8", "");
		log.debug("Amount - " + amount);

		double amountValue = 0;
		try {
			amountValue = Double.parseDouble(amount);
			amount = String.valueOf(amountValue);
			if (amountValue == 0) {
				failure = "Cash back amount is zero";
				reportFailure(failure);
				return failure;
			}
		} catch (NumberFormatException e) {
			log.error("Error occurred while getting amount value - " + amount, e);

			if (amount.length() > 0) {
				amount = amount.substring(1);

				try {
					amountValue = Double.parseDouble(amount);

					if (amountValue == 0) {
						failure = "Cash back amount is zero";
						reportFailure(failure);
						return failure;
					}
				} catch (NumberFormatException e1) {
					log.error(
							"Error occurred while getting amount value - " + amount + " after removing first character",
							e1);
				}
			}
		}

		failure = "Done - " + amount;

		return failure;
	}
}
