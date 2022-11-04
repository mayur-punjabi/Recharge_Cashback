package suites.execution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import framework.input.Configuration;

public class ExecuteBulk extends Operations {

	public static void main(String[] args) {
		ExecuteBulk eb = new ExecuteBulk();
		eb.executeSuite(args[0], args[1], args[2]);
	}

	/**
	 * Executes the suites
	 * 
	 * @param suite CashbackSuite or RechargeSuite
	 */
	private void executeSuite(String suite, String isAndroid, String isStore) {

		String flow = suite.replace("Suite", "").toLowerCase();
		List<String> statusFiles = new ArrayList<>();

		String csvFilePath = "./excel/" + flow + ".csv";
		List<List<String>> data = getCSVData(csvFilePath);

		int totalRecords = data.size();

		List<Process> aliveExecutions = new CopyOnWriteArrayList<>();

		Map<Integer, Map<String, Integer>> divisionMap = getDivsion(totalRecords);
		log.debug(divisionMap.toString());

		for (Entry<Integer, Map<String, Integer>> entry : divisionMap.entrySet()) {

			int starting = entry.getValue().get("starting");
			int ending = entry.getValue().get("ending");

			String statusFilePath = createStatusFile(flow);
			statusFiles.add(statusFilePath);

			try {
				Process p = Runtime.getRuntime()
						.exec("cmd /c start /wait cmd.exe /c java -jar project/execute.jar " + suite + " true "
								+ starting + " " + ending + " " + statusFilePath + " " + isAndroid + " " + isStore);
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

	private Map<Integer, Map<String, Integer>> getDivsion(int totalRecords) {

		Map<Integer, Map<String, Integer>> divisionMap = new HashMap<>();

		int noOfExecutions = Integer.parseInt(Configuration.getProperty("noOfExecutions"));

		if (totalRecords <= noOfExecutions) {

			noOfExecutions = totalRecords;

			for (int division = 0; division < noOfExecutions; division++) {

				int starting = division + 1;
				int ending = division + 1;

				divisionMap.put(division, new HashMap<>());
				divisionMap.get(division).put("starting", starting);
				divisionMap.get(division).put("ending", ending);
			}
		} else {

			int range = 0;
			int remiander = totalRecords % noOfExecutions;
			totalRecords = totalRecords - remiander;
			int noOfEnteriesPerExecution = totalRecords / noOfExecutions;

			for (int division = 0; division < noOfExecutions; division++) {

				if (range == 0) {
					range++;
				}

				int starting = range;
				int ending = range + noOfEnteriesPerExecution - 1;
				range = range + noOfEnteriesPerExecution;

				if (division < remiander) {
					ending++;
					range++;
				}

				divisionMap.put(division, new HashMap<>());
				divisionMap.get(division).put("starting", starting);
				divisionMap.get(division).put("ending", ending);
			}
		}

		return divisionMap;
	}
}
