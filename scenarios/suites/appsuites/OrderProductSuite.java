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

		String csvFilePath = "./excel/orderproduct.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("orderproduct");
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

			if (line.size() < 5) {
				String dataString = line.stream().collect(Collectors.joining(","));
				cf.log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				failure = "Improper data provided. Data - " + dataString;
			}

			String storeEmail = cf.phoneOrEmail(line.get(0));
			String storePassword = line.get(1);
			String phoneOrEmail = cf.phoneOrEmail(line.get(2));
			String password = line.get(3);
			String gv = line.get(4);

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

					loggedInStore = storeEmail;

					failure = login.launchAndLogin(phoneOrEmail, password);

					if (failure.isEmpty()) {

						// order product
						OrderProduct orderProduct = new OrderProduct();
						failure = orderProduct.launchAndOrderProduct(gv);
					}
				}

				// logout from amazon
				cf.logout();
			} catch (Exception e) {
				failure = "Failed to order product";
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
