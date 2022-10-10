package amazon.login.spinoffer;

import org.openqa.selenium.By;

public interface SpinOffer_OR {

	By spinBanner = By.xpath("//img[@alt='Spin and Win Banner']");
	By spinButton = By.xpath("//section[@class='sw-spinwheel-notch-overlay']");
	By claimYourPrizeButton = By.xpath("//input[following-sibling::*[normalize-space()='Claim your prize']]");
	By answerQuestionButton = By.xpath("//input[following-sibling::*[normalize-space()='Answer the question']]");
	By answer = By.xpath("//div[@data-answer='30'] | //div[@data-answer='TRUE']");

	By collectOffer = By.xpath("//button[normalize-space(.)='Collect now']");
	By offer = By.xpath("//div[@class='coupon-description-main']");

}
