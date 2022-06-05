package amazon.login;

import org.openqa.selenium.By;

public interface Login_OR {

	By emailOrPhone = By.name("email");
	By continueButton = By.id("continue");
	By passwordField = By.name("password");
	By signInButton = By.id("signInSubmit");
	By incorrectPhoneNo = By.xpath("//*[normalize-space(text())='Incorrect phone number']");
	By incorrectPassword = By.xpath("//*[normalize-space(text())='Your password is incorrect']");

	By clearCookies = By.xpath("//h4[normalize-space(text())='Enter the characters you see']");
	By signInSpan = By.xpath("//span[@class='action-inner'][text()='Sign in']");
	By sightImpaired = By.xpath("//a[contains(text(),'sight impaired')]");
}
