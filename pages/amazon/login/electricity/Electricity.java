package amazon.login.electricity;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class Electricity extends CommonFunctions implements Electricity_OR {

	public String launchAndRechargeElectricity(String state, String board, String id, String amount, String name,
			String mobile, String pincode, String flat, String area, String password, String gv) {

		String failure = "";

		// check if data is provided properly or not
		if (state.trim().isEmpty() || board.trim().isEmpty() || id.trim().isEmpty() || amount.trim().isEmpty()
				|| name.trim().isEmpty() || mobile.trim().isEmpty() || pincode.trim().isEmpty() || flat.trim().isEmpty()
				|| area.trim().isEmpty()) {
			failure = "Data is empty for recharge. Recharge state - '" + state + "'. Board - '" + board
					+ "'. Subscriber ID - '" + id + "'. Amount - '" + amount + "'" + "'. No - '" + name + "'"
					+ "'. Phone - '" + mobile + "'" + "'. Pincode - '" + pincode + "'" + "'. Flat - '" + flat + "'"
					+ "'. Area - '" + area + "'";
			return failure;
		}

		String electricityBillURL = Configuration.getProperty("electricityBillURL");
		if (electricityBillURL == null || electricityBillURL.trim().isEmpty()) {
			failure = "Electricity url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(electricityBillURL)) {
			failure = "Failed to launch Electricity url - " + electricityBillURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		if (!waitForElement(stateSpan, 7, WaitType.visibilityOfElementLocated)) {
			failure = "State selection isn't present";
			reportFailure(failure);
			return failure;
		}

		failure = selectStateAndBoard(state, board);
		if (!failure.isEmpty()) {
			return failure;
		}

		if (!waitForElement(idInput, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Id field isn't present";
			reportFailure(failure);
			return failure;
		}

		// set id
		if (!setValueMultipleTimes(idInput, id)) {
			failure = "Failed to set subscriber id - " + id;
			reportFailure(failure);
			return failure;
		}

		if (isElementDisplayed(fetchBillButton)) {
			failure = fetchBill();
			if (!failure.isEmpty()) {
				return failure;
			}
		} else {
			log.debug("Fetch bill button not present");

			// set amount
			if (!setValueMultipleTimes(amountInput, amount)) {
				failure = "Failed to set amount - " + amount;
				reportFailure(failure);
				return failure;
			}
		}

		// entering address and paying
		failure = enterAddressAndPay(amount, name, mobile, pincode, flat, area, password, gv, true);
		if (!failure.isEmpty()) {
			return failure;
		}

		return failure;
	}

	public String launchAndCheckOffer(String checkOffer) {

		String failure = "";

		// check for offer or not
		if (checkOffer == null || checkOffer.trim().isEmpty() || checkOffer.trim().equalsIgnoreCase("yes")) {

			String electricityBillURL = Configuration.getProperty("electricityBillURL");
			if (electricityBillURL == null || electricityBillURL.trim().isEmpty()) {
				failure = "Electricity url not present in config";
				log.error(failure);
				return failure;
			}

			if (!launchApplication(electricityBillURL)) {
				failure = "Failed to launch Electricity url - " + electricityBillURL;
				reportFailure(failure);
				return failure;
			}
			waitForPageLoad(60);

			// wait for any offer to be visible
			try {
				wait.withTimeout(Duration.ofSeconds(25)).ignoring(StaleElementReferenceException.class).until(
						driver -> driver.findElements(offerAvailable).stream().anyMatch(offer -> offer.isDisplayed()));
				log.debug("Cashback offer is available");
			} catch (TimeoutException e) {
				failure = "Cashback offer isn't available";
				reportFailure(failure);
				return failure;
			}
		} else {
			log.debug("Not checking for offer");
		}

		return failure;
	}

	private String selectStateAndBoard(String state, String board) {

		String failure = "";
		state = state.trim().toLowerCase();
		board = board.trim().toLowerCase();

		click(stateSpan);
		if (!waitForElement(stateListLink, 10, WaitType.visibilityOfElementLocated)) {
			failure = "State options list didn't open";
			reportFailure(failure);
			return failure;
		}

		By currentStateOption = getLocator(stateOption, state);
		if (!isElementExists(currentStateOption)) {
			failure = "State - '" + state + "' is not present in the list";
			reportFailure(failure);
			return failure;
		}
		jsScrollToElement(currentStateOption);
		click(currentStateOption);
		if (!waitForElement(stateListLink, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "State options list didn't close after selecting - '" + state + "'";
			reportFailure(failure);
			return failure;
		}

		try {
			pause(2000);
			wait.withTimeout(Duration.ofSeconds(5)).ignoring(StaleElementReferenceException.class)
					.until(driver -> driver.findElements(serviceOutageLoc).stream()
							.allMatch(serviceOutage -> !serviceOutage.isDisplayed()));
			log.debug("Service outage not present");
		} catch (TimeoutException e) {
			failure = "Service outage for state - " + state;
			reportFailure(failure);
			return failure;
		}

		String boardString = "ELECTRICITY>hfc-states-" + state.toLowerCase().replaceAll(" ", "-");
		By currentBoardSpan = getLocator(boardSpan, boardString);
		By currentBoardListLink = getLocator(boardListLink, boardString);

		if (!waitForElement(currentBoardSpan, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Boards not present";
			reportFailure(failure);
			return failure;
		}

		click(currentBoardSpan);
		if (!waitForElement(currentBoardListLink, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Board options list didn't open";
			reportFailure(failure);
			return failure;
		}

		By currentBoardOption = getLocator(boardOption, boardString, board);
		if (!isElementExists(currentBoardOption)) {
			failure = "Board - '" + board + "' is not present in the list";
			reportFailure(failure);
			return failure;
		}
		jsScrollToElement(currentBoardOption);
		click(currentBoardOption);
		if (!waitForElement(currentBoardListLink, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "Board options list didn't close after selecting - '" + board + "'";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

	private String fetchBill() {

		String failure = "";

		click(fetchBillButton);
		pause(2000);
		if (!waitForElement(loading, 120, WaitType.invisibilityOfElementLocated)) {
			failure = "Loading didn't disappear after clicking Fetch Bill";
			reportFailure(failure);
			return failure;
		}

		if (waitForElement(billFetchIssue, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Issue occurred while fetching bill";
			reportFailure(failure);
			click(billFetchIssueClose);
			return failure;
		}

		if (waitForElement(billFetched, 10, WaitType.visibilityOfElementLocated)) {
			log.debug("Bill fetched");
		} else {
			failure = "Failed to fetch bill";
			reportFailure(failure);
			return failure;
		}

		return failure;
	}
}
