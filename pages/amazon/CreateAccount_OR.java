package amazon;

import org.openqa.selenium.By;

public interface CreateAccount_OR {

	By newAccNameInput = By.name("customerName");
	By phoneInput = By.name("email");
	By passwordInput = By.name("password");
	By passwordCheckInput = By.name("passwordCheck");
	By continueButton = By.id("auth-continue");

	By iframe1 = By.id("cvf-arkose-frame");
	By iframe2 = By.id("fc-iframe-wrap");
	By iframe3 = By.xpath("(//iframe[contains(@id,'CaptchaFrame')])%s");

	By spiralGalaxy = By.xpath("//h2[text()='Pick the spiral galaxy']");
	By solvePuzzle = By.xpath("//button[text()='Solve Puzzle']");

	By image = By.id("game_challengeItem_image");
	By correctImgPartLoc = By.xpath("//li[contains(@id,'image%s')]/a");

	By incorrectCaptcha = By.xpath("//*[contains(text(),'Pick the spiral galaxy')]");
	By tryAgain = By.xpath("//button[text()='Try again']");

	By otpInput = By.name("code");

}
