package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.creategv.CreateGV;
import suites.basesuite.BaseSuite;

public class PurchaseGVNetSuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void purchaseGV(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/purchasegvnet.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("purchasegvnet");
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

			if (line.size() < 14) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String phoneOrEmail = cf.phoneOrEmail(line.get(0));
			String password = line.get(1);
			String amount = line.get(2);
			String name = line.get(3);
			String mobile = line.get(4);
			String pincode = line.get(5);
			String flat = line.get(6);
			String area = line.get(7);
			String gvEmail = line.get(8);
			String bank = line.get(9);
			String netBankingID = line.get(10);
			String netBankingPwd = line.get(11);
			String netBankingTransPwd = line.get(12);
			String account = line.get(13);

			try {

				// clear cookies, local storage, session storage if exception occurred in
				// previous record
				cf.clearBrowserStorage();

				// login to amazon
				Login login = new Login();
				failure = login.launchAndLogin(phoneOrEmail, password);

				// create gift card
				if (failure.isEmpty()) {
					CreateGV addGV = new CreateGV();
					failure = addGV.createGV(amount, "1", gvEmail, true);

					// purchase gv
					if (failure.isEmpty()) {
						failure = addGV.addNetBanking(bank, name, mobile, pincode, flat, area, pincode, netBankingID,
								netBankingPwd, netBankingTransPwd, account);
					}
				}

				// logout from amazon
				cf.logout();
			} catch (Exception e) {
				failure = "Failed to purchase GV";
				cf.reportFailure(failure, e);
			}

			cf.updateStatusFile(statusFilePath, line, failure, 14);

			// stop execution in case of clear cookies
			if (failure.equals("Clear cookies")) {
				cf.exitApplication(failure);
			}
		}
	}
}
