package amazon.login.gascylinder;

import org.openqa.selenium.By;

public interface GasCylinder_OR {

	By providerSpan = By.xpath("//select[@id='LPG']//following-sibling::span[contains(@class,'button-dropdown')]");
	By providerListLink = By.xpath("//ul[contains(@class,'list-link')][./li[@aria-labelledby='LPG_0']]");
	By providerOption = By.xpath(
			"//ul[contains(@class,'list-link')][./li[contains(@aria-labelledby,'LPG')]]//a[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']");

	By idInput = By.id("LpgId/RMN");
	By gasBookingDetailsButton = By.xpath("");
}
