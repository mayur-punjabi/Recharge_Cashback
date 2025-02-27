package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.changepassword.ChangePassword;
import suites.basesuite.BaseSuite;

public class ChangePasswordSuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void createGV(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/changePassword.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("changePassword");
		}

		cf.log.debug(startIndex + " " + endIndex);

		for (int i = startIndex - 1; i < endIndex; i++) {

			cf.clearScreenshot();

			String failure = "";
			List<String> line = data.get(i);
			if (data.get(i).stream().allMatch(cellData -> cellData.trim().isEmpty())) {
				cf.log.error("Data is empty at row - " + (i + 1));
				continue;
			}

			if (line.size() < 3) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String phoneOrEmail = cf.phoneOrEmail(line.get(0));
			String password = line.get(1);
			String newPassword = line.get(2);

			try {

				// clear cookies, local storage, session storage if exception occurred in
				// previous record
				cf.clearBrowserStorage();

				// login to amazon
				Login login = new Login();
				failure = login.launchAndLogin(phoneOrEmail, password);

				if (failure.isEmpty()) {

					// change password
					ChangePassword changePassword = new ChangePassword();
					failure = changePassword.changePassword(password, newPassword);
				}

				// logout from amazon
				cf.logout();
			} catch (Exception e) {
				failure = "Failed to recharge";
				cf.reportFailure(failure, e);
			}

			cf.updateStatusFile(statusFilePath, line, failure, 3);

			// stop execution in case of clear cookies
			if (failure.equals("Clear cookies")) {
				cf.exitApplication(failure);
			}
		}
	}
}
