package amazon.login;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.WindowType;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.Normal;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class Login extends CommonFunctions implements Login_OR {

	public String launchAndLogin(String phoneOrEmail, String password) {

		String failure = "";

		String solveCaptcha = Configuration.getProperty("solveCaptcha");
		boolean handleCaptcha = solveCaptcha != null && solveCaptcha.trim().equalsIgnoreCase("yes");

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

		if (waitForElement(signInLink, 5, WaitType.visibilityOfElementLocated)) {
			click(signInLink);
			waitForPageLoad(60);
		}

		if (!waitForElement(emailOrPhone, 7, WaitType.visibilityOfElementLocated)) {
			failure = "Phone field isn't present. Amazon sign in url might be wrong";
			reportFailure(failure);
			return failure;
		}

		setValue(emailOrPhone, phoneOrEmail);

		// setting password if displayed for e.g. in case of store login else setting it
		// in next screen
		if (isElementDisplayed(passwordField)) {

			setValue(passwordField, password);

			if (!isElementDisplayed(signInButton)) {
				failure = "Sign in button isn't present after entering email and password";
				reportFailure(failure);
				return failure;
			}

			click(signInButton);
			waitForPageLoad(60);

			if (waitForElement(incorrectEmail, 5, WaitType.visibilityOfElementLocated)) {
				failure = "Incorrect Email";
				reportFailure(failure);
				return failure;
			}

			if (isElementDisplayed(incorrectPassword)) {
				failure = "Incorrect password";
				reportFailure(failure);
				return failure;
			}

			if (!isElementDisplayed(passwordField)) {

				if (waitForElement(cart, 5, WaitType.visibilityOfElementLocated)) {
					log.debug("Amazon login successful");
					return failure;
				} else if (isElementDisplayed(clearCookies)) {
					log.debug("Captcha present instead of second password field");
				} else {
					failure = "Second Password field isn't present. Email or password might be wrong";
					reportFailure(failure);
					return failure;
				}
			}

			if (waitForElement(clearCookies, 2, WaitType.visibilityOfElementLocated)) {

				if (handleCaptcha) {

					if (isElementDisplayed(passwordField)) {
						setValue(passwordField, password);
					}

//					pause(10000);
					solveCaptcha();

					if (isElementDisplayed(signInButton)) {
						click(signInButton);
					} else if (isElementDisplayed(continueButton)) {
						click(continueButton);

						waitForPageLoad(60);
						if (isElementDisplayed(signInWithPassword)) {
							click(signInWithPassword);
						}
						waitForPageLoad(60);
					}
					waitForPageLoad(60);

				} else {
					failure = "Clear cookies";
					reportFailure(failure);
					return failure;
				}
			}
		} else {

			boolean clickContinue = true;
			waitForPageLoad(60);
			if (isElementDisplayed(signInWithPassword)) {
				click(signInWithPassword);
				clickContinue = false;
			}
			waitForPageLoad(60);

			if (clickContinue) {
				if (!isElementDisplayed(continueButton)) {
					failure = "Continue button isn't present after entering phone number";
					reportFailure(failure);
					return failure;
				}
				click(continueButton);
				waitForPageLoad(60);

				if (isElementDisplayed(signInWithPassword)) {
					click(signInWithPassword);
					clickContinue = false;
				}
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
			}
		}

		waitForPageLoad(60);
		if (waitForElement(cart, 5, WaitType.visibilityOfElementLocated)) {
			log.debug("Amazon login successful");
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

		for (int i = 0; i < 2; i++) {
			if (waitForElement(clearCookies, 5, WaitType.visibilityOfElementLocated)) {

				if (handleCaptcha) {

					if (isElementDisplayed(passwordField)) {
						setValue(passwordField, password);
					}

//					pause(10000);
					solveCaptcha();

					if (isElementDisplayed(signInButton)) {
						click(signInButton);
					} else if (isElementDisplayed(continueButton)) {
						click(continueButton);
					}
					waitForPageLoad(60);

					// setting password if displayed
					if (waitForElement(passwordField, 5, WaitType.visibilityOfElementLocated)) {

						setValue(passwordField, password);

						if (!isElementDisplayed(signInButton)) {
							failure = "Sign in button isn't present after entering email and password";
							reportFailure(failure);
							return failure;
						}

						click(signInButton);
						waitForPageLoad(60);
					}

				} else {
					failure = "Clear cookies";
					reportFailure(failure);
					return failure;
				}
			} else {
				break;
			}
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

	public String launchAndLoginStore(String phoneOrEmail, String password, String secret) {

		String failure = "";

		String amazonURL = Configuration.getProperty("amazonStoreSignInURL");
		if (amazonURL == null || amazonURL.trim().isEmpty()) {
			failure = "Amazon store sign in url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(amazonURL)) {
			failure = "Failed to launch amazon store sign in url - " + amazonURL;
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
		pause(500);
		setValue(passwordField, password);
		pause(1000);
		click(signInButton);
		waitForPageLoad(60);

		if (waitForElement(emailOrPhone, 5, WaitType.visibilityOfElementLocated)) {
			setValue(emailOrPhone, phoneOrEmail);
			pause(500);
			setValue(passwordField, password);
			pause(500);
			click(signInButton);
			waitForPageLoad(60);
		}

		if (waitForElement(incorrectPhoneNo, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Incorrect phone number";
			reportFailure(failure);
			return failure;
		}

		if (waitForElement(incorrectPassword, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Incorrect password";
			reportFailure(failure);
			return failure;
		}

		if (waitForElement(clearCookies, 2, WaitType.visibilityOfElementLocated)) {

			pause(30000);

			setValue(passwordField, password);

			click(signInButton);
			waitForPageLoad(60);

			// setting password if displayed
			if (waitForElement(passwordField, 5, WaitType.visibilityOfElementLocated)) {

				setValue(passwordField, password);

				if (!isElementDisplayed(signInButton)) {
					failure = "Sign in button isn't present after entering email and password";
					reportFailure(failure);
					return failure;
				}

				click(signInButton);
				waitForPageLoad(60);
			}

			if (waitForElement(clearCookies, 2, WaitType.visibilityOfElementLocated)) {
				failure = "Clear cookies";
				reportFailure(failure);
				return failure;
			}
		}

		if (waitForElement(otp, 2, WaitType.visibilityOfElementLocated)) {
			failure = "Mobile OTP required for store login";
			reportFailure(failure);
			return failure;
		}

		if (waitForElement(storeOTP, 5, WaitType.visibilityOfElementLocated)) {
			failure = enterOTP(secret);
			closeAllwindowExcept(globalWinHandle);
			if (!failure.isEmpty() && !failure.contains("token - ")) {
				return failure;
			}
			setValue(storeOTP, failure.replace("token - ", ""));
			waitForElement(mfaSignInButton, 5, WaitType.elementToBeClickable);
			javaScriptClick(mfaSignInButton);
			waitForPageLoad(60);
			failure = "";
		}

		if (waitForElement(approveNotification, 5, WaitType.visibilityOfElementLocated)) {

			log.debug("Approve notification is present");

			if (!waitForElement(approveNotification, 60, WaitType.invisibilityOfElementLocated)) {
				failure = "Notification not approved";
				reportFailure(failure);
				return failure;
			}

			waitForPageLoad(60);
			if (waitForElement(passwordField, 5, WaitType.visibilityOfElementLocated)) {
				setValue(passwordField, password);

				click(signInButton);
				waitForPageLoad(60);

				if (waitForElement(clearCookies, 2, WaitType.visibilityOfElementLocated)) {
					// wait to solve captcha
					pause(30000);

					setValue(passwordField, password);

					click(signInButton);
					waitForPageLoad(60);
				}

				if (waitForElement(otp, 2, WaitType.visibilityOfElementLocated)) {
					failure = "Mobile OTP required for store login";
					reportFailure(failure);
					return failure;
				}
			}

		} else if (!isElementDisplayed(cart)) {
			failure = "Approve notification not present or Amazon login failed";
			reportFailure(failure);
			return failure;
		}

		waitForPageLoad(60);

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

	public void solveCaptcha() {

		for (int i = 0; i < 3; i++) {
			try {
				String encodedCaptcha = Base64
						.encodeBase64String(Shutterbug.shootElement(driver, getWebElement(captchaImg)).getBytes());
				log.debug("Captcha - " + encodedCaptcha);

				String captchaKey = Configuration.getProperty("twoCaptchaKey");
				if (captchaKey == null) {
					captchaKey = "";
				}

				TwoCaptcha solver = new TwoCaptcha(captchaKey);
				Normal captcha = new Normal();
				captcha.setBase64(encodedCaptcha);
				solver.solve(captcha);
				String captchaSolution = captcha.getCode();
				log.debug("Captcha solution - " + captchaSolution);

				setValue(captchaInput, captchaSolution);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Failed to solve captcha", e);
				pause(2000);
			}
		}
	}

	public String enterOTP(String secret) {

		String failure = "";

		String totpURL = Configuration.getProperty("totpURL");
		if (totpURL == null || totpURL.trim().isEmpty()) {
			failure = "TOTP URL not present in config";
			reportFailure(failure);
			return failure;
		}

		driver.switchTo().newWindow(WindowType.TAB);
		if (!launchApplication(totpURL)) {
			failure = "Failed to launch TOTP URL";
			reportFailure(failure);
			return failure;
		}

		if (!waitForElement(secretKeyInput, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Secret key input not present";
			reportFailure(failure);
			return failure;
		}

		String tokenValue = getText(token);

		setValue(secretKeyInput, secret);

		if (!waitForElement(getLocator(oldToken, tokenValue), 35, WaitType.visibilityOfElementLocated)) {
			failure = "Token didn't update";
			reportFailure(failure);
			return failure;
		}

		failure = "token - " + getText(token);
		log.debug(failure);
		return failure;
	}
}
