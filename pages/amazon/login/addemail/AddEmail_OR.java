package amazon.login.addemail;

import org.openqa.selenium.By;

public interface AddEmail_OR {

	By addEmailButton = By
			.xpath("(//input[contains(@id,'add-email')][following-sibling::*[normalize-space()='Add']])[last()]");

	By emailInput = By.xpath("//input[contains(@name,'email')]");
	By continueButton = By.xpath("(//input[following-sibling::*[normalize-space()='Continue']])[last()]");
}
