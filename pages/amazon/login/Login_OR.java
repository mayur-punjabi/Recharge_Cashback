package amazon.login;

import org.openqa.selenium.By;

public interface Login_OR {

	By signInLink = By.xpath("//a[normalize-space(text())='Sign in'] | //span[text()='Sign in']");

	By emailOrPhone = By
			.xpath("//input[@name='email' or @name='emailLogin'][not(./ancestor::form[contains(@id,'register')])]");
	By signInWithPassword = By
			.xpath("(//input[following-sibling::*[normalize-space()='Sign-In with your password']])[last()]");
	By continueButton = By.xpath("//*[@id='continue'][not(./ancestor::form[contains(@id,'register')])]");
	By passwordField = By.xpath(
			"//input[@name='password' or @name='passwordLogin'][not(./ancestor::form[contains(@id,'register')])]");
	By captchaImg = By.id("auth-captcha-image");
	By captchaInput = By.xpath("//input[@placeholder='Type the characters above']");
	By signInButton = By.id("signInSubmit");
	By incorrectPhoneNo = By.xpath("//*[normalize-space(text())='Incorrect phone number']");
	By incorrectEmail = By.xpath("//*[normalize-space(text())='We cannot find an account with that email address']");
	By incorrectPassword = By.xpath("//*[normalize-space(text())='Your password is incorrect']");

	By clearCookies = By.xpath(
			"//h4[normalize-space(text())='Enter the characters you see'] | //input[@placeholder='Type the characters above']");
	By signInSpan = By.xpath("//span[@class='action-inner'][text()='Sign in']");
	By sightImpaired = By.xpath("//a[contains(text(),'sight impaired')]");

	By otp = By.xpath("(//input[following-sibling::*[normalize-space()='Get OTP']])[last()]");
	By storeOTP = By.id("auth-mfa-otpcode");
	By mfaSignInButton = By.xpath("//input[./following-sibling::*[contains(text(),'Sign In')]]");
	By approveNotification = By.xpath("//span[contains(text(),'approve the notification')]");

	By storeSignInButton = By.xpath("//a[.='Sign in']");

	// TOTP
	By secretKeyInput = By.xpath("//input[contains(@placeholder,'secret key')]");
	By token = By.id("token");
	By oldToken = By.xpath("//p[@id='token'][not(text()='%s')]");
}
