package amazon.login.changepassword;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class ChangePassword extends CommonFunctions implements ChangePassword_OR {

	public String changePassword(String password, String newPassword) {

		String failure = "";

		// check if data is provided properly or not
		if (password.trim().isEmpty() || newPassword.trim().isEmpty()) {
			failure = "Data is empty for Change Password. Password - '" + password + "'. New Password - '" + newPassword
					+ "'";
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

		if (!waitForElement(passwordEditButton, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Edit password button isn't present";
			reportFailure(failure);
			return failure;
		}

		pause(1000);
		click(passwordEditButton);
		waitForPageLoad(120);

		if (!waitForElement(currentPasswordInput, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Current Password Field isn't present";
			reportFailure(failure);
			return failure;
		}

		if (!setValueMultipleTimes(currentPasswordInput, password)) {
			failure = "Failed to set current password - " + password;
			reportFailure(failure);
			return failure;
		}

		if (!isElementDisplayed(newPasswordInput)) {
			failure = "New Password Field isn't present";
			reportFailure(failure);
			return failure;
		}

		if (!setValueMultipleTimes(newPasswordInput, newPassword)) {
			failure = "Failed to set new password - " + newPassword;
			reportFailure(failure);
			return failure;
		}

		if (!isElementDisplayed(newPasswordCheckInput)) {
			failure = "Reenter new password Field isn't present";
			reportFailure(failure);
			return failure;
		}

		if (!setValueMultipleTimes(newPasswordCheckInput, newPassword)) {
			failure = "Failed to set reenter new password - " + newPassword;
			reportFailure(failure);
			return failure;
		}

		pause(1000);
		click(saveChangesButton);
		waitForPageLoad(120);

		if (waitForElement(passwordChanged, 10, WaitType.visibilityOfElementLocated)) {
			log.debug("Password changed");
		} else {
			failure = "Failed to change password";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}
}
