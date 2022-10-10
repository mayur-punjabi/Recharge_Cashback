package amazon.login.addemail;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class AddEmail extends CommonFunctions implements AddEmail_OR {

	public String addEmail(String email) {

		String failure = "";

		// check if data is provided properly or not
		if (email.trim().isEmpty()) {
			failure = "Data is empty for Add Email. Password - '" + email + "'";
			return failure;
		}

		String accountURL = Configuration.getProperty("accountURL");
		if (accountURL == null || accountURL.trim().isEmpty()) {
			failure = "Account url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(accountURL)) {
			failure = "Failed to launch Account url - " + accountURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(120);

		if (!waitForElement(loginAndSecurityLink, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Login and security link isn't present";
			reportFailure(failure);
			return failure;
		}

		pause(1000);
		click(loginAndSecurityLink);
		waitForPageLoad(120);

		if (!waitForElement(addEmailButton, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Add Email button isn't present";
			reportFailure(failure);
			return failure;
		}

		pause(1000);
		click(addEmailButton);
		waitForPageLoad(120);

		if (!waitForElement(emailInput, 7, WaitType.visibilityOfElementLocated)) {
			failure = "New Email input isn't present";
			reportFailure(failure);
			return failure;
		}

		if (!setValueMultipleTimes(emailInput, email)) {
			failure = "Failed to set new email - " + email;
			reportFailure(failure);
			return failure;
		}

		pause(1000);
		click(continueButton);
		waitForPageLoad(120);

		// handle otp

// verify email added
//		if (waitForElement(passwordChanged, 10, WaitType.visibilityOfElementLocated)) {
//			log.debug("Password changed");
//		} else {
//			failure = "Failed to change password";
//			reportFailure(failure);
//			return failure;
//		}

		return failure;
	}
}
