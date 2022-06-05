package suites.execution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import framework.input.Configuration;

public class ExecuteBulk2 extends Operations {

	public static void main(String[] args) {
		ExecuteBulk2 eb = new ExecuteBulk2();
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

			// generating new XML path for execution
			String runXMLPath = "./project/xml/" + flow + "/run.xml";
			StringBuilder newString = new StringBuilder(runXMLPath);
			newString.insert(runXMLPath.lastIndexOf("xml") - 1, String.valueOf(starting) + String.valueOf(ending));
			String newXMLPath = newString.toString();

			if (createXML(runXMLPath, newXMLPath)) {

				// update the XML file for execution
				Map<String, String> parameters = new HashMap<>();
				parameters.put("isParallel", String.valueOf(true));
				parameters.put("starting", String.valueOf(starting));
				parameters.put("ending", String.valueOf(ending));
				parameters.put("statusFilePath", statusFilePath);
				updateXMLParameter(newXMLPath, parameters);

				// execute
				try {
					Process p = Runtime.getRuntime()
							.exec("cmd /c start /wait cmd.exe /c mvn test -Dfile=" + newXMLPath);
					aliveExecutions.add(p);

					try {
						Thread.sleep(2000);
					} catch (InterruptedException ie) {
						log.debug("Error occurred while waiting for execution to complete", ie);
					}
				} catch (IOException e) {
					log.error("Failed to start execution for suite - " + suite + " starting - " + starting
							+ " ending - " + " status file path" + statusFilePath, e);
				}
			}

//			String jarAbsolutePath = new File("project/execute.jar").getAbsolutePath();
//			try {
////				ProcessBuilder pb = new ProcessBuilder("java", "-jar",
////						jarAbsolutePath + suite + " " + starting + " " + ending + " " + statusFilePath);
////				Process p = pb.start();
//				Runtime.getRuntime().exec("cd " + new File("project/execute.jar").getParent());
//				Process p = Runtime.getRuntime().exec("java -jar " + jarAbsolutePath + " " + suite + " " + starting
//						+ " " + ending + " " + statusFilePath);
//				aliveExecutions.add(p);
//			} catch (IOException e) {
//				log.error("Failed to start execution for suite - " + suite + " starting - " + starting + " ending - "
//						+ " status file path" + statusFilePath, e);
//			}

//			try {
//				Process p = Runtime.getRuntime().exec("cmd /c start /wait cmd.exe /c java -jar project/execute.jar "
//						+ suite + " " + starting + " " + ending + " " + statusFilePath);
//				aliveExecutions.add(p);
//			} catch (IOException e) {
//				log.error("Failed to start execution for suite - " + suite + " starting - " + starting + " ending - "
//						+ " status file path" + statusFilePath, e);
//			}
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
