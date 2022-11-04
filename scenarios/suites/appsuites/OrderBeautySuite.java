package suites.appsuites;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.Login;
import amazon.login.orderproduct.OrderBeauty;
import suites.basesuite.BaseSuite;

public class OrderBeautySuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void orderProduct(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/orderbeauty.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("orderbeauty");
		}

		cf.log.debug(startIndex + " " + endIndex);

		String loggedInStore = "";

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
			String secret = line.get(2);
			String phoneOrEmail = cf.phoneOrEmail(line.get(3));
			String password = line.get(4);
			String name = line.get(5);
			String mobile = line.get(6);
			String pincode = line.get(7);
			String flat = line.get(8);
			String area = line.get(9);
			String subscribe = line.get(10);
			String gv = line.get(11);

			try {

				// login to amazon
				Login login = new Login();

				if (!loggedInStore.equals(storeEmail)) {

					// clear cookies, local storage, session storage if exception occurred in
					// previous record
					cf.clearBrowserStorage();

					failure = login.launchAndLoginStore(storeEmail, storePassword, secret);
				}

				if (failure.isEmpty()) {

					loggedInStore = storeEmail;

					failure = login.launchAndLogin(phoneOrEmail, password);

					if (failure.isEmpty()) {

						// order product
						OrderBeauty orderBeauty = new OrderBeauty();
						failure = orderBeauty.launchAndOrderProduct(name, mobile, pincode, flat, area, subscribe, gv);
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
