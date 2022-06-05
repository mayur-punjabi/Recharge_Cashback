package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.amazonpay.AmazonPay;
import suites.basesuite.BaseSuite;

public class GetBalanceSuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void gvCashback(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/getbalance.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("getbalance");
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

			if (line.size() < 2) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String phoneOrEmail = cf.phoneOrEmail(line.get(0));
			String password = line.get(1);

			try {

				// close all windows and clear cookies, local storage, session storage if
				// exception occurred in previous record
				cf.closeAllwindowExcept(cf.globalWinHandle);
				cf.clearBrowserStorage();

				// login to amazon
				Login login = new Login();
				failure = login.launchAndLogin(phoneOrEmail, password);

				if (failure.isEmpty()) {

					// get the balance
					AmazonPay ap = new AmazonPay();
					failure = ap.getBalance();
				}
				cf.logout();
			} catch (Exception e) {
				failure = "Failed to get the cashback";
				cf.reportFailure(failure, e);
			}

			cf.updateStatusFile(statusFilePath, line, failure, 2);

			// stop execution in case of clear cookies
			if (failure.equals("Clear cookies")) {
				cf.exitApplication(failure);
			}
		}
	}

}
