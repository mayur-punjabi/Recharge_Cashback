package amazon.login.checkavailability;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import amazon.CommonFunctions;
import framework.constants.WaitType;
import framework.input.Configuration;

public class CheckAvailability extends CommonFunctions implements CheckAvailability_OR {

	public void checkAvailability(List<List<String>> data, String statusFilePath) {

		// wait between multiple iterations
		int defaultCheckAvailabilityWait = 3;

		String checkAvailabilityWait = Configuration.getProperty("checkAvailabilityWait");
		if (checkAvailabilityWait == null || checkAvailabilityWait.trim().isEmpty()) {
			log.error("Taking check availability as " + defaultCheckAvailabilityWait
					+ " minutes as value not present in config");
		} else {

			try {
				defaultCheckAvailabilityWait = Integer.parseInt(checkAvailabilityWait.trim());
			} catch (Exception e) {
				log.error("Taking check availability as " + defaultCheckAvailabilityWait
						+ " minutes as failed to parse the value - " + defaultCheckAvailabilityWait);
			}
		}

		List<List<String>> dataToIterate = new ArrayList<List<String>>();

		// checking if any data is empty
		for (int i = 0; i < data.size(); i++) {
			List<String> row = data.get(i);
			if (row.stream().allMatch(cellData -> cellData.trim().isEmpty())) {
				log.error("Data is empty at row - " + (i + 1));
				continue;
			} else if (row.size() < 2) {
				String dataString = row.stream().collect(Collectors.joining(","));
				log.error("Improper data provided at row - " + (i + 1) + ". Data - " + dataString);
				continue;
			} else {
				dataToIterate.add(row);
			}
		}

		if (dataToIterate.isEmpty()) {
			log.error("Entire data is empty");
			return;
		}

		String product = "";
		String url = "";
		String failure = "";

		// run the execution till it is manually stopped
		while (true) {

			// check the availability of all products
			for (List<String> row : dataToIterate) {

				try {
					// reset the failure and screenshot
					failure = "";
					clearScreenshot();

					// get the product name and url
					product = row.get(0);
					url = row.get(1);

					// launch the product url
					if (launchApplication(url)) {

						waitForPageLoad(120);

						// check if the product is available for subscription
						if (waitForElement(subscribeAndSave, 10, WaitType.visibilityOfElementLocated)) {
							String message = product + " is available for subscription at url - " + url;
							log.debug(message);
							failure = sendToTelegram(message);
						} else {
							failure = product + " is not available";
							reportFailure(failure);
						}
					} else {
						failure = "Failed to launch url - " + url + " for product - " + product;
					}
				} catch (Exception e) {
					failure = "Failed to get the availability for " + product;
					reportFailure(failure, e);
				}

				// update the status file
				if (!failure.isEmpty()) {
					updateStatusFile(statusFilePath, row, failure, 2);
				}

			}

			// wait for few minutes
			pause(defaultCheckAvailabilityWait * 60 * 1000);
		}
	}

	public String sendToTelegram(String message) {

		String failure = "";

		String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
		String apiToken = "5420936301:AAEw9zAREo7LNMJY2RlyJzSSgOJnzvFZjFI";
		String chatId = Configuration.getProperty("telegramGroupLink");
		if (chatId == null || chatId.trim().isEmpty()) {
			failure = "Telegram link not present in config";
		}
		chatId = "@" + chatId.trim();

		urlString = String.format(urlString, apiToken, chatId, message);
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			InputStream is = new BufferedInputStream(conn.getInputStream());
		} catch (Exception e) {
			failure = "Failed to send the telegram message - " + message;
		}

		return failure;
	}
}
