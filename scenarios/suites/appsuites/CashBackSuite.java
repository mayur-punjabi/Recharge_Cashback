package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.amazonpay.AmazonPay;
import razorpay.RazorPay;
import suites.basesuite.BaseSuite;

public class CashBackSuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void cashback(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/cashback.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("cashback");
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

			if (line.size() < 5) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String phoneOrEmail = cf.phoneOrEmail(line.get(0));
			String password = line.get(1);
			String razorPayPhoneNo = line.get(2);
			String emailId = line.get(3);
			String amount = line.get(4);

			try {

				// close all windows and clear cookies, local storage, session storage if
				// exception occurred in previous record
				cf.closeAllwindowExcept(cf.globalWinHandle);
				cf.clearBrowserStorage();

				// login to amazon
				Login login = new Login();
				failure = login.launchAndLogin(phoneOrEmail, password);

				// get cashback from razor pay if successfully logged in to amazon
				if (failure.isEmpty()) {

					if (amount.trim().equalsIgnoreCase("full")) {
						cf.log.debug("Getting the amazon pay balance");

						// get the balance
						AmazonPay ap = new AmazonPay();
						failure = ap.getBalance();
					}

					if (failure.isEmpty() || failure.contains("Done - ")) {

						String amountValue = amount;

						if (amount.trim().equalsIgnoreCase("full")) {
							amountValue = failure.replace("Done - ", "");
						}

						RazorPay razorPay = new RazorPay();
						failure = razorPay.getCashback(razorPayPhoneNo, emailId, amountValue);

						// show amount in status file if full cashback
						if (amount.trim().equalsIgnoreCase("full") && failure.isEmpty()) {
							failure = "Done - " + amountValue;
						}
					}
				}

				// close razorpay and logout from amazon
				cf.closeAllwindowExcept(cf.globalWinHandle);
				cf.logout();
			} catch (Exception e) {
				failure = "Failed to get the cashback";
				cf.reportFailure(failure, e);
			}

			cf.updateStatusFile(statusFilePath, line, failure, 5);

			// stop execution in case of clear cookies
			if (failure.equals("Clear cookies")) {
				cf.exitApplication(failure);
			}
		}
	}
}
