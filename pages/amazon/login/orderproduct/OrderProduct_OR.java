package amazon.login.orderproduct;

import org.openqa.selenium.By;

public interface OrderProduct_OR {

	By addNewAddress = By.xpath("//a[normalize-space()='Add a New Address' or normalize-space()='Add a new address']");

	By quantityList = By.xpath("//ul[contains(@class,'list-link')]");
	By quantityOptions = By.xpath("//ul[contains(@class,'list-link')]//li");

	By collapsedItemsList = By.xpath("//div[contains(@class,'collapsed-item-list')]");
	By cartItems = By.xpath("//div[contains(@class,'item-content')]");
	By collapsedItemsOrCartItems = By
			.xpath("//div[contains(@class,'collapsed-item-list')] | //div[contains(@class,'item-content')]");
	By deleteButton = By.xpath("//input[following-sibling::*[normalize-space()='Delete']]");

	By addGV = By.xpath("//span[contains(text(),'Add Gift Card')]");
	By enterCodeInput = By.xpath("//input[@placeholder='Enter Code']");
	By addGVorEnterCodeInput = By
			.xpath("//span[contains(text(),'Add Gift Card')] | //input[@placeholder='Enter Code']");
	By applyButton = By.xpath("(//input[following-sibling::*[normalize-space()='Apply']])[last()]");
	By gvRedeemed = By.xpath("//p[contains(text(),'successfully redeemed')]");
	By continueButton2 = By.xpath(
			"(//input[following-sibling::*[normalize-space()='Continue']])[last()] | (//input[following-sibling::*[normalize-space()='Use this time slot']])[last()]");
	By placeYourOrderAndPayOrPlaceYourOrder = By.xpath(
			"//input[following-sibling::*[normalize-space()='Place Your Order and Pay']] | //input[contains(@name,'placeYourOrder1')][not(@disabled)]");

	By deliveryDateTimeLoc = By.xpath("//span[contains(@id,'delivery-promise')]");

	By upiLabel = By.xpath("//span[text()='Other UPI Apps']/ancestor::label/i");
	By upiInput = By.xpath("//input[contains(@placeholder,'upi')]");
	By verifyButton = By.xpath("//input[following-sibling::*[1][normalize-space()='Verify']]");
	By upiVerified = By.xpath("//div[text()='Verified!']");
	By completeUPIPayment = By.xpath("//h1[normalize-space()='Complete your payment']");
}
