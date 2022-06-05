package amazon.login.giftcard;

import org.openqa.selenium.By;

public interface GiftCard_OR {

	By giftCardInput = By.id("txt_claimCode");
	By addToYourBalance = By.xpath("(//input[following-sibling::*[normalize-space()='Add to your balance']])[last()]");

	By giftCardUsed = By.xpath(
			"//*[normalize-space()='This gift card/voucher has already been added to another account.' or contains(text(),'You already added this gift card to your account')]");
	By giftCardAdded = By
			.xpath("//*[normalize-space()='Your Gift Card amount has been added to your Amazon Pay balance.']");
}
