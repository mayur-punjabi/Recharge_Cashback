package amazon.login.recharge;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class Recharge extends CommonFunctions implements Recharge_OR {

	public String launchAndRecharge(String type, String id, String amount, String name, String mobile, String pincode,
			String flat, String area, String password, String gv) {

		String failure = "";

		// check if data is provided properly or not
		if (type.trim().isEmpty() || id.trim().isEmpty() || amount.trim().isEmpty() || name.trim().isEmpty()
				|| mobile.trim().isEmpty() || pincode.trim().isEmpty() || flat.trim().isEmpty()
				|| area.trim().isEmpty()) {
			failure = "Data is empty for recharge. Recharge type - '" + type + "'. Subscriber ID - '" + id
					+ "'. Amount - '" + amount + "'" + "'. No - '" + name + "'" + "'. Phone - '" + mobile + "'"
					+ "'. Pincode - '" + pincode + "'" + "'. Flat - '" + flat + "'" + "'. Area - '" + area + "'";
			return failure;
		}

		// launch recharge url
		failure = launchRechargeURL(type);

		if (!failure.isEmpty()) {
			return failure;
		}

		if (type.equalsIgnoreCase("vi")) {

			// set mobile number
			if (!setValueMultipleTimes(viMobileNumber, id)) {
				failure = "Failed to set mobile number - " + id;
				reportFailure(failure);
				return failure;
			}

			if (!waitForElement(operatorSpan, 7, WaitType.visibilityOfElementLocated)) {
				failure = "Operator selection isn't present";
				reportFailure(failure);
				return failure;
			}

			failure = selectOperator("vi postpaid");

			// wait for amount field
			if (!waitForElement(amountInput, 5, WaitType.visibilityOfElementLocated)) {
				failure = "Amount field not present after entring mobile number - " + id;
				reportFailure(failure);
				return failure;
			}
		} else {

			// set subscriber id
			if (!setValueMultipleTimes(subscriberIDInput, id)) {
				failure = "Failed to set subscriber id - " + id;
				reportFailure(failure);
				return failure;
			}
		}

		if (!waitForElement(amountInput, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Amount field not visible";
			reportFailure(failure);
			return failure;
		}

		// set amount
		if (!setValueMultipleTimes(amountInput, amount)) {
			failure = "Failed to set amount - " + amount;
			reportFailure(failure);
			return failure;
		}

		// entering address and paying
		failure = enterAddressAndPay(amount, name, mobile, pincode, flat, area, password, gv, true);
		if (!failure.isEmpty()) {
			return failure;
		}

		return failure;
	}

	public String launchAndCheckOffer(String type, String checkOffer) {

		String failure = "";

		// check for offer or not
		if (checkOffer == null || checkOffer.trim().isEmpty() || checkOffer.trim().equalsIgnoreCase("yes")) {

			failure = launchRechargeURL(type);
			if (!failure.isEmpty()) {
				return failure;
			}

			// wait for any offer to be visible
			try {
				wait.withTimeout(Duration.ofSeconds(25)).ignoring(StaleElementReferenceException.class).until(
						driver -> driver.findElements(offerAvailable).stream().anyMatch(offer -> offer.isDisplayed()));
				log.debug("Cashback offer is available");
			} catch (TimeoutException e) {
				failure = "Cashback offer isn't available";
				reportFailure(failure);
				return failure;
			}
		} else {
			log.debug("Not checking for offer");
		}

		return failure;
	}

	/**
	 * Launches the url from config according to the type provide
	 * 
	 * @param type allowed values vi, airtel, tatasky
	 * @return failure
	 */
	private String launchRechargeURL(String type) {

		String failure = "";
		String url = "";

		switch (type.toLowerCase().trim()) {
		case "tatasky":
			url = Configuration.getProperty("tataSkyURL");
			break;
		case "vi":
			url = Configuration.getProperty("viURL");
			break;
		case "airtel":
			url = Configuration.getProperty("airtelURL");
			break;
		default:
			failure = "Invalid recharge type. Acceptable values are - tatasky, vi, airtel, electricity bill. Got - "
					+ type;
			log.error(failure);
		}

		if (!failure.isEmpty()) {
			return failure;
		}

		if (url == null || url.trim().isEmpty()) {
			failure = type + " url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(url)) {
			failure = "Failed to launch " + type + " url - " + url;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		return failure;
	}

	private String selectOperator(String operator) {

		String failure = "";

		click(operatorSpan);
		if (!waitForElement(operatorLink, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Operator options list didn't open";
			reportFailure(failure);
			return failure;
		}

		By currentStateOption = getLocator(operatorOption, operator);
		if (!isElementExists(currentStateOption)) {
			failure = "State - '" + operator + "' is not present in the list";
			reportFailure(failure);
			return failure;
		}
		jsScrollToElement(currentStateOption);
		click(currentStateOption);
		if (!waitForElement(operatorLink, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "State options list didn't close after selecting - '" + operator + "'";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}
}
