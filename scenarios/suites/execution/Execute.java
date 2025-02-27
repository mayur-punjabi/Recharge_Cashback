package suites.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class Execute {

	public static void main(String[] args) {

		executeSuite(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
	}

	/**
	 * Executes the suites
	 * 
	 * @param suite CashbackSuite or RechargeSuite
	 */
	private static void executeSuite(String suite, String isParallel, String starting, String ending,
			String statusFilePath, String isAndroid, String isStore) {

		TestNG testng = new TestNG();

		List<XmlSuite> xmlSuites = new ArrayList<>();
		List<XmlClass> xmlClasses = new ArrayList<>();

		// create suite
		XmlSuite xmlSuite = new XmlSuite();
		xmlSuite.setName(suite);

		// add parameters to the suite
		Map<String, String> suiteParameters = new HashMap<>();
		suiteParameters.put("isAndroid", isAndroid);
		suiteParameters.put("isStore", isStore);
		xmlSuite.setParameters(suiteParameters);

		// create test
		XmlTest xmlTest = new XmlTest(xmlSuite);
		xmlTest.setName(suite);

		// add parameters to the test
		Map<String, String> parameters = new HashMap<>();
		parameters.put("isParallel", isParallel);
		parameters.put("starting", starting);
		parameters.put("ending", ending);
		if (statusFilePath.equals("empty")) {
			statusFilePath = "";
		}
		parameters.put("statusFilePath", statusFilePath);
		xmlTest.setParameters(parameters);

		// create class
		XmlClass xmlClass = new XmlClass("suites.appsuites." + suite);

		// add class to list
		xmlClasses.add(xmlClass);

		// add list of class to test
		xmlTest.setXmlClasses(xmlClasses);

		// add suite to suite list
		xmlSuites.add(xmlSuite);

		// add suite list to testng and execute
		testng.setXmlSuites(xmlSuites);

		// avoiding test-output folder generation
		testng.setUseDefaultListeners(false);

		// executing suite
		testng.run();
	}
}
