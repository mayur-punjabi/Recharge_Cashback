package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.creategv.CreateGV;
import suites.basesuite.BaseSuite;

public class CreateGVSuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void createGV(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/createGV.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("createGV");
		}

		cf.log.debug(startIndex + " " + endIndex);

		boolean loggedIn = false;

		for (int i = startIndex - 1; i < endIndex; i++) {

			cf.clearScreenshot();

			String failure = "";
			List<String> line = data.get(i);
			if (data.get(i).stream().allMatch(cellData -> cellData.trim().isEmpty())) {
				cf.log.error("Data is empty at row - " + (i + 1));
				continue;
			}

			if (line.size() < 5) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String phoneOrEmail = cf.phoneOrEmail(line.get(0));
			String password = line.get(1);
			String amount = line.get(2);
			String quantity = line.get(3);
			String email = line.get(4);

			try {

				// login once
				if (!loggedIn) {
					// clear cookies, local storage, session storage if exception occurred in
					// previous record
					cf.clearBrowserStorage();

					// login to amazon
					Login login = new Login();
					failure = login.launchAndLogin(phoneOrEmail, password);
				}

				// create gift card
				if (failure.isEmpty()) {
					loggedIn = true;
					CreateGV addGV = new CreateGV();
					failure = addGV.createGV(amount, quantity, email, false);
				} else {
					failure += " Skipping creating all GV as login failed";
				}
			} catch (Exception e) {
				failure = "Failed to create GV";
				cf.reportFailure(failure, e);
			}

			cf.updateStatusFile(statusFilePath, line, failure, 5);

			// exit the application as failed to login
			if (!loggedIn) {
				cf.exitApplication("Failed to login. Failure - " + failure);
			}
		}

		try {
			// logout from amazon
			cf.logout();
		} catch (Exception e) {
			cf.log.error("Failed to logout");
		}
	}

}
