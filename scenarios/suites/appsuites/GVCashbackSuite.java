package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.giftcard.GiftCard;
import razorpay.RazorPay;
import suites.basesuite.BaseSuite;

public class GVCashbackSuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void gvCashback(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/gvcashback.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("gvcashback");
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

			if (line.size() < 6) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String phoneOrEmail = cf.phoneOrEmail(line.get(0));
			String password = line.get(1);
			String razorPayPhoneNo = line.get(2);
			String emailId = line.get(3);
			String amount = line.get(4);
			String gv = line.get(5);

			try {

				// close all windows and clear cookies, local storage, session storage if
				// exception occurred in previous record
				cf.closeAllwindowExcept(cf.globalWinHandle);
				cf.clearBrowserStorage();

				// login to amazon
				Login login = new Login();
				failure = login.launchAndLogin(phoneOrEmail, password);

				if (failure.isEmpty()) {

					// check if offer is available or not
					RazorPay razorPay = new RazorPay();
					failure = razorPay.checkOfferAvailable(razorPayPhoneNo, emailId, amount);

					// close razorpay
					cf.closeAllwindowExcept(cf.globalWinHandle);

					if (failure.isEmpty()) {

						// add gv
						// skipping adding gv if gv value is 'skip'
						if (gv.trim().equalsIgnoreCase("skip")) {
							cf.log.debug("No GV prvoided for - " + i);
						} else {
							GiftCard giftCard = new GiftCard();
							failure = giftCard.launchAndAddGiftCard(gv, password);
						}

						if (failure.isEmpty()) {

							// get cashback from razor pay if offer gv is added
							failure = razorPay.getCashback(razorPayPhoneNo, emailId, amount);
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

			cf.updateStatusFile(statusFilePath, line, failure, 6);

			// stop execution in case of clear cookies
			if (failure.equals("Clear cookies")) {
				cf.exitApplication(failure);
			}
		}
	}
}
