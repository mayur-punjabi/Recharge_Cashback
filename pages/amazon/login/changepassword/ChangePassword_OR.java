package amazon.login.changepassword;

import org.openqa.selenium.By;

public interface ChangePassword_OR {

	By passwordEditButton = By
			.xpath("(//input[contains(@id,'password')][following-sibling::*[normalize-space()='Edit']])[last()]");

	By currentPasswordInput = By.xpath("//input[@name='password']");
	By newPasswordInput = By.xpath("//input[@name='passwordNew']");
	By newPasswordCheckInput = By.xpath("//input[@name='passwordNewCheck']");
	By saveChangesButton = By.xpath("(//input[following-sibling::*[normalize-space()='Save changes']])[last()]");

	By passwordChanged = By.xpath("//h4[text()='Success']");
}
