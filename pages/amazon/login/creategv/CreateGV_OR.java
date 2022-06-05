package amazon.login.creategv;

import org.openqa.selenium.By;

public interface CreateGV_OR {

	By amountField = By.xpath("//input[contains(@id,'custom-amount')]");
	By emailButton = By.xpath("//button[normalize-space()='Email']");
	By emailField = By.xpath("//textarea[contains(@id,'recipients')]");
	By quantityField = By.xpath("//input[contains(@id,'quantity')][not(@type='hidden')]");
	By addToCartButton = By.xpath("(//input[contains(@name,'add-to-cart')][not(@type='hidden')])[1]");
	By addedToCartMessage = By.xpath("//span[normalize-space()='Added to Cart']");
}
