package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.giftcard.GiftCard;
import amazon.login.recharge.Recharge;
import suites.basesuite.BaseSuite;

public class RechargeSuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void recharge(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/recharge.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("recharge");
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

			if (line.size() < 12) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String phoneOrEmail = cf.phoneOrEmail(line.get(0));
			String password = line.get(1);
			String gv = line.get(2);
			String type = line.get(3);
			String id = line.get(4);
			String amount = line.get(5);
			String name = line.get(6);
			String mobile = line.get(7);
			String pincode = line.get(8);
			String flat = line.get(9);
			String area = line.get(10);
			String checkOffer = line.get(11);

			try {

				// clear cookies, local storage, session storage if exception occurred in
				// previous record
				cf.clearBrowserStorage();

				// login to amazon
				Login login = new Login();
				failure = login.launchAndLogin(phoneOrEmail, password);

				if (failure.isEmpty()) {

					// check offer is available or not
					Recharge recharge = new Recharge();
					failure = recharge.launchAndCheckOffer(type, checkOffer);

					if (failure.isEmpty()) {

						// skipping adding gv if gv value is 'skip' or 'card'
						if (gv.trim().equalsIgnoreCase("skip") || gv.trim().equalsIgnoreCase("card")) {
							cf.log.debug("No GV prvoided for - " + i);
						} else {

							// add gift card
							GiftCard giftCard = new GiftCard();
							failure = giftCard.launchAndAddGiftCard(gv, password);
						}

						// recharge
						if (failure.isEmpty()) {
							failure = recharge.launchAndRecharge(type, id, amount, name, mobile, pincode, flat, area,
									password, gv);
						}
					}
				}

				// logout from amazon
				cf.logout();
			} catch (Exception e) {
				failure = "Failed to recharge";
				cf.reportFailure(failure, e);
			}

			cf.updateStatusFile(statusFilePath, line, failure, 12);

			// stop execution in case of clear cookies
			if (failure.equals("Clear cookies")) {
				cf.exitApplication(failure);
			}
		}
	}
}
