package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import amazon.login.Login;
import suites.basesuite.BaseSuite;

public class CookiesSuite extends BaseSuite {

	String statusFilePath;
	List<List<String>> data;

	@BeforeSuite
	public void createStatusFile() {
		statusFilePath = cf.createStatusFile("cookies");
		data = cf.getCSVData("./excel/cookies.csv");
	}

	@Test(priority = 0, enabled = true)
	public void cookies() {

		for (int i = 0; i < data.size(); i++) {

			cf.clearScreenshot();

			String failure = "";
			List<String> line = data.get(i);
			if (data.get(i).stream().allMatch(cellData -> cellData.trim().isEmpty())) {
				cf.log.error("Data is empty at row - " + (i + 1));
				continue;
			}

			if (line.size() < 2) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String phoneOrEmail = cf.phoneOrEmail(line.get(0));
			String password = line.get(1);

			try {

				// clear cookies, local storage, session storage if exception occurred in
				// previous record
//				cf.clearBrowserStorage();

				// login to amazon
				Login login = new Login();
				failure = login.launchAndLogin(phoneOrEmail, password);

				// clear cookies, local storage, session storage and again login if captcha is
				// detected
				if (failure.equals("Clear cookies")) {
					cf.log.debug("Clear cookies for - " + i);
//					login.solveCaptcha();
//					cf.clearBrowserStorage();

//					cf.updateDriver();
//					failure = login.launchAndLogin(phoneOrEmail, password);
				}

				// logout from amazon
				cf.logout();
			} catch (Exception e) {
				failure = "Failed to check for cookies";
				cf.log.error(failure, e);
			}

			if (!failure.isEmpty()) {
				cf.takeScreenshot();
			}

			cf.updateStatusFile(statusFilePath, line, failure, 2);
		}
	}

	@Test(priority = 1, enabled = false)
	public void cookies2() {

		for (int i = 0; i < 1000; i++) {

			cf.clearScreenshot();

			String failure = "";
			List<String> line = data.get(0);
			if (data.get(0).stream().allMatch(cellData -> cellData.trim().isEmpty())) {
				cf.log.error("Data is empty at row - " + (i + 1));
				continue;
			}

			if (line.size() < 2) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String phone = line.get(0);
			String password = line.get(1);

			try {

				// clear cookies, local storage, session storage if exception occurred in
				// previous record
//				cf.clearBrowserStorage();

				// login to amazon
				Login login = new Login();
				failure = login.launchAndLogin(phone, password);

				// clear cookies, local storage, session storage and again login if captcha is
				// detected
				if (failure.equals("Clear cookies")) {
					cf.log.debug("Clear cookies for - " + i);
//					cf.clearBrowserStorage();
					String captchaURL = "https://opfcaptcha-prod.s3.amazonaws.com/cbb08efb81be4062b13ca1706e1bf9e9.jpg?AWSAccessKeyId=AKIA5WBBRBBB5RSLMZ3P&Expires=1645956729&Signature=lexNWveKIit0hDU0r1SnlS69Ceo%3D";

//					cf.updateDriver();
//					failure = login.launchAndLogin(phone, password);
				}

				// logout from amazon
				cf.logout();
			} catch (Exception e) {
				failure = "Failed to check for cookies";
				cf.log.error(failure, e);
			}

			if (!failure.isEmpty()) {
				cf.takeScreenshot();
			}

			cf.updateStatusFile(statusFilePath, line, failure, 2);
		}
	}

}
