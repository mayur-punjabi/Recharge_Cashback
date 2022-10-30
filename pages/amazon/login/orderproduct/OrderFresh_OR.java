package amazon.login.orderproduct;

import org.openqa.selenium.By;

public interface OrderFresh_OR {

	By collectNow = By.xpath("//button[normalize-space(text())='Collect Now']");
	By buyNow = By.xpath("//a[normalize-space(text())='Buy Now']");

	By oneTimePurchase = By.xpath("//span[contains(text(),'One-time purchase')]//ancestor::a");
	By subscribeAndSave = By.xpath("//span[contains(text(),'Subscribe & Save')]//ancestor::a");
	By anotherWayToBuy = By.xpath("//span[contains(text(),'Another way to buy')]//ancestor::a");
	By addToCart = By.xpath(
			"(//input[contains(@aria-labelledby,'fresh')][following-sibling::*[normalize-space()='Add to Cart']])");
	By cartItems = By.xpath("//div[contains(@class,'active-cart--selected')]/*[contains(@class,'item')]");
	By quantityButton = By.xpath("//button[contains(text(),'Qty')]");
	By quantityList = By.xpath("//ul[contains(@class,'list-link')]");
	By quantityOptions = By.xpath("//ul[contains(@class,'list-link')]//li");
	By done = By.xpath("//ul[contains(@class,'list-link')]//ancestor::div//span[text()='Done']");

	By subscribeAndSaveLink = By.xpath("//a[contains(text(),'Subscribe & Save')]");
	By subscribeButton = By.xpath("(//input[following-sibling::*[normalize-space()='Subscribe']])[last()]");

	By proceedToBuy = By.xpath("//input[@name='proceedToRetailCheckout']/following-sibling::span");
	By payAtStore = By.xpath("//span[contains(text(),'Pay at Store')]/ancestor::label");

	By cashBackPresent = By.xpath("//a/*[contains(text(),'Cashback')]");

	By tokenNumber = By.xpath("//span[text()='Token Number:']/../following-sibling::span");

	By freshOption = By.xpath("//a[contains(@class,'accordion-row')][.//span[contains(text(),'Fresh')]]");
	By freshItems = By.xpath("//span[contains(text(),'Fresh items')]");
	By freshItemOrOption = By.xpath(
			"//a[contains(@class,'accordion-row')][.//span[contains(text(),'Fresh')]] | //span[contains(text(),'Fresh items')]");
	By buyFreshItems = By.xpath("(//input[following-sibling::*[normalize-space()='Buy Fresh Items']])[last()]");

	By continueButton = By.xpath("//a[normalize-space(text())='Continue']");
	By continueButton2 = By.xpath(
			"(//input[following-sibling::*[normalize-space()='Continue']])[last()] | (//input[following-sibling::*[normalize-space()='Use this time slot']])[last()]");

	By addGV = By.xpath("//span[contains(text(),'Add Gift Card')]");
	By enterCodeInput = By.xpath("//input[@placeholder='Enter Code']");
	By addGVorEnterCodeInput = By
			.xpath("//span[contains(text(),'Add Gift Card')] | //input[@placeholder='Enter Code']");
	By applyButton = By.xpath("(//input[following-sibling::*[normalize-space()='Apply']])[last()]");
	By gvRedeemed = By.xpath("//p[contains(text(),'successfully redeemed')]");

	By useThisPaymentMethod = By.xpath("//input[following-sibling::*[normalize-space()='Use this payment method']]");

	By freshOrderPlaced = By.xpath("//*[contains(text(),'Fresh order') and contains(text(),'placed')]");

	By placeYourOrderButton2 = By
			.xpath("//div[@id='subtotalsSection']//input[contains(@name,'placeYourOrder1')][not(@disabled)]");
}
