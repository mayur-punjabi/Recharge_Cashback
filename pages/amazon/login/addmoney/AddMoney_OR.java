package amazon.login.addmoney;

import org.openqa.selenium.By;

public interface AddMoney_OR {

	By incorrectName = By.xpath("//div[contains(@id,'name-validation-error')]");
	By editName = By.xpath("//a[contains(@href,'edit_name')][normalize-space()='Edit']//input");
	By newNameInput = By.name("customerName");
	By saveChangesButton = By.xpath("(//input[following-sibling::*[normalize-space()='Save Changes']])[last()]");
	By nameChangeSuccess = By.xpath("//h4[text()='Success']");

	By idInput = By.id("ovdValue");
	By selectIDSpan = By.xpath("//*[@role='button'][normalize-space()='Select ID Type']");
	By voterIDOption = By.xpath("//a[contains(@class,'dropdown-link')][normalize-space()='Voter ID']");
	By continueButton = By.xpath("(//input[following-sibling::*[normalize-space()='Continue']])[last()]");

	By idOrAmountInput = By.xpath("//input[@id='ovdValue' or @name='amount']");

	By amazonPaySection = By.id("amazonPaySection");
	By addMoneyForm = By.id("add-money-form");

}
