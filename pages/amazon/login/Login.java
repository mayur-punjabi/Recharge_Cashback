package amazon.login;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class Login extends CommonFunctions implements Login_OR {

	public String launchAndLogin(String phoneOrEmail, String password) {

		String failure = "";

		String amazonURL = Configuration.getProperty("amazonSignInURL");
		if (amazonURL == null || amazonURL.trim().isEmpty()) {
			failure = "Amazon sign in url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(amazonURL)) {
			failure = "Failed to launch amazon sign in url - " + amazonURL;
			reportFailure(failure);
			return failure;
		}

		waitForPageLoad(60);

		if (!waitForElement(emailOrPhone, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Phone field isn't present. Amazon sign in url might be wrong";
			reportFailure(failure);
			return failure;
		}

		setValue(emailOrPhone, phoneOrEmail);

		if (!isElementDisplayed(continueButton)) {
			failure = "Continue button isn't present after entering phone number";
			reportFailure(failure);
			return failure;
		}
		click(continueButton);
		waitForPageLoad(60);

		if (waitForElement(incorrectPhoneNo, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Incorrect phone number";
			reportFailure(failure);
			return failure;
		}

		if (!isElementDisplayed(passwordField)) {
			failure = "Password field isn't present. Phone number might be wrong";
			reportFailure(failure);
			return failure;
		}
		setValue(passwordField, password);

		if (!isElementDisplayed(signInButton)) {
			failure = "Sign in button isn't present after entering password";
			reportFailure(failure);
			return failure;
		}
		click(signInButton);
		waitForPageLoad(60);

		if (waitForElement(incorrectPassword, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Incorrect password";
			reportFailure(failure);
			return failure;
		}

		if (waitForElement(clearCookies, 2, WaitType.visibilityOfElementLocated)) {
			failure = "Clear cookies";
			reportFailure(failure);
			return failure;
		}

		if (waitForElement(cart, 5, WaitType.visibilityOfElementLocated)) {
			log.debug("Amazon login successful");
		} else {
			failure = "Amazon login failed";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

	/**
	 * Tries sight impaired option
	 * 
	 * @return failure
	 */
	public void trySightImpaired() {

		if (isElementDisplayed(sightImpaired)) {
			click(sightImpaired);
			waitForPageLoad(60);
			if (waitForElement(signInSpan, 5, WaitType.visibilityOfElementLocated)) {
				click(signInSpan);
			} else {
				log.error("Sign in span not visible after clicking sight impaired");
			}
		} else {
			log.error("Sight impaired link not present");

		}
	}
}
