package amazon.login.orderproduct;

import org.openqa.selenium.By;

public interface OrderBeauty_OR {

	By quantityButton = By.xpath("//select[@name='quantity']/following-sibling::span");

//	By addToCart = By
//			.xpath("//input[following-sibling::span[1][normalize-space()='Add to Cart']][not(@type='hidden')]");

	By addToCart = By.id("add-to-cart-button");

	By subscribeAndSave = By.xpath("(//a[contains(text(),'Subscribe & Save')])%s");
	By subscribeButton = By.xpath("(//input[following-sibling::*[normalize-space()='Subscribe']])[last()]");
	By proceedToBuy = By.xpath("//input[following-sibling::*[contains(normalize-space(),'Proceed to Buy')]]");

	By addMobileNo = By.xpath("//h1[text()='Add a mobile number']");

	By continueButton = By.xpath("//input[following-sibling::*[normalize-space()='Continue']]");

	By payAtStoreOrLink = By
			.xpath("//span[contains(text(),'Pay at Store') or contains(text(),'Pay through link')]/ancestor::label");

	By tokenNumber = By.xpath("//span[text()='Token Number:']/../following-sibling::span");

	By orderPlaced = By.xpath("//*[contains(normalize-space(),'Order placed')]");

	By codLabel = By.xpath("//span[text()='Cash On Delivery']/ancestor::label");
}
