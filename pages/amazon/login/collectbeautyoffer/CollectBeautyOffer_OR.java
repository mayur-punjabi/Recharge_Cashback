package amazon.login.collectbeautyoffer;

import org.openqa.selenium.By;

public interface CollectBeautyOffer_OR {

	By collectNowButton = By.xpath("//button[normalize-space(text())='Collect Now']");

	By buyNowButton = By.xpath("//a[normalize-space(text())='Buy Now']");

	By offerAvailableOrNot = By.xpath(
			"//div[contains(text(),'reward') and contains(text(),'not available')] | //button[normalize-space(text())='Collect Now'] | //a[normalize-space(text())='Buy Now']");
}
