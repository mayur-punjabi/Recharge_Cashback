package suites.appsuites;

import java.util.List;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import amazon.login.checkavailability.CheckAvailability;
import suites.basesuite.BaseSuite;

public class CheckAvailabilitySuite extends BaseSuite {

	@Test(priority = 0)
	@Parameters({ "isParallel", "starting", "ending", "statusFilePath" })
	public void checkAvailibility(String isParallel, String starting, String ending, String statusFilePath) {

		String csvFilePath = "./excel/checkAvailability.csv";
		List<List<String>> data = cf.getCSVData(csvFilePath);
		int startIndex = 1;
		int endIndex = data.size();
		if (Boolean.parseBoolean(isParallel)) {
			cf.log.debug("Not clearing the CSV file");
			startIndex = Integer.parseInt(starting);
			endIndex = Integer.parseInt(ending);
		} else {
			cf.clearCSVFile(csvFilePath);
			statusFilePath = cf.createStatusFile("checkAvailability");
		}

		cf.log.debug(startIndex + " " + endIndex);

		String failure = "";
		if (data.isEmpty()) {
			cf.log.error("Data is empty");
			return;
		}

		try {
			CheckAvailability ca = new CheckAvailability();
			ca.checkAvailability(data, statusFilePath);
		} catch (Exception e) {
			failure = "Failed to check the availability of the products";
			cf.reportFailure(failure, e);
		}
	}
}
