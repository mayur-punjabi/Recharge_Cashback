package amazon.login.orderproduct;

import org.openqa.selenium.By;

public interface OrderProduct_OR {

	By collectNow = By.xpath("//button[normalize-space(text())='Collect Now']");
	By buyNow = By.xpath("//a[normalize-space(text())='Buy Now']");

	By oneTimePurchase = By.xpath("//span[contains(text(),'One-time purchase')]//ancestor::a");
	By subscribeAndSave = By.xpath("//span[contains(text(),'Subscribe & Save')]//ancestor::a");
	By anotherWayToBuy = By.xpath("//span[contains(text(),'Another way to buy')]//ancestor::a");
	By addToCart = By.xpath("(//input[following-sibling::*[normalize-space()='Add to Cart']])[last()]");
	By quantitySpan = By
			.xpath("//select[@name='quantity']//following-sibling::span[contains(@class,'button-dropdown')]");
	By quantityList = By.xpath("//ul[contains(@class,'list-link')]");
	By quantityOption = By.xpath("//ul[contains(@class,'list-link')]//li/a[normalize-space(text())='%s']");

	By subscribeAndSaveLink = By.xpath("//a[contains(text(),'Subscribe & Save')]");
	By subscribeButton = By.xpath("(//input[following-sibling::*[normalize-space()='Subscribe']])[last()]");

	By proceedToBuy = By.xpath("//input[@name='proceedToRetailCheckout']/following-sibling::span");
	By payAtStore = By.xpath("//span[contains(text(),'Pay at Store')]/ancestor::label");
	By continueButton = By.xpath("(//input[following-sibling::*[normalize-space()='Continue']])[last()]");

	By cashBackPresent = By.xpath("//a/*[contains(text(),'Cashback')]");

	By tokenNumber = By.xpath("//span[text()='Token Number:']/../following-sibling::span");
}
