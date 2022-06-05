package suites.execution;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.google.common.io.Files;

import framework.logs.LogUtil;

public class Operations {

	public Logger log;

	public Operations() {
		log = new LogUtil(this.getClass()).logger;
	}

	/**
	 * Reads the CSV file and returns the data
	 * 
	 * @param filePath
	 * @return
	 */
	public List<List<String>> getCSVData(String filePath) {
		List<List<String>> data = new ArrayList<>();
		log.debug("Reading csv file at path - " + filePath);

		try {
			File file = new File(filePath);
			if (file.exists()) {
				data = Files.readLines(file, StandardCharsets.ISO_8859_1).stream()
						.map(line -> line.contains(",") ? Arrays.asList(line.split(","))
								: line.contains(";") ? Arrays.asList(line.split(";")) : Arrays.asList(line.split("\t")))
						.collect(Collectors.toList());
			} else {
				log.error("File at path - " + filePath + " doesn't exist");
			}
		} catch (Exception e) {
			log.error("Exection occurred while reading file at path - " + filePath);
		}

		log.debug("Data from csv at path - " + filePath + ":");
		log.debug(data.stream().map(line -> line.stream().collect(Collectors.joining(",")))
				.collect(Collectors.joining("\n")));

		// removing the header
		if (!data.isEmpty()) {
			data.remove(0);
		}
		return data;
	}

	/**
	 * Creates the status file
	 * 
	 * @param flow
	 * @return file path
	 */
	public String createStatusFile(String flow) {

		Date todaysDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MMMM_yyyy_HH_mm_ss");
		String date = sdf.format(todaysDate);

		String statusDir = "./status/";
		File dir = new File(statusDir);
		String filePath = "./status/" + flow + "_" + date + ".html";
		File file = new File(filePath);

		if (!file.exists()) {
			try {
				if (!dir.exists()) {
					dir.mkdir();
				}
				file.createNewFile();
				String oldFilePath = "./project/baseStatus/" + flow + "Base.html";
				Files.copy(new File(oldFilePath), file);
			} catch (Exception e) {
				log.error("Failed to create file at path - " + filePath, e);
				filePath = "";
			}
		}

		return filePath;
	}

	/**
	 * Clears the CSV file
	 * 
	 * @param filePath
	 */
	public void clearCSVFile(String filePath) {
		log.debug("Clearing csv file at path - " + filePath);

		try {
			File file = new File(filePath);
			if (file.exists()) {
				List<String> data = Files.readLines(file, StandardCharsets.ISO_8859_1);
				log.debug("CSV file data to delete:");
				String header = data.get(0);
				data.clear();
				data.add(header);
				saveData(filePath, data.stream().collect(Collectors.joining("\n")), false);
			} else {
				log.error("File at path - " + filePath + " doesn't exist");
			}
		} catch (Exception e) {
			log.error("Exception occurred while clearing file at path - " + filePath, e);
		}
	}

	/**
	 * Writing data in text file
	 * 
	 * @param filePath   Path of text file to write
	 *
	 * @param strText    Data to store in text file
	 * 
	 * @param appendFlag Flag to clear or append the data in existing file.
	 */
	public boolean saveData(String filePath, String strText, boolean appendFlag) {

		BufferedOutputStream bufferedOutput = null;

		try {

			File file = new File(filePath);

			// check file availability
			if (file.exists()) {

				// check if file is writable
				if (file.canWrite()) {

					// Output Buffer creation
					bufferedOutput = new BufferedOutputStream(new FileOutputStream(filePath, appendFlag));

					// writing string to file
					bufferedOutput.write(strText.getBytes());

					// close file
					bufferedOutput.close();

					log.debug("Writing in text file is completed successfully at path - " + filePath);
					return true;
				} else {
					log.debug("File is not writable at path - " + filePath);
				}
			} else {
				log.error("No such file exists at path - " + filePath);
			}
		} catch (FileNotFoundException e) {
			log.error("File Not Found at path - " + filePath);
		} catch (IOException e) {
			log.error("Exception occurred in reading file at path - " + filePath, e);
		}
		return false;
	}

	/**
	 * Create a single report
	 * 
	 * @param statusFiles
	 */
	public void createSingleStatusFile(List<String> statusFiles) {

		if (statusFiles.size() > 1) {

			String singleStatusFilePath = statusFiles.get(0);
			try {
				File singleStatusFile = new File(singleStatusFilePath);
				Document statusDoc = Jsoup.parse(singleStatusFile, "UTF-8");
				Element mainTableBody = statusDoc.body().selectFirst("tbody");

				for (int i = 1; i < statusFiles.size(); i++) {
					Document currentStatusDoc = Jsoup.parse(new File(statusFiles.get(i)), "UTF-8");
					Elements records = currentStatusDoc.body().select("tr");
					records.remove(0);
					mainTableBody.append(records.toString());
				}

				for (int i = 1; i < statusFiles.size(); i++) {
					File currentStatusFile = new File(statusFiles.get(i));
					currentStatusFile.delete();
				}

				FileUtils.writeStringToFile(singleStatusFile, statusDoc.outerHtml(), "UTF-8");
			} catch (IOException e) {
				log.error("Failed to create single status file at path - " + singleStatusFilePath, e);
			}

		} else {
			log.debug("No status file or single status file");
		}

	}

	/**
	 * Creates the new XML file from old one
	 * 
	 * @param oldXMLPath
	 * @param newXMLPath
	 * @return file create of not
	 */
	public boolean createXML(String oldXMLPath, String newXMLPath) {

		boolean fileCreated = false;

		try {

			log.debug("Started creating new XML");

			// getting the XML data
			File newXML = new File(newXMLPath);
			String oldXMLText = getXMLData(oldXMLPath);

			// updating the parameters
			Document xmlDoc = Jsoup.parse(oldXMLText, "UTF-8", Parser.xmlParser());

			// storing the file
			FileUtils.writeStringToFile(newXML, xmlDoc.outerHtml(), "UTF-8");
			fileCreated = true;

			log.debug("Ended creating new XML\n");

		} catch (Exception e) {
			log.error("Error occured in reading the file at path - " + oldXMLPath, e);
		}
		return fileCreated;
	}

	/**
	 * Gets the data of passed XML
	 * 
	 * @param xmlPath
	 * @return data of the file
	 */
	private String getXMLData(String xmlPath) {

		log.debug("Started getting XML data at path - " + xmlPath);

		StringBuilder xmlText = new StringBuilder();
		try {
			FileReader fr = new FileReader(xmlPath);
			BufferedReader bfr = new BufferedReader(fr);
			String currentLine;
			while ((currentLine = bfr.readLine()) != null) {
				xmlText.append(currentLine);
			}

			fr.close();
			bfr.close();

		} catch (Exception e) {
			log.error("Error occured in reading the file at path - " + xmlPath, e);
		}

		log.debug("Ended getting XML data at path - " + xmlPath);
		return xmlText.toString();
	}

	/**
	 * Updates the passed parameters in passed XML file
	 * 
	 * @param xmlPath    path of XML file
	 * @param parameters <value to search, value to update> Map
	 */
	public void updateXMLParameter(String xmlPath, Map<String, String> parameters) {

		try {

			// getting the XML data
			File xml = new File(xmlPath);
			String xmlText = getXMLData(xmlPath);
			String updateValue;
			String searchValue;
			String tag;

			// updating the parameters
			Document xmlDoc = Jsoup.parse(xmlText, "UTF-8", Parser.xmlParser());
			for (Entry<String, String> entry : parameters.entrySet()) {
				searchValue = entry.getKey();
				updateValue = entry.getValue();
				xmlDoc.select("parameter[name='" + searchValue + "']").attr("value", updateValue);
			}

			// storing in the file
			FileUtils.writeStringToFile(xml, xmlDoc.outerHtml(), "UTF-8");

		} catch (Exception e) {
			log.error("Error occured in reading the file at path - " + xmlPath, e);
		}
	}
}
