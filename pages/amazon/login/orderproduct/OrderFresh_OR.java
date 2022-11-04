package amazon.login.orderproduct;

import org.openqa.selenium.By;

public interface OrderFresh_OR {

	By addToCart = By.xpath(
			"(//input[contains(@aria-labelledby,'fresh')][following-sibling::*[normalize-space()='Add to Cart']])");
	By cartItems = By.xpath("//div[contains(@class,'active-cart--selected')]/*[contains(@class,'item')]");
	By quantityButton = By.xpath("//button[contains(text(),'Qty')]");

	By done = By.xpath("//ul[contains(@class,'list-link')]//ancestor::div//span[text()='Done']");

	By freshOption = By.xpath("//a[contains(@class,'accordion-row')][.//span[contains(text(),'Fresh')]]");
	By freshItemOrOption = By.xpath(
			"//a[contains(@class,'accordion-row')][.//span[contains(text(),'Fresh')]] | //span[contains(text(),'Fresh items')]");
	By buyFreshItems = By.xpath("(//input[following-sibling::*[normalize-space()='Buy Fresh Items']])[last()]");

	By continueButton = By.xpath("//a[normalize-space(text())='Continue']");

	By useThisPaymentMethod = By.xpath("//input[following-sibling::*[normalize-space()='Use this payment method']]");

	By freshOrderPlaced = By.xpath("//*[contains(text(),'Fresh order') and contains(text(),'placed')]");

	By placeYourOrderButton2 = By
			.xpath("//div[@id='subtotalsSection']//input[contains(@name,'placeYourOrder1')][not(@disabled)]");
}
