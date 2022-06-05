package amazon.login.addaddress;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class AddAddress extends CommonFunctions implements AddAddress_OR {

	public String launchAndAddAddress(String name, String mobile, String pincode, String flat, String area) {

		String failure = "";

		// check if data is provided properly or not
		if (name.trim().isEmpty() || mobile.trim().isEmpty() || pincode.trim().isEmpty() || flat.trim().isEmpty()
				|| area.trim().isEmpty()) {
			failure = "Data is empty for add address. Name - '" + name + "'" + "'. Phone - '" + mobile + "'"
					+ "'. Pincode - '" + pincode + "'" + "'. Flat - '" + flat + "'" + "'. Area - '" + area + "'";
			return failure;
		}

		String addAddressURL = Configuration.getProperty("addAddressURL");
		if (addAddressURL == null || addAddressURL.trim().isEmpty()) {
			failure = "Add address url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(addAddressURL)) {
			failure = "Failed to launch add address url - " + addAddressURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		if (!waitForElement(nameInput, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Name field isn't present";
			reportFailure(failure);
			return failure;
		}

		failure = addAddressDetails(name, mobile, pincode, flat, area);
		if (!failure.isEmpty()) {
			return failure;
		}

		// click add address button
		if (!waitForElement(addOrUseAddress, 2, WaitType.presenceOfElementLocated)) {
			failure = "AddAddress button isn't present";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(addOrUseAddress);
		if (!waitForElement(addOrUseAddress, 2, WaitType.elementToBeClickable)) {
			failure = "Add Address button isn't clickable";
			reportFailure(failure);
			return failure;
		}
		click(addOrUseAddress);

		waitForPageLoad(120);

		if (waitForElement(addressSaved, 10, WaitType.visibilityOfElementLocated)) {
			log.debug("Address added");
			return failure;
		} else if (isElementDisplayed(saveAddressButton)) {

			if (!waitForElement(saveAddressButton, 2, WaitType.elementToBeClickable)) {
				failure = "Save Address button isn't clickable";
				reportFailure(failure);
				return failure;
			}
			click(saveAddressButton);

		} else {
			failure = "Failed to add address";
			reportFailure(failure);
			return failure;
		}

		waitForPageLoad(120);

		if (waitForElement(addressSaved, 10, WaitType.visibilityOfElementLocated)) {
			log.debug("Address added");
		} else {
			failure = "Failed to add address";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

}
