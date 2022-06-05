package amazon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import framework.constants.WaitType;
import framework.input.Configuration;
import otp.OTP;

public class CreateAccount extends CommonFunctions implements CreateAccount_OR {

	public String launchAndCreateAccount() {

		boolean isPuzzleSolved = false;

		String failure = "";

		OTP otp = new OTP();

		// TODO: GET NUMBER

		String phoneNo = "";

		String newAccountURL = Configuration.getProperty("newAccountURL");
		if (newAccountURL == null || newAccountURL.trim().isEmpty()) {
			failure = "New account url not present in config";
			log.error(failure);
			return failure;
		}

		if (!launchApplication(newAccountURL)) {
			failure = "Failed to launch new account url - " + newAccountURL;
			reportFailure(failure);
			return failure;
		}
		waitForPageLoad(60);

		String newUserName = Configuration.getProperty("newUserName");
		if (newUserName == null || newUserName.trim().isEmpty()) {
			failure = "New user name not present in config";
			log.error(failure);
			return failure;
		}

		setValue(newAccNameInput, newUserName);
		setValue(phoneInput, phoneNo);
		setValue(passwordInput, phoneNo);
		if (isElementDisplayed(passwordCheckInput)) {
			setValue(passwordCheckInput, phoneNo);
		}
		click(continueButton);

		waitForPageLoad(120);

		waitForElement(iframe1, 60, WaitType.visibilityOfElementLocated);
		switchToFrame(iframe1);
		waitForPageLoad(120);

		switchToFrame(iframe2);
		waitForPageLoad(120);

		for (int j = 1; j <= getList(getLocator(iframe3, "")).size(); j++) {
			switchToFrame(getLocator(iframe3, "[" + j + "]"));
			if (getText(By.tagName("body")).trim().isEmpty()) {
				driver.switchTo().parentFrame();
			} else {
				break;
			}
		}
		waitForPageLoad(120);

		waitForElement(solvePuzzle, 60, WaitType.visibilityOfElementLocated);

		click(solvePuzzle);

		int i;
		for (i = 0; i < 15; i++) {
			pause(2000);
			switchToDefaultContent();
			waitForPageLoad(60);

			try {
				pause(2000);
				wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
						.until(driver -> isElementDisplayed(iframe1) || isElementDisplayed(otpInput));
			} catch (TimeoutException e) {
				failure = "Neither puzzle nor otp input is present";
				log.error(failure);
				return failure;
			}

			if (isElementDisplayed(otpInput)) {
				log.debug("Puzzle solved");
				isPuzzleSolved = true;
				break;
			}

			switchToFrame(iframe1);
			switchToFrame(iframe2);

			for (int j = 1; j <= getList(getLocator(iframe3, "")).size(); j++) {
				switchToFrame(getLocator(iframe3, "[" + j + "]"));
				if (getText(body).trim().isEmpty()) {
					driver.switchTo().parentFrame();
				} else {
					break;
				}
			}

			try {
				pause(2000);
				wait.withTimeout(Duration.ofSeconds(30)).ignoring(StaleElementReferenceException.class)
						.until(driver -> isElementDisplayed(tryAgain) || isElementDisplayed(image));
			} catch (TimeoutException e) {
				switchToDefaultContent();
				waitForPageLoad(60);
				if (isElementDisplayed(otpInput)) {
					log.debug("Puzzle solved");
					isPuzzleSolved = true;
					break;
				}
			}

			if (isElementDisplayed(tryAgain)) {
				click(tryAgain);
				continue;
			}

			if (!isElementDisplayed(spiralGalaxy)) {
				failure = "Non spiral galaxy puzzle";
				log.error(failure);
				return failure;
			}

			String sourceData = getElementAttribute(image, "src");
			BufferedImage bf = base64ToImage(sourceData);
			int part = getOrangeDifference(bf);
			By correctImgPart = getLocator(correctImgPartLoc, String.valueOf(part));
			click(correctImgPart);
		}

		if (isPuzzleSolved) {
			log.debug("Puzzle solved in " + i + " tries");

			// TODO: GET OTP
			String otpCode = "";

			setValue(otpInput, otpCode);

		} else {
			failure = "Puzzle not solved";
			log.error(failure);
			return failure;
		}

		return failure;
	}

	public BufferedImage base64ToImage(String sourceData) {

		String base64Image = sourceData.split(",")[1];
		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
		BufferedImage img = null;
		try {
			img = ImageIO.read(new ByteArrayInputStream(imageBytes));
		} catch (IOException e) {
			log.error("Failed to decode image", e);
		}
		return img;
	}

	/**
	 * Calculates the difference between two ARGB colours
	 * (BufferedImage.TYPE_INT_ARGB).
	 */
	public double compareARGB(int rgb1, int rgb2) {
		double r1 = ((rgb1 >> 16) & 0xFF) / 255.0;
		double r2 = ((rgb2 >> 16) & 0xFF) / 255.0;
		double g1 = ((rgb1 >> 8) & 0xFF) / 255.0;
		double g2 = ((rgb2 >> 8) & 0xFF) / 255.0;
		double b1 = (rgb1 & 0xFF) / 255.0;
		double b2 = (rgb2 & 0xFF) / 255.0;
		double a1 = ((rgb1 >> 24) & 0xFF) / 255.0;
		double a2 = ((rgb2 >> 24) & 0xFF) / 255.0;
		// if there is transparency, the alpha values will make difference smaller
		return a1 * a2 * Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
	}

	public int getOrangeDifference(BufferedImage im) {

		int[][] coordinates = { { 0, 100, 0, 100 }, { 101, 200, 0, 100 }, { 201, 300, 0, 100 }, { 0, 100, 101, 200 },
				{ 101, 200, 101, 200 }, { 201, 300, 101, 200 } };

		int part = 0;
		double maxDiff = 0;
		for (int parts = 0; parts < 6; parts++) {
			double diff = 0;
			for (int x = coordinates[parts][0]; x < coordinates[parts][1]; x++) {
				for (int y = coordinates[parts][2]; y < coordinates[parts][3]; y++) {
					System.out.println("x - " + x + " y - " + y);
					diff += Math.abs(compareARGB(im.getRGB(x, y), Color.ORANGE.getRGB()));
				}
			}

			System.out.println("diff for " + (parts + 1) + " - " + diff);
			if (diff > maxDiff) {
				maxDiff = diff;
				part = parts;
			}
		}
		part++;
		System.out.println("part - " + part);
		return part;
	}
}
