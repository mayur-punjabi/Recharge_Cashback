package amazon.login.orderproduct;

import org.openqa.selenium.By;

public interface OrderBeauty_OR {

	By quantityButton = By.xpath("//select[@name='quantity']/following-sibling::span");

//	By addToCart = By
//			.xpath("//input[following-sibling::span[1][normalize-space()='Add to Cart']][not(@type='hidden')]");

	By addToCart = By.id("add-to-cart-button");

	By cartItems = By.xpath("//div[contains(@class,'item-content')]");

	By subscribeAndSave = By.xpath("(//a[contains(text(),'Subscribe & Save')])%s");
	By subscribeButton = By.xpath("(//input[following-sibling::*[normalize-space()='Subscribe']])[last()]");
	By proceedToBuy = By.xpath("//input[following-sibling::*[contains(normalize-space(),'Proceed to Buy')]]");

	By addMobileNo = By.xpath("//h1[text()='Add a mobile number']");

	By addNewAddress = By.xpath("//a[normalize-space()='Add a New Address']");

	By continueButton = By.xpath("//input[following-sibling::*[normalize-space()='Continue']]");

	By payAtStore = By.xpath("//span[contains(text(),'Pay at Store')]/ancestor::label");

	By placeYourOrderAndPayOrPlaceYourOrder = By.xpath(
			"//input[following-sibling::*[normalize-space()='Place Your Order and Pay']] | //input[contains(@name,'placeYourOrder1')][not(@disabled)]");

	By tokenNumber = By.xpath("//span[text()='Token Number:']/../following-sibling::span");

	By orderPlaced = By.xpath("//*[contains(normalize-space(),'Order placed')]");
}
