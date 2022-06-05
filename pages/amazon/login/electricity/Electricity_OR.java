package amazon.login.electricity;

import org.openqa.selenium.By;

public interface Electricity_OR {

	By stateSpan = By.xpath("//select[@id='ELECTRICITY']//following-sibling::span[contains(@class,'button-dropdown')]");
	By stateListLink = By.xpath("//ul[contains(@class,'list-link')][./li[@aria-labelledby='ELECTRICITY_0']]");
	By stateOption = By.xpath(
			"//ul[contains(@class,'list-link')][./li[@aria-labelledby='ELECTRICITY_0']]//a[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']");

	By boardSpan = By.xpath("//select[@id='%s']//following-sibling::span[contains(@class,'button-dropdown')]");
	By boardListLink = By.xpath("//ul[contains(@class,'list-link')][./li[@aria-labelledby='%s_0']]");
	By boardOption = By.xpath(
			"//ul[contains(@class,'list-link')][./li[@aria-labelledby='%s_0']]//a[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']");

	By serviceOutageLoc = By.xpath("//h4[text()='Service Outage']");

	By idInput = By.xpath(
			"//input[contains(translate(@id,'n','N'),'Number') or contains(translate(@id,'n','N'),'No') or contains(translate(@id,'o','O'),'NO') or contains(translate(@id,'d','D'),'ID') or contains(@id,'Code') or contains(@id,'IVRS')][not(@type='hidden')]");

	By fetchBillButton = By.xpath("//button[normalize-space()='Fetch Bill']");
	By loading = By.id("planSpinner");
	By billFetched = By.xpath("//td[normalize-space()='Bill amount']");

	By billFetchIssue = By.xpath("//h4[normalize-space()='Information']");
	By billFetchIssueClose = By
			.xpath("//h4[normalize-space()='Information']//following-sibling::button[contains(@class,'button-close')]");
}
