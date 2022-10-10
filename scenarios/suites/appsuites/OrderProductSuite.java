package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.orderproduct.OrderProduct;
import suites.basesuite.BaseSuite;

public class OrderProductSuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void orderProduct(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/orderProduct.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("orderProduct");
		}

		cf.log.debug(startIndex + " " + endIndex);

		String loggedInStore = "";

		// by pass store login mobile otp issue
		cf.launchApplication(
				"https://www.amazon.in/ap/signin?openid.pape.max_auth_age=1209600&openid.return_to=https%3A%2F%2Fwww.amazon.in%2Fh%2Frewards%2Fdp%2Famzn1.rewards.rewardAd.INWZGJOWJDCUC%3Frdpf%3Den&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=inflex&openid.mode=checkid_setup&language=en_IN&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0");

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

			String storeEmail = cf.phoneOrEmail(line.get(0));
			String storePassword = line.get(1);
			String phoneOrEmail = cf.phoneOrEmail(line.get(2));
			String password = line.get(3);
			String subscribe = line.get(4);
			String quantity = line.get(5);
			String name = line.get(6);
			String mobile = line.get(7);
			String pincode = line.get(8);
			String flat = line.get(9);
			String area = line.get(10);
			String checkCashback = line.get(11);

			try {

				// login to amazon
				Login login = new Login();

				if (!loggedInStore.equals(storeEmail)) {

					// clear cookies, local storage, session storage if exception occurred in
					// previous record
					cf.clearBrowserStorage();

					failure = login.launchAndLoginStore(storeEmail, storePassword);
				}

				if (failure.isEmpty()) {

					cf.launchApplication("https://store.amazon.in/");

					loggedInStore = storeEmail;

					failure = login.launchAndLogin(phoneOrEmail, password);

					if (failure.isEmpty()) {

						// order product
						OrderProduct orderProduct = new OrderProduct();
						failure = orderProduct.launchAndOrderProduct(subscribe, quantity, name, mobile, pincode, flat,
								area, checkCashback);
					}
				}

				// logout from amazon
				cf.logout();
			} catch (Exception e) {
				failure = "Failed to order product";
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
