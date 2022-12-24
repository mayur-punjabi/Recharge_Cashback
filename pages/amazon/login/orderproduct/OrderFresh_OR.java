package amazon.login.orderproduct;

import org.openqa.selenium.By;

public interface OrderFresh_OR {

	By addLocation = By.xpath("//*[contains(text(),'Select a location') or contains(text(),'Deliver to')]");
	By manageAddress = By.name("MANAGE_ADDRESS");
	By addressSaved = By.xpath("//h4[text()='Address saved']");
	By addressExist = By.xpath(
			"//input[contains(@aria-label,'%s') and contains(@aria-label,'%s') and contains(@aria-label,'%s') and contains(@aria-label,'%s')]");
	By addressSelected = By.xpath("//*[contains(text(),'Deliver to') and contains(text(),'%s')]");

	By addToCart = By.xpath(
			"(//input[contains(@aria-labelledby,'fresh')][following-sibling::*[normalize-space()='Add to Cart']])");
	By quantityButton = By.xpath("//button[contains(text(),'Qty')]");
	By quantityOption = By.xpath("//ul[contains(@class,'list-link')]//li[text()='%s']");

	By done = By.xpath("//ul[contains(@class,'list-link')]//ancestor::div//span[text()='Done']");
	By closeIcon = By.xpath("//span[contains(@class,'icon-close')]");

	By freshOption = By.xpath("//a[contains(@class,'accordion-row')][.//span[contains(text(),'Fresh')]]");
	By freshItemOrOption = By.xpath(
			"//a[contains(@class,'accordion-row')][.//span[contains(text(),'Fresh')]] | //span[contains(text(),'Fresh items')]");
	By buyFreshItems = By
			.xpath("(//input[following-sibling::*[normalize-space()='Proceed to Buy Fresh Items']])[last()]");

	By continueButton = By.xpath("//a[normalize-space(text())='Continue']");

	By freshOrderPlaced = By
			.xpath("//h2[contains(normalize-space(),'Fresh order') and contains(normalize-space(),'placed')]");
}
