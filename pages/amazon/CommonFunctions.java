package amazon;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.google.common.io.Files;

import amazon.login.Login_OR;
import framework.constants.WaitType;
import framework.framework.CommonActions;
import framework.input.Configuration;

public class CommonFunctions extends CommonActions implements Shared_OR {

	public static String screenshotPath = "";
	public static JSONObject pincodes = null;

	public void logout() {
		launchApplication(Configuration.getProperty("amazonSignOut"));
		waitForPageLoad(50);
		if (waitForElement(Login_OR.emailOrPhone, 10, WaitType.visibilityOfElementLocated)) {
			log.debug("Logged out successfully");
		} else {
			exitApplication("Failed to log out");
		}
	}

	/**
	 * Creates the status file
	 * 
	 * @param flow
	 * @return file path
	 */
	public String createStatusFile(String flow) {

		Date todaysDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MMMM_yyyy_HH_mm_ss");
		String date = sdf.format(todaysDate);

		String statusDir = "./status/";
		File dir = new File(statusDir);
		String filePath = "./status/" + flow + "_" + date + ".html";
		File file = new File(filePath);

		if (!file.exists()) {
			try {
				if (!dir.exists()) {
					dir.mkdir();
				}
				file.createNewFile();
				String oldFilePath = "./project/baseStatus/" + flow + "Base.html";
				Files.copy(new File(oldFilePath), file);
			} catch (Exception e) {
				log.error("Failed to create file at path - " + filePath, e);
				filePath = "";
			}
		}

		return filePath;
	}

	/**
	 * Updates the status file
	 * 
	 * @param filePath
	 * @param data        data to enter in row
	 * @param failure     reason of failure while getting cashback
	 * @param noOfColumns number of columns in csv
	 */
	public void updateStatusFile(String filePath, List<String> data, String failure, int noOfColumns) {

		List<String> rowData = new ArrayList<>(Collections.nCopies(noOfColumns + 1, ""));

		// updating row data with provided data
		for (int i = 0; i < data.size() && i < noOfColumns; i++) {
			rowData.set(i, data.get(i));
		}

		rowData = rowData.stream().map(tdData -> "<td>" + tdData + "</td>").collect(Collectors.toList());

		// 1) if no failure then update status to done
		// 2) if failure contains 'done' then show the message
		// 3) if screenshot present then show failure and add that else only show
		// failure
		if (failure.isEmpty()) {
			failure = "<td style='color:green;'>Done</td>";
		} else if (failure.contains("Done - ")) {
			failure = "<td style='color:green;'>" + failure.replace("Done - ", "") + "</td>";
		} else {
			failure = screenshotPath.isEmpty() ? "<td style='color:red;'>" + failure + "</td>"
					: "<td style='color:red;'>" + failure + "<br /><a target='_blank' href='" + screenshotPath
							+ "'>View Screenshot</a></td>";
		}

		// add fail/pass to row data
		rowData.set(noOfColumns, failure);

		File file = new File(filePath);

		// updating logs if file not present
		if (!file.exists()) {
			log.error("Status file not present at path - " + filePath + ". Status - " + failure + " for data - "
					+ data.stream().collect(Collectors.joining(",")));
			return;
		}

		try {

			Document statusDoc = Jsoup.parse(file, "UTF-8");
			Element tbody = statusDoc.body().selectFirst("table tbody");
			String row = rowData.stream().collect(Collectors.joining());
			tbody.append("<tr>" + row + "</tr>");
			saveData(filePath, statusDoc.html(), false);
		} catch (Exception e) {
			log.error("Failed to update status in file at path - " + filePath + ". Status - " + failure + " for data - "
					+ data.stream().collect(Collectors.joining(",")), e);
		}
	}

	/**
	 * Reads the CSV file and returns the data
	 * 
	 * @param filePath
	 * @return
	 */
	public List<List<String>> getCSVData(String filePath) {
		List<List<String>> data = new ArrayList<>();
		log.debug("Reading csv file at path - " + filePath);

		try {
			File file = new File(filePath);
			if (file.exists()) {
				data = Files.readLines(file, StandardCharsets.ISO_8859_1).stream()
						.map(line -> line.contains(",") ? Arrays.asList(line.split(","))
								: line.contains(";") ? Arrays.asList(line.split(";")) : Arrays.asList(line.split("\t")))
						.collect(Collectors.toList());
			} else {
				log.error("File at path - " + filePath + " doesn't exist");
			}
		} catch (Exception e) {
			log.error("Exection occurred while reading file at path - " + filePath);
		}

		log.debug("Data from csv at path - " + filePath + ":");
		log.debug(data.stream().map(line -> line.stream().collect(Collectors.joining(",")))
				.collect(Collectors.joining("\n")));

		// removing the header
		if (!data.isEmpty()) {
			data.remove(0);
		}
		return data;
	}

	/**
	 * Clears the CSV file
	 * 
	 * @param filePath
	 */
	public void clearCSVFile(String filePath) {
		log.debug("Clearing csv file at path - " + filePath);

		try {
			File file = new File(filePath);
			if (file.exists()) {
				List<String> data = Files.readLines(file, StandardCharsets.ISO_8859_1);
				log.debug("CSV file data to delete:");
				String header = data.get(0);
				data.clear();
				data.add(header);
				saveData(filePath, data.stream().collect(Collectors.joining("\n")), false);
			} else {
				log.error("File at path - " + filePath + " doesn't exist");
			}
		} catch (Exception e) {
			log.error("Exception occurred while clearing file at path - " + filePath, e);
		}
	}

	/**
	 * Clears cookies, local storage and session storage
	 */
	public void clearBrowserStorage() {
		try {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			deleteCookies();
			pause(1500);
			executor.executeScript("localStorage.clear()");
			pause(1500);
			executor.executeScript("sessionStorage.clear()");
		} catch (Exception e) {
			log.error("Exception occurred while clearing cookies, localStorate, sessionStorage", e);
		}
	}

	/**
	 * Checks if element is displayed or not
	 * 
	 * @param locator
	 * @return
	 */
	public boolean isElementDisplayed(By elementLocator) {
		// flag to hold status of element
		boolean flag = false;

		// variable to hold element name
		String elementName = null;

		try {
			// set web element name
			elementName = elementLocator.toString();

			WebElement element = driver.findElement(elementLocator);

			// check if element gets enabled
			flag = (element.isDisplayed());

			if (flag) {
				log.debug("Element is displayed - " + elementName);
			}

		} catch (NoSuchElementException e) {
			log.error("Element not found - " + elementName + " while checking for displayed status");
		} catch (WebDriverException e) {
			log.error("Error: Caused while checking element -'" + elementName + "' is displayed", e);
		}
		return flag;
	}

	/**
	 * Gets element's text
	 * 
	 * @param elementLocator
	 * @return
	 */
	public String getText(By elementLocator) {

		// flag to hold status of element
		String text = "";

		// variable to hold element name
		String elementName = null;

		try {
			// set web element name
			elementName = elementLocator.toString();

			WebElement element = driver.findElement(elementLocator);

			// get text
			text = element.getText();
		} catch (NoSuchElementException e) {
			log.error("Element not found - " + elementName + " while getting text");
		} catch (WebDriverException e) {
			log.error("Error: Caused while getting text of element -'" + elementName + "'", e);
		}
		return text;
	}

	/**
	 * Takes browser screenshot
	 */
	public void takeScreenshot() {

		try {

			String screenshotFolder = "./status/screenshots/";
			File dir = new File(screenshotFolder);
			if (!dir.exists()) {
				dir.mkdir();
			}

			Date todaysDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd_MMMM_yyyy_HH_mm_ss");
			String date = sdf.format(todaysDate);

			// to save file
			String screenshotName = "screenshot_" + date + ".jpg";
			screenshotPath = screenshotFolder + screenshotName;

			TakesScreenshot scrShot = (TakesScreenshot) driver;
			FileUtils.copyFile(scrShot.getScreenshotAs(OutputType.FILE), new File(screenshotPath));

			// for report link href
			screenshotPath = "screenshots/" + screenshotName;

			log.debug("Screenshot taken at path - " + screenshotPath);
		} catch (Exception e) {
			log.error("Error occurred while taking screenshot", e);
		}
	}

	/**
	 * Clears the screenshot
	 */
	public void clearScreenshot() {
		screenshotPath = "";
	}

	/**
	 * Logs failure and takes screenshot
	 * 
	 * @param message
	 * @param exception
	 */
	public void reportFailure(String message, Exception... exception) {
		takeScreenshot();
		if (exception.length > 0) {
			log.error(message, exception[0]);
		} else {
			log.error(message);
		}
	}

	/**
	 * Pause - ThreadSleep
	 * 
	 * @param timeInMilliSecond time in milliseconds
	 */
	public void pause(int timeInMilliSecond) {
		try {
			// Thread sleep for provided time
			Thread.sleep(timeInMilliSecond);

		} catch (Exception e) {
			log.error("Exception occurred in Thread sleeping", e);
		}
	}// end of threadSleep

	/**
	 * Sets the value in the locator. If not set, tries for 2 more times
	 * 
	 * @param locator
	 * @param value
	 * @return
	 */
	public boolean setValueMultipleTimes(By locator, String value) {

		boolean valueSet = false;

		// set the Element Name
		String elementName = null;
		elementName = locator.toString();

		try {
			// get the webElement of locator
			WebElement element = driver.findElement(locator);
			try {

				for (int i = 0; i < 3; i++) {
					// clear the text field
					element.clear();

					// Enter keys in the text field
					element.sendKeys(value);

					// get the entered value
					String getText = getElementAttribute(locator, "value");
					if (getText.equalsIgnoreCase(value)) {
						log.debug(value + " is entered in '" + elementName + "' Text Field");
						valueSet = true;
						break;
					} else {
						log.error(getText + " is entered in '" + elementName + "' Text Field instead of " + value);
					}

					pause(1500);
				}
			} catch (WebDriverException e) {
				log.error("Exception occurred in entering " + value + " in '" + elementName, e);
			}
		} catch (NoSuchElementException e) {
			log.error("Element not found - '" + elementName + "' while setting value");
		}
		return valueSet;
	}

	/**
	 * Javascript click on WebElement
	 * 
	 * @param locator locator of WebElement
	 */
	public void javaScriptClick(By locator) {
		JavascriptExecutor executor;
		// set locator name
		String elementName = null;
		try {
			elementName = locator.toString();

			// get WebElement of locator
			WebElement element = driver.findElement(locator);
			try {

				// Create an object of JavaScriptExector Class
				executor = (JavascriptExecutor) driver;

				// click on WebElement
				executor.executeScript("arguments[0].click();", element);
				log.debug(elementName + " is js clicked");
			} catch (WebDriverException e) {
				log.error("Exception occurred in js clicking '" + elementName + "'", e);
			}
		} catch (NoSuchElementException e) {
			log.error("Element not found - " + elementName + " while js clicking");
		}
		executor = null;
	}

	/**
	 * Javascript click on WebElement
	 * 
	 * @param element WebElement
	 */
	public void javaScriptClick(WebElement element) {
		JavascriptExecutor executor;
		// set locator name
		String elementName = element.toString();

		try {

			// Create an object of JavaScriptExector Class
			executor = (JavascriptExecutor) driver;

			// click on WebElement
			executor.executeScript("arguments[0].click();", element);
			log.debug(elementName + " is js clicked");
		} catch (WebDriverException e) {
			log.error("Exception occurred in js clicking '" + elementName + "'", e);
		}
		executor = null;
	}

	/**
	 * click on WebElement
	 * 
	 * @param locator Locator of webElement
	 * 
	 */
	public void click(By locator) {
		// set the Element Name
		String elementName = null;
		try {
			elementName = locator.toString();

			// get the webElement of locator
			WebElement element = driver.findElement(locator);
			try {

				// click on webElement
				element.click();

				// wait for page to load
				log.debug(elementName + " is clicked");

			} catch (StaleElementReferenceException e) {
				log.error("StaleElementReferenceException occurred in clicking '" + elementName
						+ "'. Getting the element again and trying to click again");

				// get the webElement of locator
				element = driver.findElement(locator);
				try {

					// click on webElement
					element.click();

					// wait for page to load
					log.debug(elementName + " is clicked");
				} catch (WebDriverException wde) {
					log.error("Exception occurred in clicking '" + elementName + "'", wde);
				}
			} catch (WebDriverException wde) {
				log.error("Exception occurred in clicking '" + elementName + "'", wde);
			}
		} catch (NoSuchElementException e) {
			log.error("Element not found - '" + elementName + "'" + elementName + "' while clicking");
		}
	}

	/**
	 * To scroll to given element inside browser default window using By locator
	 * 
	 * @param locator: By
	 */
	public void jsScrollToElement(By locator) {
		JavascriptExecutor executor;

		try {
			// Create instance of Javascript executor
			executor = (JavascriptExecutor) driver;

			try {
				// Identify the WebElement which will appear after scrolling down
				WebElement element = driver.findElement(locator);

				// now execute query which actually will scroll until that element is not
				// appeared on page.
				executor.executeScript("arguments[0].scrollIntoView();", element);
				log.debug("Scrolled to given locator - " + locator);

			} catch (NoSuchElementException e1) {
				log.error("Given element is not available to scroll to");
			}
		} catch (WebDriverException e) {
			log.error("Exception occurred in scroll to element '" + locator + "'", e);
		}

		executor = null;
	}

	/**
	 * Enters address and pays
	 * 
	 * @param amount
	 * @param name
	 * @param mobile
	 * @param pincode
	 * @param flat
	 * @param area
	 * @param password
	 * @param gv
	 * @param clickContinue click continue button or not
	 * @return failure if any
	 */
	public String enterAddressAndPay(String amount, String name, String mobile, String pincode, String flat,
			String area, String password, String gv, boolean clickContinue) {

		String failure = "";

		boolean isCard = gv.trim().equalsIgnoreCase("card");

		if (clickContinue) {

			// click continue to pay button
			if (!waitForElement(continueToPayButton, 5, WaitType.visibilityOfElementLocated)) {
				failure = "Continue to Pay button isn't present";
				reportFailure(failure);
				return failure;
			}
			if (!waitForElement(continueToPayButton, 5, WaitType.elementToBeClickable)) {
				failure = "Continue to Pay button isn't clickable";
				reportFailure(failure);
				return failure;
			}
			click(continueToPayButton);

			// wait for loading
			try {
				pause(2000);
				wait.withTimeout(Duration.ofSeconds(60)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(blackLoading).stream()
								.allMatch(loading -> !loading.isDisplayed()));
				log.debug("Loading disappeared after clicking Continue to Pay button");
			} catch (TimeoutException e) {
				failure = "Loading didn't complete after clicking Continue to Pay button";
				reportFailure(failure);
				return failure;
			}

			// wait for page load
			waitForPageLoad(120);

		} else {
			log.debug("Not clicking continue button");
		}

		// enter password and click sign in if it appears
		if (waitForElement(Login_OR.passwordField, 5, WaitType.visibilityOfElementLocated)) {

			setValue(Login_OR.passwordField, password);

			if (!isElementDisplayed(Login_OR.signInButton)) {
				failure = "Sign in button isn't present after entering password";
				reportFailure(failure);
				return failure;
			}
			click(Login_OR.signInButton);
			waitForPageLoad(120);
		} else {
			log.debug("Password field isn't present after adding gift card");
		}

		// click place order and pay button
		if (!waitForElement(placeOrderAndPay, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Pay Order and Pay button isn't present.";
			reportFailure(failure);
			return failure;
		}

		if (gv.trim().equalsIgnoreCase("card")) {

			failure = addCard();
			if (!failure.isEmpty()) {
				return failure;
			}
		} else {

			// check balance is available or not
			if (waitForElement(availableBalance, 3, WaitType.visibilityOfElementLocated)) {
				log.debug("Amazon Pay balance is available for recharge");
			} else {
				failure = "Amazon Pay balance not available for recharge";
				reportFailure(failure);
				return failure;
			}

			if (isElementExists(enoughBalance)) {
				log.debug("Enough Amazon Pay balance is available for recharge");
			} else {
				failure = "Enough Amazon Pay balance not available for recharge";
				reportFailure(failure);
				return failure;
			}

			// selecting amazon pay option
			click(amazonPayLabel);
		}

		click(placeOrderAndPay);

		// wait for continue without saving card popup
		if (gv.trim().equalsIgnoreCase("card")) {

			// multiple continue without saving card buttons present. Getting the first
			// displayed one
			try {
				pause(3000);
				wait.withTimeout(Duration.ofSeconds(15)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(continueWithoutSavingCard).stream()
								.anyMatch(element -> element.isDisplayed()));
				log.debug("Continue without saving card popup is present");
				WebElement continueWithoutSavingCardElement = getList(continueWithoutSavingCard).stream()
						.filter(element -> element.isDisplayed()).collect(Collectors.toList()).get(0);
				javaScriptClick(continueWithoutSavingCardElement);

			} catch (TimeoutException e) {
				log.debug("Continue without saving card popup isn't present");
			}
		}

		// wait for loading
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking Place order and buy button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking Place order and buy button";
			reportFailure(failure);
			return failure;
		}

		// wait for page load
		waitForPageLoad(120);

		if (!waitForElement(nameInput, 5, WaitType.visibilityOfElementLocated)) {

			// check if otp button is present
			failure = otpRequired();
			if (!failure.isEmpty()) {
				return failure;
			}

			// check for recharge processing
			failure = waitForPaymentProcessing(10, false, isCard);
			if (failure.equals("Recharge was successful")) {
				failure = "";
				return failure;
			} else if (failure.equals("Recharge processing didn't appear")) {
				failure = "Name field not present after clicking Place order and buy button";
				reportFailure(failure);
			}
			return failure;
		}

		failure = addAddressDetails(name, mobile, pincode, flat, area);
		if (!failure.isEmpty()) {
			return failure;
		}

		// click add address button
		if (!waitForElement(addOrUseAddress, 2, WaitType.presenceOfElementLocated)) {
			failure = "Add/Use Address button isn't present";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(addOrUseAddress);
		if (!waitForElement(addOrUseAddress, 2, WaitType.elementToBeClickable)) {
			failure = "Add/Use Address button isn't clickable";
			reportFailure(failure);
			return failure;
		}
		click(addOrUseAddress);

		// wait for loading
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking Add Address button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking Add Address button";
			reportFailure(failure);
			return failure;
		}

		boolean skipSaveAdress = false;

		// click save address button
		if (!isElementDisplayed(saveAddressButton)) {

			if (isElementDisplayed(useThisAddressButton)) {
				log.debug("Use this address apppeared instead of save address");
				skipSaveAdress = true;
			} else {
				failure = "Save Address button isn't present";
				reportFailure(failure);
				return failure;
			}
		}

		if (!skipSaveAdress) {
			if (!waitForElement(saveAddressButton, 2, WaitType.elementToBeClickable)) {
				failure = "Save Address button isn't clickable";
				reportFailure(failure);
				return failure;
			}
			click(saveAddressButton);

			// wait for loading
			try {
				pause(3000);
				wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(yellowLoading).stream()
								.allMatch(loading -> !loading.isDisplayed()));
				log.debug("Loading disappeared after clicking Save Address button");
			} catch (TimeoutException e) {
				failure = "Loading didn't complete after clicking Save Address button";
				reportFailure(failure);
				return failure;
			}
		}

		waitForPageLoad(60);

		boolean clickUseThisAddress = true;

		// click use this address button
		if (!waitForElement(useThisAddressButton, 5, WaitType.visibilityOfElementLocated)) {

			if (isElementDisplayed(placeYourOrderButton)) {
				log.debug("Place your order button displayed instead of use this address button");
				clickUseThisAddress = false;
			} else {
				failure = "Use this address button isn't present";
				reportFailure(failure);
				return failure;
			}
		}

		if (clickUseThisAddress) {
			if (!waitForElement(useThisAddressButton, 2, WaitType.elementToBeClickable)) {
				failure = "Use this address button isn't clickable";
				reportFailure(failure);
				return failure;
			}
			click(useThisAddressButton);

			// wait for loading
			try {
				pause(3000);
				wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
						.until(driver -> driver.findElements(yellowLoading).stream()
								.allMatch(loading -> !loading.isDisplayed()));
				log.debug("Loading disappeared after clicking Use this address button");
			} catch (TimeoutException e) {
				failure = "Loading didn't complete after clicking Use this address button";
				reportFailure(failure);
				return failure;
			}
		}

		// click place your order button
		if (!isElementDisplayed(placeYourOrderButton)) {
			failure = "Place your order button isn't present";
			reportFailure(failure);
			return failure;
		}
		if (!waitForElement(placeYourOrderButton, 2, WaitType.elementToBeClickable)) {
			failure = "Place your order button isn't clickable";
			reportFailure(failure);
			return failure;
		}
		click(placeYourOrderButton);

		// wait for loading
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking Place your order button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking Place your order button";
			reportFailure(failure);
			return failure;
		}

		// wait for page load
		waitForPageLoad(120);

		// check if otp button is present
		failure = otpRequired();
		if (!failure.isEmpty()) {
			return failure;
		}

		// wait for recharge processing
		failure = waitForPaymentProcessing(30, true, isCard);
		if (failure.equals("Recharge was successful")) {
			failure = "";
		}

		return failure;
	}

	public String addAddressDetails(String name, String mobile, String pincode, String flat, String area) {

		String failure = "";

		// enter name
		if (!setValueMultipleTimes(nameInput, name)) {
			failure = "Failed to set name - " + name;
			reportFailure(failure);
			return failure;
		}

		// enter mobile number
		if (!setValueMultipleTimes(mobileInput, mobile)) {
			failure = "Failed to set mobile number - " + mobile;
			reportFailure(failure);
			return failure;
		}

		// enter pin code
		if (!setValueMultipleTimes(pincodeInput, pincode)) {
			failure = "Failed to set pin code - " + pincode;
			reportFailure(failure);
			return failure;
		}

		pincodes = getPincodeData();
		if (pincode != null) {
			JSONObject currentPincodeData = (JSONObject) pincodes.get(pincode);
			String district = (String) currentPincodeData.get("district");
			String state = (String) currentPincodeData.get("state");
			if (!waitForElement(getLocator(currentStateSpan, state.toUpperCase()), 5,
					WaitType.presenceOfElementLocated)) {
				click(addressStateSpan);
				By currentStateOption = getLocator(addressStateOption, state.toUpperCase());
				if (waitForElement(currentStateOption, 2, WaitType.visibilityOfElementLocated)) {
					click(currentStateOption);
					pause(1000);
				} else {
					log.error("State not available while setting state - '" + state + "' in address");
				}
			} else {
				log.debug("State - '" + state + "' is already selected");
			}

			if (!setValueMultipleTimes(cityInput, district)) {
				failure = "Failed to set city - " + district;
				reportFailure(failure);
				return failure;
			}
		} else {
			log.error("Pincode data is null");
		}

		// enter flat(address 1)
		if (!setValueMultipleTimes(flatInput, flat)) {
			failure = "Failed to set flat - " + flat;
			reportFailure(failure);
			return failure;
		}

		// enter area(address 2)
		if (!setValueMultipleTimes(areaInput, area)) {
			failure = "Failed to set area - " + area;
			reportFailure(failure);
			return failure;
		}

		return failure;
	}

	private String addCard() {

		String failure = "";

		// click card label
		click(cardLabel);

		// wait for add card visibility
		if (!waitForElement(addCardLink, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Add card link not present after selecting card payment";
			reportFailure(failure);
			return failure;
		}

		// click add card
		click(addCardLink);

		// wait for yellow loading invisibility
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking add card link");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking add card link";
			reportFailure(failure);
			return failure;
		}

		// wait for iframe visibility
		if (!waitForElement(addCardIframe, 10, WaitType.visibilityOfElementLocated)) {
			failure = "Card detail poup isn't present";
			reportFailure(failure);
			return failure;
		}

		// switch to iframe
		switchToFrame(addCardIframe);

		// add card number
		String cardNo = Configuration.getProperty("cardNumber");
		setValue(cardNumberInput, cardNo);

		// add name on card
		String cardName = Configuration.getProperty("cardName");
		setValue(cardNameInput, cardName);

		// select month
		String month = Configuration.getProperty("cardMonth");
		if (month.trim().length() == 1) {
			month = "0" + month;
		}
		click(monthSpan);
		By monthOption = getLocator(monthYearOption, month);
		if (!waitForElement(monthOption, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Month options list didn't open";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(monthOption);
		click(monthOption);
		if (!waitForElement(monthOption, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "Month options list didn't close after selecting - '" + month + "'";
			reportFailure(failure);
			return failure;
		}

		// select year
		String year = Configuration.getProperty("cardYear");
		click(yearSpan);
		By yearOption = getLocator(monthYearOption, year);
		if (!waitForElement(yearOption, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Year options list didn't open";
			reportFailure(failure);
			return failure;
		}

		jsScrollToElement(yearOption);
		click(yearOption);
		if (!waitForElement(yearOption, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "Year options list didn't close after selecting - '" + year + "'";
			reportFailure(failure);
			return failure;
		}

		// uncheck default payment
		handleCheckbox(defaultPayment, false);

		// click add card
		click(addCardButton);

		// switch to default
		switchToDefaultContent();

		// wait for yellow loading invisibility
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(yellowLoading).stream().allMatch(loading -> !loading.isDisplayed()));
			log.debug("Loading disappeared after clicking add card button");
		} catch (TimeoutException e) {
			failure = "Loading didn't complete after clicking add card button";
			reportFailure(failure);
			return failure;
		}

		// wait for cvv input
		try {
			pause(3000);
			wait.withTimeout(Duration.ofSeconds(10)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(cvvInput).stream().anyMatch(cvvInput -> cvvInput.isDisplayed()));
			log.debug("CVV input is present");
		} catch (TimeoutException e) {
			failure = "CVV input isn't present";
			reportFailure(failure);
			return failure;
		}

		// add cvv
		String cvv = Configuration.getProperty("cardCVV");
		try {

			WebElement cvvInputElement = getList(cvvInput).stream().filter(cvvInput -> cvvInput.isDisplayed())
					.collect(Collectors.toList()).get(0);
			// clear the text field
			cvvInputElement.clear();

			// Enter keys in the text field
			cvvInputElement.sendKeys(cvv);

			// get the entered value
			String getText = cvvInputElement.getAttribute("value");
			if (getText.equalsIgnoreCase(cvv)) {
				log.debug(cvv + " is entered in '" + cvvInput + "' Text Field");
			} else {
				log.debug(getText + " is entered in '" + cvvInput + "' Text Field instead of " + cvv);
			}
		} catch (WebDriverException e) {
			log.error("Exception occurred in entering " + cvv + " in '" + cvvInput, e);
		}

		// uncheck save card
		handleCheckbox(saveCard, false);

		// wait for enter details every time and click it
		if (waitForElement(enterCardDetailsEveryTime, 10, WaitType.visibilityOfElementLocated)) {
			log.debug("Enter details card every time popup present");
			click(enterCardDetailsEveryTime);
		} else {
			log.error("Enter details card every time popup isn't present");
		}

		return failure;
	}

	/**
	 * Waits for the recharge to process
	 * 
	 * @param timeToWait         time to wait for loading to appear in seconds
	 * @param reportNoProcessing report processing not present failure or not
	 * @param isCard             card payment or not
	 * 
	 * @return
	 */
	private String waitForPaymentProcessing(int timeToWait, boolean reportNoProcessing, boolean isCard) {

		String failure = "";

		if (isCard) {
			// wait for amazon loading/please wait
			waitForPageLoad(120);
			if (!waitForElement(amazonImgBlackLoading, 120, WaitType.invisibilityOfElementLocated)) {
				failure = "Loading didn't complete after clicking Continue to Pay button/Place and order button";
				reportFailure(failure);
				return failure;
			}
			waitForPageLoad(120);

			// wait for ipin or OTP or password link
			if (!waitForElement(pinOrOTPOrPassword, 30, WaitType.visibilityOfElementLocated)) {
				failure = "Pin or Password link isn't present";
				reportFailure(failure);
				return failure;
			}

			// click otp or password link
			if (isElementDisplayed(enterOTPOrPassword)) {
				log.debug("Enter OTP or Password link present");
				click(enterOTPOrPassword);
			}

			// wait for amazon loading/please wait
			waitForPageLoad(120);
			if (!waitForElement(amazonImgBlackLoading, 120, WaitType.invisibilityOfElementLocated)) {
				failure = "Loading didn't complete after clicking Continue to Pay button/Place and order button";

			}
			waitForPageLoad(120);

			// wait for ipin
			if (!waitForElement(pinInput, 30, WaitType.visibilityOfElementLocated)) {
				failure = "Pin input isn't present";
				reportFailure(failure);
				return failure;
			}

			// enter pin
			String pin = Configuration.getProperty("cardPin");
			setValue(pinInput, pin);

			// click submit
			click(pinSubmit);

			// wait for page load and amazon loading/please wait
			waitForPageLoad(120);
			if (!waitForElement(amazonImgBlackLoading, 120, WaitType.invisibilityOfElementLocated)) {
				failure = "Loading didn't complete after clicking Continue to Pay button/Place and order button";
				reportFailure(failure);
				return failure;
			}
			waitForPageLoad(120);
		}

		// wait for loading
		int processingWaitTime = 120;
		try {
			processingWaitTime = Integer.parseInt(Configuration.getProperty("processingWaitTime"));
		} catch (Exception e) {
			log.error("Failed to get processingWaitTime", e);
		}
		if (waitForElement(yellowLoading2, timeToWait, WaitType.visibilityOfElementLocated)) {
			if (!waitForElement(yellowLoading2, processingWaitTime, WaitType.invisibilityOfElementLocated)) {
				failure = "Loading didn't complete after clicking Continue to Pay button/Place and order button";
				reportFailure(failure);
				return failure;
			} else {
				log.debug("Loading disappeared after clicking Continue to Pay button/Place and order button");
			}
		} else {
			// verify recharge successful or not
			if (!waitForElement(rechargePending, 5, WaitType.invisibilityOfElementLocated)) {
				failure = "Recharge is pending. Please check manually";
				reportFailure(failure);
				return failure;
			}
			if (waitForElement(rechargeSuccessful, 5, WaitType.visibilityOfElementLocated)) {
				failure = "Recharge was successful";
				log.debug(failure);
				return failure;
			}
			failure = "Recharge processing didn't appear";
			if (reportNoProcessing) {
				reportFailure(failure);
			}
			return failure;
		}

		// verify recharge successful or not
		if (!waitForElement(rechargePending, 5, WaitType.invisibilityOfElementLocated)) {
			failure = "Recharge is pending. Please check manually";
			reportFailure(failure);
			return failure;
		}
		if (waitForElement(rechargeSuccessful, 5, WaitType.visibilityOfElementLocated)) {
			failure = "Recharge was successful";
			log.debug(failure);
		} else {
			failure = "Recharge was unsuccessful";
			reportFailure(failure);
			return failure;
		}
		return failure;
	}

	/**
	 * Verifies if OTP is required or not
	 * 
	 * @return
	 */
	private String otpRequired() {

		String failure = "";

		try {
			wait.withTimeout(Duration.ofSeconds(7)).ignoring(StaleElementReferenceException.class).until(
					driver -> driver.findElements(sendOTPButton).stream().anyMatch(button -> button.isDisplayed()));
			failure = "OTP is required";
			reportFailure(failure);
			return failure;
		} catch (TimeoutException e) {
			log.debug("OTP is not required");
		}

		return failure;
	}

	/**
	 * Get all the option values in drop-down
	 * 
	 * @param dropDownLocator Locator of drop-down
	 * 
	 * @param getValue        true/false based on the need to get value
	 * 
	 * @return ArrayList All the options text/value of drop-down as List
	 * 
	 */
	public ArrayList<String> getDropDownOptions(By dropDownLocator, boolean... getValue) {
		// variable to hold drop-down element
		String elementName = null;

		// need to get the value of not
		boolean needValue = false;
		if (getValue.length > 0)
			needValue = getValue[0];

		Select dropDownSelect;

		// List to store all options of drop-down
		ArrayList<String> allOptionList = new ArrayList<String>();

		try {
			// set drop-down element name
			elementName = dropDownLocator.toString();

			try {

				// wait and locate drop-down element
				waitForElement(dropDownLocator, 40, WaitType.visibilityOfElementLocated);
				WebElement element = driver.findElement(dropDownLocator);

				dropDownSelect = new Select(element);

				// Retrieve all options web elements as List
				List<WebElement> dropDownOptions = dropDownSelect.getOptions();

				// checking if list contains drop-down options web elements
				if (dropDownOptions != null) {

					// store each drop-down option in list
					for (WebElement webElement : dropDownOptions) {

						try {
							if (needValue)
								// Extracting the value
								allOptionList.add(webElement.getAttribute("value"));
							else
								// Extracting the label texts for each option
								allOptionList.add(webElement.getText());
						} catch (Exception e) {
							log.error("Error getting option from drop-down - '" + elementName + "'", e);
							allOptionList.add("");
						}
					}

					log.debug(
							"Drop down options retrieved\n" + allOptionList.stream().collect(Collectors.joining(",")));
				} else {
					log.error("No option is available in drop-down - '" + elementName + "'");
				}
			} catch (NoSuchElementException e) {
				log.error("Drop down element not found - '" + elementName + "'");
			}
		} catch (WebDriverException e) {
			log.error("Exception occurred in getting option values of '" + elementName + "'", e);
		}

		dropDownSelect = null;
		return allOptionList;
	}

	/**
	 * Convert locator string to By type
	 * 
	 * @param strLocator element locator as String
	 * 
	 * @return By return elementLocator as By
	 * 
	 */
	public By locatorParser(String strLocator) {

		// variable to hold element locator
		By elementLocator = null;

		// get locator string excluding locator type(i.e. xpath, id etc)
		try {

			String locatorString = strLocator.substring(strLocator.indexOf(":") + 1, strLocator.length()).trim();

			// get locator type string (i.e. xpath, id etc)
			// first index based on the locator coming
			String locatorType = strLocator
					.substring(strLocator.startsWith("By.") ? strLocator.indexOf(".") + 1 : 0, strLocator.indexOf(":"))
					.trim();

			// based on locator type string it returns element locator object
			switch (locatorType) {

			// returns By id locator
			case "id":
				elementLocator = By.id(locatorString);
				break;

			// returns By name locator
			case "name":
				elementLocator = By.name(locatorString);
				break;

			// returns By partialLinkText locator
			case "partialLinkText":
				elementLocator = By.partialLinkText(locatorString);
				break;

			// returns By className locator
			case "className":
				elementLocator = By.className(locatorString);
				break;

			// returns By cssSelector locator
			case "cssSelector":
				elementLocator = By.cssSelector(locatorString);
				break;

			// returns By cssSelector locator
			case "tagName":
				elementLocator = By.tagName(locatorString);
				break;

			// returns By linkText locator
			case "linkText":
				elementLocator = By.linkText(locatorString);
				break;

			// returns By xpath locator
			case "xpath":
				elementLocator = By.xpath(locatorString);
				break;

			// invalid locator type
			default:
				log.error("Invalid locator type");
			}

		} catch (WebDriverException e) {
			log.error("Exception occurred while parsing locator", e);
		}
		return elementLocator;
	}

	/**
	 * Get dynamic locator by formatting wild card in locator string
	 * 
	 * @param elementLocator Locator must contain N no wild cards '%s'
	 *                       elementLocator as By
	 * 
	 * @param strReplace     N no of wild cards to be replaced with specified N no
	 *                       of arguments as string
	 * 
	 * @return By return locator as By
	 * 
	 * 
	 */
	public By getLocator(By elementLocator, String... strReplace) {// variable to hold count of wild card %s in locator
																	// string
		int wildCardCount;

		// get count of string arguments
		int countOfStringArguments = strReplace.length;

		try {
			// get string value of element locator containing two wild cards %s
			String strElementLocator = String.valueOf(elementLocator);

			// replace all '%s' with \"%s\" to induce uniformity
			strElementLocator = strElementLocator.replace("'%s'", "\"%s\"");

			// get no of wild card '%s' in locator string
			wildCardCount = (strElementLocator.length() - strElementLocator.replace("%s", "").length()) / 2;

			// check for two wild cards
			if (wildCardCount == countOfStringArguments) {

				for (int i = 0; i < wildCardCount; i++) {
					String replacement = "";

					boolean containsDoubleQuotes = strReplace[i].contains("\"");
					boolean containsSingleQuotes = strReplace[i].contains("'");

					if (containsDoubleQuotes && !containsSingleQuotes) { // wrap replacement text by '
						replacement = "'" + strReplace[i] + "'";
					} else if (!(containsSingleQuotes && containsDoubleQuotes)) { // wrap replacement text by "
						replacement = "\"" + strReplace[i] + "\"";
					} else {
						log.error(
								"The replacement string contains both \' and \". Please try using translate in locator to replace \".");
					}

					// get the index of %s
					int indexOfWildcard = strElementLocator.indexOf("%s");
					// check if it's with quotes
					boolean withQuotes = (strElementLocator.charAt(indexOfWildcard) - 1 == '\"') ? true : false;

					if (withQuotes)
						strElementLocator = strElementLocator.replaceFirst("\"%s\"", replacement);
					else
						strElementLocator = Pattern.compile("%s").matcher(strElementLocator)
								.replaceFirst(replacement.replaceAll("\"", "").replaceAll("\\$", "\\\\\\$"));
				}

				// convert locator string to By type
				elementLocator = locatorParser(strElementLocator);

			} else {
				log.error("No of wild cards and No of strings passed in argument is not matched");
				elementLocator = null;
			}
		} catch (WebDriverException e) {
			log.error("Exception occurred while getting dynamic locator", e);
		}
		// return dynamic element locator
		return elementLocator;
	}

	/**
	 * Select an option from drop-down
	 * 
	 * @param dropDownLocator Locator of drop-down element
	 * 
	 * @param optionValue     Value attribute of option or visible text of option as
	 *                        String
	 * 
	 * @return boolean
	 * 
	 */

	public boolean selectDropDownOption(By dropDownLocator, String optionValue) {
		// variable to hold drop down element
		String elementName = null;

		Select dropDownSelect;

		// in-case of invalid option, store in variable for reporting
		String invalidOption = null;

		// List to store all options of drop-down
		ArrayList<String> allOptionList = new ArrayList<String>();

		// return if selected
		boolean dropdownSelected = false;

		try {
			// set drop-down element name
			elementName = dropDownLocator.toString();

			try {

				// wait and locate drop-down element
				waitForElement(dropDownLocator, 40, WaitType.visibilityOfElementLocated);
				WebElement element = driver.findElement(dropDownLocator);

				dropDownSelect = new Select(element);

				// Retrieve all options web elements as List
				List<WebElement> dropDownOptions = dropDownSelect.getOptions();

				// checking if list contains drop-down options web elements
				if (dropDownOptions != null) {

					allOptionList = getDropDownOptions(dropDownLocator);

					if (allOptionList.contains(optionValue)) {

						invalidOption = optionValue;

						// select desired option from drop down
						dropDownSelect.selectByVisibleText(optionValue);

						log.debug("Drop down option is selected with specified visible text -'" + optionValue + "'");
						dropdownSelected = true;
					} else {

						invalidOption = optionValue;

						try {
							// select desired option based on value attribute
							dropDownSelect.selectByValue(optionValue);

							log.debug("Drop down option is selected with specified value -'" + optionValue + "'");
							dropdownSelected = true;
						} catch (NoSuchElementException e) {
							log.error("Specified option -'" + invalidOption + "' is not available in drop down - '"
									+ elementName + "'");
						}
					}
				} else {
					log.error("No option is available in drop-down - '" + elementName + "'");
				}
			} catch (NullPointerException e) {
				log.error("Specified option -'" + invalidOption + "' is not available in drop down - '" + elementName
						+ "'");
			} catch (NoSuchElementException e) {
				log.error("Element not found - '" + elementName + "'");
			}
		} catch (WebDriverException e) {
			log.error("Exception occurred in selecting option value of drop down '" + elementName + "'", e);
		}

		return dropdownSelected;
	}

	/**
	 * Gets the pincode data
	 * 
	 * @return
	 */
	public JSONObject getPincodeData() {

		if (pincodes == null) {
			try {
				JSONParser parser = new JSONParser();
				pincodes = (JSONObject) parser.parse(new FileReader("./project/data/pincode.json"));
			} catch (Exception e) {
				log.error("Failed to get pincode data", e);
				pincodes = null;
			}
		}

		return pincodes;
	}

	/**
	 * Get List of WebElement
	 *
	 * @param locator Specifies locator of element
	 * 
	 * @return List<WebElement> List of WebElement
	 */
	public List<WebElement> getList(By locator) {
		String locatorName = null;
		try {
			locatorName = locator.toString();
			List<WebElement> elements = driver.findElements(locator);
			if (elements != null) {
				return elements;
			} else {
				log.error(locatorName + " List is null");
			}
		} catch (WebDriverException e) {
			log.error("Exception occurred in getting List of '" + locatorName + "'", e);
		}
		return new ArrayList<>();
	}

	/**
	 * Check/Uncheck the element
	 * 
	 * @param locator Locator of webElement
	 * @param check   true if check else false
	 * 
	 */
	public void handleCheckbox(By locator, boolean check) {
		// set the Element Name
		String elementName = null;
		try {
			elementName = locator.toString();

			// get the webElement of locator
			WebElement element = driver.findElement(locator);
			try {

				boolean isChecked = element.isSelected();

				if (isChecked && check) {
					log.debug(elementName + " is already checked");
				} else if (!isChecked && check) {
					click(locator);
					log.debug(elementName + " is checked");
				} else if (isChecked && !check) {
					click(locator);
					log.debug(elementName + " is unchecked");
				} else {
					log.debug(elementName + " is already unchecked");
				}

				log.debug(elementName + " is checked/unchecked - " + check);
			} catch (WebDriverException e) {
				log.error("Exception occurred in checking/unchecking - " + check + " '" + elementName + "'", e);
			}
		} catch (NoSuchElementException e) {
			log.error("Element not found - '" + elementName + "' while checking/unchecking - " + check);
		}
	}

	/**
	 * This will close all windows excepts the window handle passed and switch to
	 * the new window.
	 * 
	 * @param windowIdToSwitch        window handle which is to switch
	 * @param windowHandlesNotToClose window handles not to close
	 */
	public void closeAllwindowExcept(String windowIdToSwitch, List<String> windowHandlesNotToClose) {
		try {

			// loop through all the handle and close all ignoring given one
			for (String window : driver.getWindowHandles()) {
				if (!windowHandlesNotToClose.contains(windowIdToSwitch)) {

					driver.switchTo().window(window);

					driver.close();
					log.debug("Window closed with id - " + window);
				}
			}

			// switch to given handle
			switchToWindowUsingHandle(windowIdToSwitch);
			switchToDefaultContent();
			log.debug("Switched to given window");

		} catch (Exception e) {
			switchToDefaultContent();
			log.error("Exception in closing all windows", e);
		}
	}

	public boolean waitForAlert(int waitTime) {

		boolean isAlertPresent = false;
		wait.withTimeout(Duration.ofSeconds(waitTime));
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			isAlertPresent = true;
		} catch (TimeoutException e) {
			log.error("Alert not present");
		} catch (WebDriverException e) {
			log.error("Error occurs while waiting for alert", e);
		}

		return isAlertPresent;
	}

	public void handleAlert() {

		try {
			String text = driver.switchTo().alert().getText();
			driver.switchTo().alert().accept();
			log.debug("Alert handled with text - " + text);
		} catch (WebDriverException e) {
			log.error("Failed to handle alert", e);
		}
	}

	public void submitForm(By locator) {

		try {
			WebElement formElement = getWebElement(locator);
			formElement.submit();
		} catch (WebDriverException e) {
			log.error("Failed to submit form", e);
		}
	}

	/**
	 * Append +91 if the phoneOrEmail is phone number
	 * 
	 * @param phoneOrEmail
	 * @return
	 */
	public String phoneOrEmail(String phoneOrEmail) {

		if (phoneOrEmail.contains("@")) {
			log.debug("Email address. Not modifying -" + phoneOrEmail);
		} else {

			// removing previous characters if phone number already has 91 or +91
			if (phoneOrEmail.length() > 10) {
				log.debug("Phone number - " + phoneOrEmail + " length > 10. Removing previous characters");
				phoneOrEmail = phoneOrEmail.substring(phoneOrEmail.length() - 10);
			}

			// appending +91 before phone number
			phoneOrEmail = "+91" + phoneOrEmail;
		}

		return phoneOrEmail;
	}
}
