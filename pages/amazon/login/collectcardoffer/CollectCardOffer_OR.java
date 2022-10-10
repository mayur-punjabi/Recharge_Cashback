package amazon.login.collectcardoffer;

import org.openqa.selenium.By;

public interface CollectCardOffer_OR {

	By payNowButton = By.xpath("(//input[following-sibling::*[normalize-space()='Pay Now']])[last()]");

	By otpInput = By.xpath("//input[@id='indusind_otp' or @id='otpValue' or @id='passReal']");

	By cardAddedSuccessfully = By.xpath("//span[contains(text(),'saved for future')]");
}
