package amazon;

import org.openqa.selenium.By;

public interface Shared_OR {

	By body = By.tagName("body");

	By cart = By.xpath("//*[contains(@class,'cart')]");

	// enterAddressAndPay()
	By amountInput = By.name("amount");
	By continueToPayButton = By.xpath("//button[./*[contains(text(),'Continue to Pay')]]");

	By availableBalance = By.xpath(
			"(//span[contains(normalize-space(.),'Use') and contains(normalize-space(.),'your') and contains(normalize-space(.),'Amazon Pay balance')])[last()]");
	By enoughBalance = By.xpath("//*[contains(@class,'order-total-currency')][contains(text(),' 0')]");
	By amazonPayLabel = By
			.xpath("//span[contains(text(),'Amazon Pay balance')]//ancestor::div[@class='a-radio']//label");

	By cardLabel = By.xpath("//input[@value='SelectableAddCreditCard']//parent::label");
	By addCardLink = By.xpath("//a[text()='Add a credit or debit card']");
	By addCardIframe = By.xpath("//iframe[@name='ApxSecureIframe']");
	By cardNumberInput = By.xpath("//input[@name='addCreditCardNumber']");
	By cardNameInput = By.xpath("//input[contains(@name,'accountHolderName')]");
	By monthSpan = By
			.xpath("//select[contains(@name,'month')]//following-sibling::span[contains(@class,'button-dropdown')]");
	By yearSpan = By
			.xpath("//select[contains(@name,'year')]//following-sibling::span[contains(@class,'button-dropdown')]");
	By monthYearOption = By.xpath("//li[contains(@class,'dropdown-item')]/a[text()='%s']");
	By defaultPayment = By.xpath("//input[contains(@name,'setBuyingPreference')]");
	By addCardButton = By.xpath("//span[contains(@class,'button-input')][.//span[text()='Add your card']]");
	By cvvInput = By.xpath("//input[contains(@name,'addCreditCardVerificationNumber')][@type='password']");
	By saveCard = By.xpath("//input[@type='checkbox'][./following-sibling::*[contains(text(),'RBI')]]");
	By enterCardDetailsEveryTime = By.xpath("//span[text()='I will enter details every time']/parent::span");
	By continueWithoutSavingCard = By.xpath("//span[text()='Continue without saving card']/parent::span");
	By enterOTPOrPassword = By.xpath("//a[contains(normalize-space(text()),'OTP or password')]");
	By pinInput = By.xpath("//input[@name='IPIN' or @name='txtPassword']");
	By pinOrOTPOrPassword = By.xpath(
			"//a[contains(normalize-space(text()),'OTP or password')] | //input[@name='IPIN' or @name='txtPassword']");
	By pinSubmit = By.xpath("//input[@type='submit']");

	By placeOrderAndPay = By.xpath("(//input[following-sibling::*[normalize-space()='Place Order and Pay']])[last()]");

	By sendOTPButton = By.xpath("//input[following-sibling::*[normalize-space()='Send OTP']]");

	By nameInput = By.xpath("//input[contains(@id,'enterAddressFullName')]");
	By mobileInput = By.xpath("//input[contains(@id,'enterAddressPhoneNumber')]");
	By pincodeInput = By.xpath("//input[contains(@id,'enterAddressPostalCode')]");
	By flatInput = By.xpath("//input[contains(@id,'enterAddressLine1')]");
	By areaInput = By.xpath("//input[contains(@id,'enterAddressLine2')]");
	By addressStateSpan = By.xpath("//span[contains(@id,'enterAddressStateOrRegion')]");
	By currentStateSpan = By.xpath("//span[contains(@id,'enterAddressStateOrRegion')][normalize-space()='%s']");
	By addressStateOption = By.xpath("//li[@role='option']/a[text()='%s']");
	By cityInput = By.xpath("//input[contains(@id,'enterAddressCity')]");
	By saveAddressButton = By.xpath("(//input[following-sibling::*[normalize-space()='Save Address']])[last()]");
	By useThisAddressButton = By.xpath("//a[contains(text(),'Use this address')]");
	By addOrUseAddress = By.xpath(
			"(//input[following-sibling::*[normalize-space()='Add address']])[last()] | (//input[following-sibling::*[normalize-space()='Use this address']])[last()]");

	By placeYourOrderButton = By.xpath("//input[contains(@name,'placeYourOrder1')]");

	By amazonImgBlackLoading = By.id("redirect-spinner-overlay");
	By blackLoading = By.id("pageWaitSpinner");
	By yellowLoading = By.xpath("//img[contains(@src,'loading')]");
	By yellowLoading2 = By.xpath("//img[contains(@src,'Spinner')]");

	// waitForPaymentProcessing()
	By rechargeSuccessful = By.xpath(
			"//h4[contains(text(),'Your')][contains(text(),'recharge is successful') or contains(text(),'is successful')]");
	By rechargePending = By
			.xpath("//h4[contains(text(),'Your')][contains(text(),'is pending') or contains(text(),'within')]");

	By offerAvailable = By.xpath("//*[normalize-space(text())='Offers']");

	By loginAndSecurityLink = By.xpath("//a[contains(normalize-space(.),'security')]");
}
