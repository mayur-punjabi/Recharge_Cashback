package razorpay;

import org.openqa.selenium.By;

public interface RazorPay_OR {

	By razorPayVerification = By.xpath("//img[@id='rzp-logo']");

	By frame = By.className("razorpay-checkout-frame");

	By paymentCompleted = By.xpath("//div[text()='Payment Completed']");

	// By phone = By.name("contact");
	By phone = By.xpath("//input[@name='contact' or @name='phone']");
	By emailField = By.name("email");
	By paymentInParts = By.xpath("//button[.//*[text()='Make payment in parts']]");
	By amountField = By.name("amount");
	By nextButton = By.xpath("//*[@role='button'][./*[@id='footer-cta'][text()='Next']]");

	By wallet = By.xpath("//button[.//div[text()='Wallet']]");
	By amazonPayWallet = By.xpath("//button[.//span[text()='Amazon Pay']]");
	By amazonPayWalletSelected = By.xpath("//button[.//span[text()='Amazon Pay']][contains(@class,'selected')]");
	By payButton = By.xpath(
			"//*[@role='button'][./span[@id='footer-cta'][contains(text(),'Pay')]] | //button[contains(text(),'Pay Now')]");

	By newAmountField = By.xpath("//input[@placeholder='Enter Amount']");
	By newPayButton = By.xpath("//button[text()='Pay']");
	By loading = By.className("razorpay-loader");

	By walletPageLoading = By.xpath("//h2[contains(text(),'Loading wallet page')]");
	By payNow = By.xpath("//input[following-sibling::*[normalize-space()='Pay Now']]");
	By availableBalanceRazorPay = By.xpath("//*[text()='Your available balance']");

	By offer = By.id("promo-details");
}
