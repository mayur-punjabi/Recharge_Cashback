package amazon.login.addmoney;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class AddMoney extends CommonFunctions implements AddMoney_OR {

	public String addMoney(String voterID, String amount, String name, String mobile, String pincode, String flat,
			String area, String password, String gv) {
		String failure = "";

		// check if data is provided properly or not
		if (voterID.trim().isEmpty() || amount.trim().isEmpty() || name.trim().isEmpty() || mobile.trim().isEmpty()
				|| pincode.trim().isEmpty() || flat.trim().isEmpty() || area.trim().isEmpty()) {
			failure = "Data is empty for recharge. Voter ID - '" + voterID + "'. Amount - '" + amount + "'"
					+ "'. Name - '" + name + "'" + "'. Phone - '" + mobile + "'" + "'. Pincode - '" + pincode + "'"
					+ "'. Flat - '" + flat + "'" + "'. Area - '" + area + "'";
			return failure;
		}

		String addMoneyURL = Configuration.getProperty("addMoneyURL");
		if (addMoneyURL == null || addMoneyURL.trim().isEmpty()) {
			failure = "Add Money url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(addMoneyURL)) {
			failure = "Failed to launch Add Money url - " + addMoneyURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(120);

		if (!waitForElement(idOrAmountInput, 20, WaitType.visibilityOfElementLocated)) {
			failure = "Neither id nor amount field is present";
			reportFailure(failure);
			return failure;

		}

		failure = updateName(name);
		if (!failure.isEmpty()) {
			return failure;
		}

		if (isElementDisplayed(idInput)) {
			jsScrollToElement(idInput);

			click(selectIDSpan);
			if (!waitForElement(voterIDOption, 5, WaitType.visibilityOfElementLocated)) {
				failure = "Voter ID option is not visible";
				reportFailure(failure);
				return failure;
			}

			javaScriptClick(voterIDOption);
			waitForElement(voterIDOption, 5, WaitType.invisibilityOfElementLocated);

			if (!setValueMultipleTimes(idInput, voterID)) {
				failure = "Failed to set voter id - " + voterID;
				reportFailure(failure);
				return failure;
			}

			click(continueButton);
			waitForPageLoad(120);

			waitForElement(yellowLoading, 30, WaitType.visibilityOfElementLocated);
			if (!waitForElement(yellowLoading, 120, WaitType.invisibilityOfElementLocated)) {
				failure = "Loading didn't complete after entring voter id details";
				reportFailure(failure);
				return failure;
			}
		} else {
			log.debug("ID input not present");
		}

		waitForPageLoad(120);

		if (!waitForElement(amountInput, 30, WaitType.visibilityOfElementLocated)) {
			failure = "Amount field is not present";
			reportFailure(failure);
			return failure;
		}

		if (!setValueMultipleTimes(amountInput, amount)) {
			failure = "Failed to set amount - " + amount;
			reportFailure(failure);
			return failure;
		}

		submitForm(addMoneyForm);
		if (!waitForElement(blackLoading, 120, WaitType.invisibilityOfElementLocated)) {
			failure = "Loading didn't complete after clicking add money to balance button";
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(120);

		failure = enterAddressAndPay(amount, name, mobile, pincode, flat, area, password, gv, false);
		if (!failure.isEmpty()) {
			return failure;
		}

		return failure;
	}

	public String updateName(String name) {

		String failure = "";

		if (isElementDisplayed(incorrectName)) {
			log.debug("Incorrect name");
			click(editName);
			waitForPageLoad(120);

			if (!waitForElement(newNameInput, 20, WaitType.visibilityOfElementLocated)) {
				failure = "New name input not present";
				reportFailure(failure);
				return failure;
			}

			if (!setValueMultipleTimes(newNameInput, name)) {
				failure = "Failed to set new name - " + name;
				reportFailure(failure);
				return failure;
			}

			click(saveChangesButton);
			waitForPageLoad(120);

			if (!waitForElement(nameChangeSuccess, 20, WaitType.visibilityOfElementLocated)) {
				failure = "Failed to change the name";
				reportFailure(failure);
				return failure;
			}
		}

		return failure;
	}

}
