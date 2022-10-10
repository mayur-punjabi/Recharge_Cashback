package amazon.login.creategv;

import org.openqa.selenium.By;

public interface CreateGV_OR {

	By amountField = By.xpath("//input[contains(@id,'custom-amount')]");
	By emailButton = By.xpath("//button[normalize-space()='Email']");
	By emailField = By.xpath("//textarea[contains(@id,'recipients')]");
	By quantityField = By.xpath("//input[contains(@id,'quantity')][not(@type='hidden')]");
	By addToCartButton = By.xpath("(//input[contains(@name,'add-to-cart')][not(@type='hidden')])[1]");
	By addedToCartMessage = By.xpath("//span[normalize-space()='Added to Cart']");
	By buyNowButton = By.xpath("(//input[contains(@name,'buy-now')][not(@type='hidden')])[1]");

	By nameOrYourAddressOrPlaceOrder = By.xpath(
			"//input[contains(@id,'enterAddressFullName')] | //span[normalize-space(text())='Your addresses'] | //input[following-sibling::*[normalize-space()='Place Your Order and Pay']] | //input[contains(@name,'placeYourOrder1')]");
	By placeYourOrderAndPay = By.xpath(
			"//input[following-sibling::*[normalize-space()='Place Your Order and Pay']] | //input[contains(@name,'placeYourOrder1')]");
	By usePaymentMethod = By.xpath("//input[following-sibling::*[normalize-space()='Use this payment method']]");
	By continueButton = By.xpath("//input[following-sibling::*[normalize-space()='Continue']]");
	By firstAddOrUseAddress = By.xpath(
			"//input[following-sibling::*[normalize-space()='Add address']][not(@type='hidden')] | //input[following-sibling::*[normalize-space()='Use this address']][not(@type='hidden')] | //input[following-sibling::*[normalize-space()='Add address']][not(@type='hidden')] | //input[following-sibling::*[normalize-space()='Use this address']][not(@type='hidden')] | //a[normalize-space(text())='Use this address']");

	By contentNotAvailable = By.xpath("//div[text()='Sorry, content is not available.']");

	// net banking
	By iAgreeButton = By.xpath("//a[text()='I AGREE']");
	By netBankingIDInput = By.id("uid");
	By netBankingPwdInput = By.id("pwd");
	By netBankingLogin = By.xpath("//a[text()='LOGIN']");
	By accDropdown = By.xpath("//select[@id='fromAcc']");
	By transPwdInput = By.id("password");
	By submit = By.xpath("//a[text()='SUBMIT']");
	By otpInput = By.id("otp");
}
