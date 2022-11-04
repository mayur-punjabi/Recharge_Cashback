package amazon.login.orderproduct;

import org.openqa.selenium.By;

public interface OrderProduct_OR {

	By addNewAddress = By.xpath("//a[normalize-space()='Add a New Address' or normalize-space()='Add a new address']");

	By quantityList = By.xpath("//ul[contains(@class,'list-link')]");
	By quantityOptions = By.xpath("//ul[contains(@class,'list-link')]//li");

	By cartItems = By.xpath("//div[contains(@class,'item-content')]");

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
}
