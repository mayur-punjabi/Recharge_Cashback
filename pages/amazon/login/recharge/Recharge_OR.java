package amazon.login.recharge;

import org.openqa.selenium.By;

public interface Recharge_OR {

	By subscriberIDInput = By.id("auth_1");

	By viMobileNumber = By.xpath("//input[@id='Mobile number']");

	By operatorSpan = By
			.xpath("//select[@id='MOBILE_POSTPAID']//following-sibling::span[contains(@class,'button-dropdown')]");
	By operatorLink = By.xpath("//ul[contains(@class,'list-link')][./li[@aria-labelledby='MOBILE_POSTPAID_0']]");
	By operatorOption = By.xpath(
			"//ul[contains(@class,'list-link')][./li[contains(@aria-labelledby,'MOBILE_POSTPAID')]]//a[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']");

}
