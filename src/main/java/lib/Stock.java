package lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import lib.Log.Level;
import core.framework.Globals;
import core.framework.ThrowException;
import core.framework.ThrowException.TYPE;

public class Stock {
	private static Map<String, String> globalParam = new LinkedHashMap<String, String>();
	private static int dataProviderIterations;
	public static Map<String, String> globalTestdata;
	public static String globalManualTCName;
	private static int iterationNumber = 0;

	public static Object[][] setDataProvider(
			LinkedHashMap<Integer, Map<String, String>> dataObj) {
		// Converting Map to Object[][] to handle @DataProvider
		Object[][] tdObject = null;
		try {
			if (dataObj != null) {
				tdObject = new Object[dataObj.size()][2];
				int count = 0;
				for (Map.Entry<Integer, Map<String, String>> entry : dataObj
						.entrySet()) {
					tdObject[count][0] = entry.getKey();
					tdObject[count][1] = entry.getValue();
					count++;
				}
				dataProviderIterations = tdObject.length;
				return tdObject;
			}
		} catch (Exception e) {
			ThrowException.Report(
					TYPE.EXCEPTION,
					"Exception occurred while setting DataProvider : "
							+ e.getMessage());
		}
		return new Object[][] { {} }; // As null cannot be returned in
										// DataProvider
	}

	public static int getIterations() {
		return dataProviderIterations;
	}

	private static String checkEnv(String envName) {
		if (envName.contains("PROJ")) {
			return Globals.DB_TYPE.get("PROJ");
		}
		if (envName.contains("QA")) {
			return Globals.DB_TYPE.get("QA");
		}
		if (envName.contains("PROD")) {
			return Globals.DB_TYPE.get("PROD");
		}
		return null;
	}

	public static String[] getTestQuery(String queryName) {
		try {
			String[] queryData = new String[3];
			String appName = getConfigParam("AUT");

			File xmlfile = new File(Globals.GC_TESTDATALOC
					+ Globals.GC_TESTDATAPREFIX + appName + "_"
					+ checkEnv(getConfigParam("TEST_ENV")) + ".xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlfile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Module");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				Element eElement = (Element) nNode;
				String name = eElement.getAttribute("name");
				if (name.equalsIgnoreCase("query")) {
					NodeList parameterList = eElement
							.getElementsByTagName("parameter");

					for (int temp1 = 0; temp1 < parameterList.getLength(); temp1++) {
						Node pNode = parameterList.item(temp1);
						Element pElement = (Element) pNode;

						String pName = pElement.getAttribute("QueryName");
						if (pName.equalsIgnoreCase(queryName)) {
							queryData[0] = pElement.getAttribute("DB");
							queryData[1] = pElement.getAttribute("Query");
						}
					}
				}
			}
			return queryData;
		} catch (Exception e) {
			ThrowException.Report(TYPE.EXCEPTION, "Unable to get test query :"
					+ e.getMessage());
		}
		return null;

	}
	 
	public static Map<String, String> getLoopIndex(String indexString) {
		String GET_INDEX_PATTERN = "[0-9,>temp]+";
		String[] arrFirstSplit = null;
		Pattern pattern = null;
		Matcher matcher = null;
		Map<String, String> mapIndex = new LinkedHashMap<String, String>();
		Log.Report(Level.INFO, "computing pattern : " + indexString
				+ " to get test data index");
		if (!indexString.contains(",")) {
			indexString = indexString + ",temp";
		}
		try {
			pattern = Pattern.compile(GET_INDEX_PATTERN);
			matcher = pattern.matcher(indexString);
			if (!matcher.matches()) {
				ThrowException
						.Report(TYPE.ILLEGALSTATE,
								"Pattern mismatch found for getLoopIndex() -"
										+ " Only allowed characters: '0-9,>' and no consecutive repetions of operator is allowed (>>)");
			}
			if (indexString.contains(",")) {
				arrFirstSplit = indexString.split(",");
			} else { // to do else if
				arrFirstSplit = (indexString).split(">");
			}

			if (arrFirstSplit.length > 1) {
				String[] arrayOfString1;
				int j = (arrayOfString1 = arrFirstSplit).length;
				for (int i = 0; i < j; i++) {
					String str = arrayOfString1[i];
					if (str.split(">").length - 1 != 0) {
						int varA = Integer.parseInt(str.split(">")[1]);
						int varB = Integer.parseInt(str.split(">")[0]);
						if (varA - varB > 0) {
							for (int iLoop = 0; iLoop <= varA - varB; iLoop++) {
								mapIndex.put(Integer.toString(varB + iLoop), "");
							}
						}
					} else {
						mapIndex.put(str, "");
					}
				}
			}
			mapIndex.remove("");
			mapIndex.remove("temp");
			return mapIndex;
		} catch (Exception e) {
			ThrowException.Report(TYPE.EXCEPTION,
					"Exception at getLoopIndex() : " + e.getMessage());
		}
		return null;
	}

	public static void getParam(String configPath) {
		readConfigProperty(configPath);
	}

	public static String GetParameterValue(String strParamName) {
		String value = null;
		if (globalTestdata.containsKey(strParamName.toUpperCase().trim())) {
			if (globalTestdata.get(strParamName.toUpperCase().trim()).length() > 0)
				value = globalTestdata.get(strParamName.trim().toUpperCase());
		} else {
			throw new Error(
					"Parameter '"
							+ strParamName
							+ "' does not exist in Test data!\nStopping script execution!");
		}
		return value;
	}

	/**
	 * <pre>
	 * Method to set config property in globalParam map
	 * </pre>
	 * 
	 * <pre>
	 * If overWriteExisting is 
	 * @param parameterName
	 * @param parameterValue
	 * @param overWriteExisting
	 * 
	 * <pre>
	 * <b> - true</b> to update the property value even if the property already exists.
	 * <b> - false</b> to ignore updating value to already existing property.
	 * Default is <b>true</b>
	 * </pre>
	 */
	public static void setConfigParam(String parameterName,
			String parameterValue, boolean... overWriteExisting) {

		if (overWriteExisting.length > 0) {
			if (!overWriteExisting[0])
				if (Stock.globalParam.containsKey(parameterName.trim()
						.toUpperCase())) {
					return;
				}
		}
		Stock.globalParam.put(parameterName.trim().toUpperCase(),
				parameterValue);
	}

	/**
	 * <pre>
	 * This method returns the testdata for the current iteration to access them in @Beforemethod
	 * </pre>
	 * 
	 * @param globalTestData
	 *            <pre>
	 * It is the testdata map for the respective testcase
	 * </pre>
	 * @return <pre>
	 * <b>Returns the data for the running iteration number</b>
	 * </pre>
	 */
	public static Map<String, String> getIterationTestData(
			LinkedHashMap<Integer, Map<String, String>> globalTestData) {
		LinkedList<Integer> keyList = new LinkedList<>(globalTestData.keySet());
		int index = keyList.get(iterationNumber);
		iterationNumber++;
		return globalTestData.get(index);
	}

	public static Map<String, String> getGlobalParam() {
		return globalParam;
	}

	public static void setGlobalParam(Map<String, String> globalParam) {
		Stock.globalParam = globalParam;
	}

	public static String getConfigParam(String parameterName) {
		return globalParam.get(parameterName.trim().toUpperCase());
	}

	/**
	 * <pre>
	 * Method to read Config property file to get all the config parameter into 
	 * <b>globalparam<String,String> </b> Map,which is being used to get Config paramaeter.
	 * </pre>
	 * 
	 * @param configPath
	 *            <pre>
	 * This indicates the <b>testexecutionconfig.properties</b> file location
	 * </pre>
	 * @author ranjan
	 */
	public static void readConfigProperty(String configPath) {
		Properties prop = new Properties();
		InputStream ins = null;
		if (getGlobalParam().isEmpty()) {
			try {
				ins = new FileInputStream(configPath);
				Log.Report(Level.INFO, "reading " + configPath + " file");
				prop.load(ins);
				Enumeration<?> e = prop.propertyNames();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					String val = prop.getProperty(key);
					Stock.getGlobalParam().put(
							key.toString().trim().toUpperCase(), val);
					Log.Report(Level.DEBUG, "setting @globalParam with key :"
							+ key + " -- value :" + val);
					Stock.getGlobalParam().remove(Globals.GC_EMPTY);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Exception e) {
				ThrowException.Report(TYPE.EXCEPTION, "Unable to read Config :"
						+ e.getMessage());
			} finally {
				if (ins != null) {
					try {
						ins.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	public static String getTestDataAsString() throws Exception {
		String printTestData="";
		for (Map.Entry<String, String> entry : Stock.globalTestdata.entrySet()) {
			if(!entry.getKey().equalsIgnoreCase("PASSWORD"))
				printTestData=printTestData+entry.getKey() + "="+ entry.getValue() +"\n";
		}
	 return printTestData;
	}

	/**
	 * <pre>Method to get test data</pre>
	 * @param tcAbsPath
	 * @param tcName
	 * @return
	 */
	public static LinkedHashMap<Integer, Map<String, String>> getTestData(
			String tcAbsPath, String tcName) {
		Stock.iterationNumber = 0;
		Log.Report(Level.INFO, Globals.GC_LOG_INITTC_MSG + tcAbsPath + "."
				+ tcName + Globals.GC_LOG_INITTC_MSG);
		// Getting Application name and Module name so that the
		// correct excel is picked up
		LinkedHashMap<Integer, Map<String, String>> td = null;
		Map<String, String> mapData = null;
		String appName = getConfigParam("AUT");
		String modName = tcAbsPath.split("\\.")[(tcAbsPath.split("\\.").length) - 1];

		boolean ifTCFound = false;
		Log.Report(Level.DEBUG, "Preparing test data for Test Case : " + tcName);

		try {
			XL_ReadWrite XL ;
			
			if (System.getProperties().containsKey("testDataPath")
					&& System.getProperty("testDataPath").equalsIgnoreCase(
							"true")) {
				XL = new XL_ReadWrite(Globals.GC_COMMON_TESTDATALOC + appName
						+ "\\\\" + Globals.GC_TESTDATAPREFIX + appName + "_"
						+ checkEnv(getConfigParam("TEST_ENV")) + ".xls");
			} else {
				XL = new XL_ReadWrite(Globals.GC_TESTDATALOC
						+ Globals.GC_TESTDATAPREFIX + appName + "_"
						+ checkEnv(getConfigParam("TEST_ENV")) + ".xls");
			}

			int manualTCColNo = XL.getColNum(modName, 0,
					Globals.GC_COL_MANUAL_TC);
			int itrColNo = XL.getColNum(modName, 0, Globals.GC_ITRCCOLNAME);
			int itcPointer = 0;

			// Finding the TC in given TestData sheet
			for (; itcPointer <= XL.getRowCount(modName); itcPointer++) {
				if (XL.getCellData(modName, itcPointer, manualTCColNo).trim()
						.equalsIgnoreCase(tcName)) {
					Log.Report(Level.DEBUG, tcName + " found in " + modName
							+ " sheet for row : " + itcPointer);
					ifTCFound = true;
					break;
				}
			}

			if (ifTCFound) { // Loop to TD index
				String strLoopPattern = XL.getCellData(modName, itcPointer,
						itrColNo).trim();
				int counter = 0;
				// Run ALL Test Data iteration
				if (strLoopPattern.equals(Globals.GC_EMPTY)
						|| strLoopPattern
								.equalsIgnoreCase(Globals.GC_VAL_RUNALLITR)) {
					for (int iLoop = itcPointer; iLoop <= XL
							.getRowCount(modName); iLoop++) {
						if (XL.getCellData(modName, iLoop + 2, manualTCColNo)
								.trim().equals(tcName)) {
							counter = counter + 1;
						} else if (XL
								.getCellData(modName, iLoop + 2, manualTCColNo)
								.trim().equals(Globals.GC_EMPTY)) {
							// breaking out after all row count
							break;
						}
					}
					if (counter > 1) {
						strLoopPattern = ("1>" + String.valueOf(counter));
					} else {
						strLoopPattern = "1";
					}
				}

				td = new LinkedHashMap<Integer, Map<String, String>>();
				Map<String, String> testDataItr = getLoopIndex(strLoopPattern);

				for (String itrNo : testDataItr.keySet()) {
					mapData = new HashMap<String, String>();
					// Loop to point to TC iterations
					for (int iSelIndx = itcPointer + 2; iSelIndx <= XL
							.getRowCount(modName); iSelIndx++) {
						if (itrNo.equals(XL.getCellData(modName, iSelIndx,
								itrColNo))) {
							// Loop through the column to generate Key,Value
							// pair TD map
							for (int iColCnt = itrColNo + 1; iColCnt <= XL
									.getColCount(iSelIndx - 1, modName); iColCnt++) {
								if (!XL.getCellData(modName, itcPointer + 1,
										iColCnt).trim()
										.equals(Globals.GC_EMPTY)) {
									mapData.put(
											XL.getCellData(modName,
													itcPointer + 1, iColCnt)
													.trim().toUpperCase(),
											XL.getCellData(modName, iSelIndx,
													iColCnt).trim());
									Log.Report(
											Level.DEBUG,
											"test data mapped for index "
													+ itrNo
													+ " with key "
													+ XL.getCellData(modName,
															itcPointer + 1,
															iColCnt)
													+ " and value "
													+ XL.getCellData(modName,
															iSelIndx, iColCnt));
								}
							}
							mapData.remove("");
							td.put(Integer.valueOf(itrNo), mapData);
							break;
						}
					}
				}
				XL.clearXL();
			} else {
				Log.Report(Level.DEBUG, tcName + " not found in test data : "
						+ modName + " sheet");
			}
			return td;
		} catch (Exception e) {
			ThrowException.Report(
					TYPE.EXCEPTION,
					"Exception occurred while preparing test data : "
							+ e.getMessage());
		}
		return null;
	}

}
