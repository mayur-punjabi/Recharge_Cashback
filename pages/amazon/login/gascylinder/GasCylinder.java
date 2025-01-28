package amazon.login.gascylinder;

import org.openqa.selenium.By;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class GasCylinder extends CommonFunctions implements GasCylinder_OR {

	public String launchAndOrderGasCylinder(String provider, String id, String amount, String name, String mobile,
			String pincode, String flat, String area, String password, String gv) {

		String failure = "";

		// check if data is provided properly or not
		if (provider.trim().isEmpty() || id.trim().isEmpty() || amount.trim().isEmpty() || name.trim().isEmpty()
				|| mobile.trim().isEmpty() || pincode.trim().isEmpty() || flat.trim().isEmpty()
				|| area.trim().isEmpty()) {
			failure = "Data is empty for recharge. Gas Cylinder provider - '" + provider + "'. Subscriber ID - '" + id
					+ "'. Amount - '" + amount + "'" + "'. No - '" + name + "'" + "'. Phone - '" + mobile + "'"
					+ "'. Pincode - '" + pincode + "'" + "'. Flat - '" + flat + "'" + "'. Area - '" + area + "'";
			return failure;
		}

		if (!id.contains("'")) {
			failure = "' not present in the subscriber id";
			log.error(failure);
			return failure;
		}

		String gasCylinderBillURL = Configuration.getProperty("gasCylinderBillURL");
		if (gasCylinderBillURL == null || gasCylinderBillURL.trim().isEmpty()) {
			failure = "Gas Cylinder url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(gasCylinderBillURL)) {
			failure = "Failed to launch Gas Cylinder url - " + gasCylinderBillURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		if (!waitForElement(providerSpan, 7, WaitType.visibilityOfElementLocated)) {
			failure = "State selection isn't present";
			reportFailure(failure);
			return failure;
		}

		String providerToSelect = "";
		if (provider.toLowerCase().contains("bharat")) {
			providerToSelect = "bharat gas";
		} else if (provider.toLowerCase().contains("hp")) {
			providerToSelect = "hp gas";
		} else if (provider.toLowerCase().contains("indane")) {
			providerToSelect = "indane gas";
		} else {
			failure = "Invalid provider provided. Valid providers - bharat, hp, indane";
			log.error(failure);
			return failure;
		}

		failure = selectProvider(providerToSelect);
		if (!failure.isEmpty()) {
			return failure;
		}

		if (!waitForElement(idInput, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Id field isn't present";
			reportFailure(failure);
			return failure;
		}

		// set id
		if (!setValueMultipleTimes(idInput, id)) {
			failure = "Failed to set subscriber id - " + id;
			reportFailure(failure);
			return failure;
		}

//		if (isElementDisplayed(fetchBillButton)) {
//			failure = fetchBill();
//			if (!failure.isEmpty()) {
//				return failure;
//			}
//		} else {
//			log.debug("Fetch bill button not present");
//
//			// set amount
//			if (!setValueMultipleTimes(amountInput, amount)) {
//				failure = "Failed to set amount - " + amount;
//				reportFailure(failure);
//				return failure;
//			}
//		}

		// entering address and paying
		failure = enterAddressAndPay(amount, name, mobile, pincode, flat, area, password, gv, true);
		if (!failure.isEmpty()) {
			return failure;
		}

		return failure;
	}

	private String selectProvider(String provider) {

		String failure = "";

		click(providerSpan);
		if (!waitForElement(providerListLink, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Provider options list didn't open";
			reportFailure(failure);
			return failure;
		}

		By currentStateOption = getLocator(providerOption, provider);
		if (!isElementExists(currentStateOption)) {
			failure = "Provider - '" + provider + "' is not present in the list";
			reportFailure(failure);
			return failure;
		}
		jsScrollToElement(currentStateOption);
		click(currentStateOption);
		if (!waitForElement(providerListLink, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "Provider options list didn't close after selecting - '" + provider + "'";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

}
