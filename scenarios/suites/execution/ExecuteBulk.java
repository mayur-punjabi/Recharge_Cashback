package suites.execution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import framework.input.Configuration;

public class ExecuteBulk extends Operations {

	public static void main(String[] args) {
		ExecuteBulk eb = new ExecuteBulk();
		eb.executeSuite(args[0]);
	}

	/**
	 * Executes the suites
	 * 
	 * @param suite CashbackSuite or RechargeSuite
	 */
	private void executeSuite(String suite) {

		String flow = suite.replace("Suite", "").toLowerCase();
		List<String> statusFiles = new ArrayList<>();

		String csvFilePath = "./excel/" + flow + ".csv";
		List<List<String>> data = getCSVData(csvFilePath);

		int totalRecords = data.size();
		int noOfExecutions = Integer.parseInt(Configuration.getProperty("noOfExecutions"));
		int noOfRecordsPerExecution = (int) Math.floor(totalRecords / noOfExecutions);

		List<Process> aliveExecutions = new CopyOnWriteArrayList<>();

		for (int division = 0; division < noOfExecutions; division++) {

			int starting = (division * noOfRecordsPerExecution) + 1;
			int ending = division == noOfExecutions - 1 ? totalRecords : (division + 1) * noOfRecordsPerExecution;

			String statusFilePath = createStatusFile(flow);
			statusFiles.add(statusFilePath);

			try {
				Process p = Runtime.getRuntime().exec("cmd /c start /wait cmd.exe /c java -jar project/execute.jar "
						+ suite + " " + starting + " " + ending + " " + statusFilePath);
				aliveExecutions.add(p);

				try {
					Thread.sleep(2000);
				} catch (InterruptedException ie) {
					log.debug("Error occurred while waiting for execution to complete", ie);
				}
			} catch (IOException e) {
				log.error("Failed to start execution for suite - " + suite + " starting - " + starting + " ending - "
						+ " status file path" + statusFilePath, e);
			}
		}

		log.debug("Waiting for " + aliveExecutions.size() + " executions to complete");
		while (!aliveExecutions.isEmpty()) {
			aliveExecutions.forEach((process) -> {
				if (!process.isAlive()) {
					aliveExecutions.remove(process);
				}
			});

			try {
				Thread.sleep(5000);
			} catch (InterruptedException ie) {
				log.debug("Error occurred while waiting for execution to complete", ie);
			}
		}

		// create single status file
		createSingleStatusFile(statusFiles);

		// clear the CSV file
		clearCSVFile(csvFilePath);
	}
}
