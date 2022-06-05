package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.giftcard.GiftCard;
import suites.basesuite.BaseSuite;

public class AddGVSuite extends BaseSuite {

	String statusFilePath;
	List<List<String>> data;

	@BeforeSuite
	public void createStatusFile() {
		String csvFilePath = "./excel/addGV.csv";
		statusFilePath = cf.createStatusFile("addgv");
		data = cf.getCSVData(csvFilePath);
		cf.clearCSVFile(csvFilePath);

		// TODO : delete logs, status, screenshots older than 7 days or keep it
		// configurable
	}

	@Test(priority = 0)
	public void addGV() {

		for (int i = 0; i < data.size(); i++) {

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
			String gv = line.get(2);

			try {

				// clear cookies, local storage, session storage if exception occurred in
				// previous record
				cf.clearBrowserStorage();

				// login to amazon
				Login login = new Login();
				failure = login.launchAndLogin(phoneOrEmail, password);

				// add gift card
				if (failure.isEmpty()) {

					GiftCard giftCard = new GiftCard();
					failure = giftCard.launchAndAddGiftCard(gv, password);
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
