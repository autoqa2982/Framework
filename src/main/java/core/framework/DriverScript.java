package core.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lib.Log;
import lib.Stock;
import lib.XL_ReadWrite;
import lib.Log.Level;
import org.testng.TestNG;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import com.google.common.collect.Lists;
import core.framework.ThrowException.TYPE;

public class DriverScript {

	private XL_ReadWrite xlRW;
	private Map<String,String> runOrderDet;
	private String RunXMLFileName;
	private String testConfigPath;
	private Map<String,String> globalParam;

	public static void main(String[] args) throws Exception {
		new DriverScript();		
	}
	
	public DriverScript() throws Exception {
		Log.Report(Level.INFO, Globals.GC_LOG_INIT_MSG);
		// testConfigPath =
		// Globals.GC_TESTCONFIGLOC+Globals.GC_CONFIGFILEANDSHEETNAME+".xls";
		testConfigPath = Globals.GC_TESTCONFIGLOC
				+ Globals.GC_CONFIGFILEANDSHEETNAME + ".properties";
		ReadTestConfig();
		// Checking if RunXML key has value set to "SelectXML" OR "";
		if (Stock.getConfigParam(Globals.GC_KEY_RUNXML).trim()
				.equalsIgnoreCase(Globals.GC_RUNXML_DEFAULT)
				|| Stock.getConfigParam(Globals.GC_KEY_RUNXML).trim()
						.equalsIgnoreCase(Globals.GC_EMPTY)) {
			BuildTestNGXML();
		}
		// Commenting the run part of the testng xml since the run can be
		// initiated by the individual project POM
		// RunTestNG();
	}

/*  ------------------------------------------------------------------------------------------------------------------------------------------------------------
FUNCTION:			ReadTestConfig()	
DESCRIPTION:	    Reading Test Config sheet to access Driver script parameters  
PARAMETERS: 		NA
RETURNS:		    NA    
REVISION HISTORY:   
------------------------------------------------------------------------------------------------------------------------------------------------------------
Author : Souvik     Date : 09-10-2015       
------------------------------------------------------------------------------------------------------------------------------------------------------------
*/	private void ReadTestConfig() throws Exception{
		Stock.getParam(testConfigPath);
		//globalParam = Stock.globalParam;
	}
	
/*  ------------------------------------------------------------------------------------------------------------------------------------------------------------
FUNCTION:			ReadRunOrder(String runOrderFilePath)	
DESCRIPTION:	    Reading Run Order sheet to get a runnable test module names required to build testNG xml  
PARAMETERS: 		String runOrderFilePath
RETURNS:		    NA    
REVISION HISTORY:   
------------------------------------------------------------------------------------------------------------------------------------------------------------
Author : Souvik     Date : 09-10-2015       
------------------------------------------------------------------------------------------------------------------------------------------------------------
*/	private void ReadRunOrder(String runOrderFilePath) throws Exception{
		try{
			xlRW = new XL_ReadWrite(runOrderFilePath);
			runOrderDet = new LinkedHashMap<String,String>();
			String key = Globals.GC_EMPTY;String val = Globals.GC_EMPTY;
			Log.Report(Level.INFO,"reading "+runOrderFilePath+" file to build TestNG XML");
			for(int iROLoop=0;iROLoop<xlRW.getRowCount((Globals.GC_TESTCASERUNORDERPREFIX).split("_")[0])-1;iROLoop++){
				key = xlRW.getCellData((Globals.GC_TESTCASERUNORDERPREFIX).split("_")[0],iROLoop+1,Globals.GC_COLNAME_MODULENAME).trim();
				val = xlRW.getCellData((Globals.GC_TESTCASERUNORDERPREFIX).split("_")[0],iROLoop+1,Globals.GC_COLNAME_RUNSTATUS).trim();
				runOrderDet.put(key,val);			
				Log.Report(Level.DEBUG,"setting @runOrderDet with key :"+key+" -- value :"+val);
			}
			runOrderDet.remove(Globals.GC_EMPTY);
			xlRW.clearXL();
		}catch(Exception e){
			ThrowException.Report(TYPE.EXCEPTION, "Unable to read Run Order sheet :" + e.getMessage());			
		}
	}
	
/*  ------------------------------------------------------------------------------------------------------------------------------------------------------------
FUNCTION:			BuildTestNGXML()
DESCRIPTION:	    Build testNG xml and saves the same to RunXML directory  
PARAMETERS: 		NA
RETURNS:		    NA    
REVISION HISTORY:   
------------------------------------------------------------------------------------------------------------------------------------------------------------
Author : Souvik     Date : 09-10-2015       
------------------------------------------------------------------------------------------------------------------------------------------------------------
*/	private void BuildTestNGXML() throws Exception {
	String autName = Globals.GC_EMPTY;
	String runOrderPath = Globals.GC_EMPTY;
	String testType = Globals.GC_EMPTY;
	String manualTCName = Globals.GC_EMPTY;
	try {
		autName = Stock.getConfigParam(Globals.GC_KEYAUT).toLowerCase();
		if (autName.isEmpty()) {
			throw new Exception("Application name is not provided in TestExecutionConfig sheet");
		}
		runOrderPath = Globals.GC_TESTCASESLOC + Globals.GC_TESTCASERUNORDERPREFIX + autName + ".xls";
		Log.Report(Level.INFO, "building TestNG XML to execute automation tests for application : " + autName);

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// Initializing TestNG XML nodes
		Element suite = doc.createElement(Globals.GC_XML_SUITE);
		Log.Report(Level.DEBUG, "Initialize SUITE node to build TestNG XML ");
		Element listeners = doc.createElement(Globals.GC_XML_LISTENERS);
		Log.Report(Level.DEBUG, "Initialize LISTENRS node to build TestNG XML ");
		Element listener = doc.createElement(Globals.GC_XML_LISTENER);
		Log.Report(Level.DEBUG, "Initialize LISTENER node to build TestNG XML ");
		
		doc.appendChild(suite);
		suite.setAttribute(Globals.GC_XML_ATTR_NAME, Globals.GC_XML_ATTR_VAL_SUITE);
		Log.Report(Level.DEBUG, "Setting attribute to SUITE node (" + Globals.GC_XML_ATTR_NAME + "--"
				+ Globals.GC_XML_ATTR_VAL_SUITE + ") to build TestNG XML");
		suite.setAttribute(Globals.GC_XML_ATTR_VERBOSE, "1");
		suite.appendChild(listeners);
		Log.Report(Level.DEBUG, "Append child node LISTENERS to SUITE node to build TestNG XML ");
		listeners.appendChild(listener);

		Log.Report(Level.DEBUG, "Append child node LISTENER to LISTENERS node to build TestNG XML ");
		listener.setAttribute(Globals.GC_XML_ATTR_CLASSNAME, Globals.GC_LISTENERS_CLASSNAME);
		Log.Report(Level.DEBUG, "Setting attribute to LISTENER node (" + Globals.GC_XML_ATTR_NAME + "--"
				+ Globals.GC_LISTENERS_CLASSNAME + ") to build TestNG XML");

		// Read Run Order
		ReadRunOrder(runOrderPath);

		// Building testNG XML
		xlRW = new XL_ReadWrite(runOrderPath);
		for (Map.Entry<String, String> moduleName : runOrderDet.entrySet()) {				
			if (moduleName.getValue().equalsIgnoreCase(Globals.GC_RUNSTATUS_YES)) {
				if (xlRW.isSheetExist(moduleName.getKey())) { // check if the sheet exist
					
					 // Setting test dependency for each module
					 //Map<Integer, String> depdItr = SetTestCaseDependency(xlRW, moduleName.getKey());
					 // Map sort to provide testcase prioritization
					// Map<Integer, String> treeMap = new TreeMap<Integer, String>();

					 // Looping through Test
					 for (int iModuleLoop=0;iModuleLoop<xlRW.getRowCount(moduleName.getKey())- 1;iModuleLoop++){
						  manualTCName = xlRW.getCellData(moduleName.getKey(),iModuleLoop+1,
								         Globals.GC_XML_ATTR_VAL_TEST_COL_NM);
						  //Creating Methods Tags
						  Element methods = doc.createElement(Globals.GC_XML_METHODS);
						  
						  // Not including Run Status NO scenarios as
						  // exclude tag in not allowing to update
						  // Globals.GC_MANUAL_TC_NAME for following include tags, this leads to 
						  // incorrect data setup for the TC
							if (!manualTCName.equals(Globals.GC_EMPTY) & 
							     !xlRW.getCellData(moduleName.getKey(),iModuleLoop+1,
							     Globals.GC_COLNAME_RUNSTATUS).equalsIgnoreCase(Globals.GC_RUNSTATUS_NO)) {
								// Creating Test Tags
								Element test = doc.createElement(Globals.GC_XML_TEST);
								Log.Report(Level.DEBUG,
										"Initialize TEST node to build TestNG XML for TC :" + manualTCName);
								// Creating Classes Tags
								Element classes = doc.createElement(Globals.GC_XML_CLASSES);
								Log.Report(Level.DEBUG,
										"Initialize CLASSES node to build TestNG XML for TC :" + manualTCName);
								suite.appendChild(test);
								Log.Report(Level.DEBUG, "Append child node TEST to SUITE node to build TestNG XML ");

								test.setAttribute(Globals.GC_XML_ATTR_NAME, manualTCName);
								Log.Report(Level.DEBUG, "Setting attribute to TEST node (" + Globals.GC_XML_ATTR_NAME
										+ "--" + manualTCName + ") to build TestNG XML");
								test.appendChild(classes);
								Log.Report(Level.DEBUG, "Append child node CLASSES to TEST node to build TestNG XML ");

								// Creating class tags	
								Element clazz = doc.createElement(Globals.GC_XML_CLASS);
								classes.appendChild(clazz);
								Log.Report(Level.DEBUG, "append child node CLASS to CLASSES node to build TestNG XML ");
								clazz.setAttribute(Globals.GC_XML_ATTR_NAME, xlRW
										.getCellData(moduleName.getKey(), iModuleLoop + 1, Globals.GC_COLNAME_CLASSPATH)
										.toLowerCase());
								Log.Report(Level.DEBUG, "setting attribute to CLASS node : " + xlRW
										.getCellData(moduleName.getKey(), iModuleLoop + 1, Globals.GC_COLNAME_CLASSPATH)
										.toLowerCase());

								Log.Report(Level.DEBUG, "initialize METHODS node to build TestNG XML ");
								//Appending Methods as Child for Class
								clazz.appendChild(methods);

							} else {
								Log.Report(Level.INFO,
										"Manual Test case name not provided for Test Case :" + xlRW.getCellData(
												moduleName.getKey(), iModuleLoop + 1, Globals.GC_COLNAME_TESTCASES));
							}	
							//Creating Including/Excluding Tags
							if (xlRW.getCellData(moduleName.getKey(),iModuleLoop+1,Globals.GC_COLNAME_RUNSTATUS)
							    .equalsIgnoreCase(Globals.GC_RUNSTATUS_YES)) {
								 Element include = doc.createElement(Globals.GC_XML_INCLUDE);
								 Log.Report(Level.DEBUG,"initialize INCLUDE node to build TestNG XML ");
								 methods.appendChild(include);
								 include.setAttribute(Globals.GC_XML_ATTR_NAME,
										 xlRW.getCellData(moduleName.getKey(),iModuleLoop+1,
										 Globals.GC_COLNAME_TESTCASES));
								 Log.Report(Level.DEBUG,xlRW.getCellData(moduleName.getKey(),iModuleLoop+1,
										 Globals.GC_COLNAME_TESTCASES)+" included for execution");
								 
							}   //else if(xlRW.getCellData(moduleName.getKey(),iModuleLoop+1,Globals.GC_COLNAME_RUNSTATUS)
								//    .equalsIgnoreCase(Globals.GC_RUNSTATUS_NO)){
								//========================================================================
								// Commenting out this section as exclude tag in not allowing to update
								// Globals.GC_MANUAL_TC_NAME for following include tags, this leads to 
								// incorrect data setup for the TC
								//========================================================================
								//Element exclude = doc.createElement(Globals.GC_XML_EXCLUDE);
								//Log.Report(Level.DEBUG,"initialize EXCLUDE node to build TestNG XML ");
								//methods.appendChild(exclude);
								//exclude.setAttribute(Globals.GC_XML_ATTR_NAME,
								//		 xlRW.getCellData(moduleName.getKey(),iModuleLoop+1,
								//		 Globals.GC_COLNAME_TESTCASES));
								//Log.Report(Level.DEBUG,xlRW.getCellData(moduleName.getKey(),iModuleLoop+1,
								//		 Globals.GC_COLNAME_TESTCASES)+" exclude from execution");								
							    //}
					 }
				}else{
					ThrowException.Report(TYPE.EXCEPTION,"Valid module does not exist");
				}
			} else if (moduleName.getValue().equalsIgnoreCase(Globals.GC_RUNSTATUS_NO)) {
				Log.Report(Level.DEBUG, moduleName.getKey() + " module test cases are skipped as RUN STATUS = NO");
			}
		}
		xlRW.saveXL();
		xlRW.clearXL();

		// Transforming to XML
		Transformer docTransformer = TransformerFactory.newInstance().newTransformer();
		Log.Report(Level.DEBUG, "initialize document transformer to create TestNG XML");
		docTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
		docTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
		docTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		docTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		DOMImplementation domImpl = doc.getImplementation();
		DocumentType doctype = domImpl.createDocumentType("suite", "SYSTEM", "http://testng.org/testng-1.0.dtd");
		docTransformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
		docTransformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

		DOMSource source = new DOMSource(doc);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyy_HHmmss");
		Date date = Calendar.getInstance().getTime();
		File file = new File(Globals.GC_TESTNG_XML_PATH);
		Log.Report(Level.DEBUG, "verify if " + file.getPath() + " exists to store TestNG XML");
		if (!file.exists()) {
			file.mkdir();
			Log.Report(Level.DEBUG, "created directory : " + file.getPath() + " to store TestNG XML");
		}
		if (Stock.getConfigParam(Globals.GC_KEYTESTTYPE).equals(Globals.GC_EMPTY)) {
			testType = Globals.GC_DEFAULTTESTTYPE;

		} else {
			testType = Stock.getConfigParam(Globals.GC_KEYTESTTYPE);
		}

		RunXMLFileName = testType + "_" + dateFormat.format(date) + ".xml";
		Log.Report(Level.DEBUG, "finalizing TestNG XML file name : " + RunXMLFileName);

		StreamResult result = new StreamResult(new File(Globals.GC_TESTNG_XML_PATH + "\\" + RunXMLFileName));
		docTransformer.transform(source, result);
		Log.Report(Level.DEBUG, "created " + Globals.GC_TESTNG_XML_PATH + "\\" + RunXMLFileName);

	} catch (Exception e) {
		ThrowException.Report(TYPE.EXCEPTION, "Exception occurred while building TestNG XML : " + e.getMessage());
	}
}

/*  ------------------------------------------------------------------------------------------------------------------------------------------------------------
FUNCTION:			SetTestCaseDependency()
DESCRIPTION:	    Set dependent test cases to run status =YES
PARAMETERS: 		String moduleName
RETURNS:		    NA    
REVISION HISTORY:   
------------------------------------------------------------------------------------------------------------------------------------------------------------
Author : Souvik     Date : 09-10-2015       
------------------------------------------------------------------------------------------------------------------------------------------------------------
*/	@SuppressWarnings("unused")
private Map<Integer,String> SetTestCaseDependency(XL_ReadWrite xlObj, String moduleName) throws Exception {
		int colNumber = 0;String cellRef =Globals.GC_EMPTY;
		String depdTC = Globals.GC_EMPTY;
		Map<Integer,String> itr = new TreeMap<Integer,String>();
		Log.Report(Level.INFO,"setting testcase dependency for module : "+moduleName);
		try{
			for(int iDepTCLoop=1;iDepTCLoop<=xlObj.getRowCount(moduleName);iDepTCLoop++){
				colNumber = xlObj.getColNum(moduleName,iDepTCLoop,Globals.GC_COLNAME_DEPNDTC);	
				depdTC = xlObj.getCellData(moduleName, iDepTCLoop,colNumber);
				
				if(!depdTC.equals(Globals.GC_EMPTY)){	
					if(xlObj.getCellData(moduleName, iDepTCLoop,Globals.GC_COLNAME_RUNSTATUS).
					   equalsIgnoreCase(Globals.GC_RUNSTATUS_YES)){
					   //itr of dependsOnTC	
					   itr.put(Integer.valueOf(xlObj.getCellData(moduleName, iDepTCLoop,Globals.GC_COLNAME_SETPRIORITY)),
							                   xlObj.getCellData(moduleName, iDepTCLoop,Globals.GC_COLNAME_TESTCASES));
					   	
					   Log.Report(Level.DEBUG,"setting formula MATCH(\""+depdTC+"\",A:A,0) to module : "+moduleName+
							   " row: "+iDepTCLoop+ " col: "+ colNumber+1);
					   xlObj.setFormulae(moduleName, iDepTCLoop,colNumber+1,"MATCH(\""+depdTC+"\",A:A,0)");				   
					   cellRef = xlObj.getCellData(moduleName,iDepTCLoop,colNumber+1);
					   Log.Report(Level.DEBUG,"cell reference recieved for evaluating test dependency : "+
					              Globals.GC_COLNAME_RUNSTATUS+" row :"+cellRef);
					   if(!cellRef.equals(Globals.GC_EMPTY)){
						   Log.Report(Level.DEBUG,"setting runstatus : "+Globals.GC_RUNSTATUS_YES);					              
						   xlObj.setCellData(moduleName,Globals.GC_COLNAME_RUNSTATUS,Integer.valueOf(cellRef)-1,Globals.GC_RUNSTATUS_YES);
						   itr.put(Integer.valueOf(xlObj.getCellData(moduleName, Integer.valueOf(cellRef)-1, Globals.GC_COLNAME_SETPRIORITY))
								   ,xlObj.getCellData(moduleName, Integer.valueOf(cellRef)-1,Globals.GC_COLNAME_TESTCASES));
					   }						
					}
				}
			}
			return itr;
		}catch(Exception e){
			ThrowException.Report(TYPE.EXCEPTION, "Exception occurred while setting test case dependency : " + e.getMessage());
		}
		return null;
	}

/*  ------------------------------------------------------------------------------------------------------------------------------------------------------------
FUNCTION:			RunTestNG()
DESCRIPTION:	    Executes saved testNG xml 
PARAMETERS: 		NA
RETURNS:		    NA    
REVISION HISTORY:   
------------------------------------------------------------------------------------------------------------------------------------------------------------
Author : Souvik     Date : 09-10-2015       
------------------------------------------------------------------------------------------------------------------------------------------------------------
*/	@SuppressWarnings("unused")
private void RunTestNG() throws Exception{
	    String resourcePath = Globals.GC_EMPTY;
	    Process proc = null;
		try{
			Log.Report(Level.INFO,"preparing correct TestNG XML to execute");			
			int keyRowNum=0;String command = Globals.GC_EMPTY;
			int fileCounter=0; 
			for(String keyName : globalParam.keySet()){
			    if(keyName.equalsIgnoreCase(Globals.GC_KEY_RUNXML)){break;}
				keyRowNum++;
			}
			
			resourcePath = Stock.getConfigParam("TESTRESOURCES")+"\\";
				
			//setting  data validation for RunXML in test config sheet
			if(Stock.getConfigParam(Globals.GC_KEY_RUNXML).trim().equalsIgnoreCase(Globals.GC_RUNXML_DEFAULT)||
			   Stock.getConfigParam(Globals.GC_KEY_RUNXML).trim().equalsIgnoreCase(Globals.GC_EMPTY)){				
			   Log.Report(Level.DEBUG,"Setting data validation for RunXML in test config sheet ");
				
			   ArrayList<String> runXMLFileName = new ArrayList<String>();
			   File[] files = new File(Globals.GC_TESTNG_XML_PATH).listFiles();
			   for (File file : files) {
			     if (file.isFile()) {runXMLFileName.add(file.getName());}
			   }
			   
			   runXMLFileName.add(Globals.GC_RUNXML_DEFAULT);							   
			   xlRW = new XL_ReadWrite(testConfigPath);
			   xlRW.setDataValidation(Globals.GC_CONFIGFILEANDSHEETNAME,keyRowNum+1,Globals.GC_COLNAME_VALUE,runXMLFileName);
			   xlRW.setCellData(Globals.GC_CONFIGFILEANDSHEETNAME, Globals.GC_COLNAME_VALUE, keyRowNum+1, RunXMLFileName);
			//   xlRW.cell.setCellValue(RunXMLFileName);
			   xlRW.saveXL();
			}
		
			//To check if the mandatory test execution jar is available at right location
			//also provides flexibility to execute the scripts from anywhere (IDE or batch) 
			File jarFileDir = new File(Globals.GC_PROJECT_DIR);
			File[] jarFileList = jarFileDir.listFiles();		
			for (File file : jarFileList) {
			    if (file.isFile()) {
			       if (file.getName().endsWith(".jar")) {		
			    	   fileCounter = fileCounter+1;
			       }
			    } 
			}
						
			xlRW = new XL_ReadWrite(testConfigPath);
			String xmlFinaltoExecute =  xlRW.getCellData(Globals.GC_CONFIGFILEANDSHEETNAME, keyRowNum+1, Globals.GC_COLNAME_VALUE);
		    if(fileCounter==1){
		    	 command = "cmd /c rmdir /s /q "+Globals.GC_TESTNG_TEST_OUTPUT+" & copy /y "+Globals.GC_PROJECT_DIR+"*.jar "+
					     resourcePath;
		    	 proc  = Runtime.getRuntime().exec(command);
		    			    	
		    }else if(fileCounter > 1){
		    	System.out.println("Error: Only one JAR (the correct one!) file should be present in the directory : "+ 
	                      Globals.GC_PROJECT_DIR);
		    	ThrowException.Report(TYPE.INTERRUPTED, "Error: Only one JAR (the correct one!) file should be present in the directory : "+ 
	                        Globals.GC_PROJECT_DIR);	
		    }
		    		   
			//xlRW.cell.setCellValue(Globals.GC_RUNXML_DEFAULT);
		    xlRW.setCellData(Globals.GC_CONFIGFILEANDSHEETNAME, Globals.GC_COLNAME_VALUE, keyRowNum+1, Globals.GC_RUNXML_DEFAULT);
			xlRW.saveXL();
			
			//Executes TestNGXML
			TestNG testng = new TestNG();
			List<String> suites = Lists.newArrayList();
		    suites.add(Globals.GC_TESTNG_XML_PATH+"\\"+xmlFinaltoExecute);
		    testng.setTestSuites(suites);
			Log.Report(Level.DEBUG,"Executing TestNG XML : "+Globals.GC_TESTNG_XML_PATH+"\\"+xmlFinaltoExecute);	
		    testng.run();
			
								
			// Displaying console output 

			BufferedReader stdInput = new BufferedReader(new 
				     InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new 
			     InputStreamReader(proc.getErrorStream()));

			// Read all outputs from the command
			String s = null;
			while ((s = stdInput.readLine()) != null) {
			    System.out.println(s);
			}

			// Read all errors if any from the attempted command
			while ((s = stdError.readLine()) != null) {
			    System.out.println(s);
			}
		}catch(Exception e){
			ThrowException.Report(TYPE.EXCEPTION, "Exception occurred while running TestNG XML : " + e.getMessage());
		}
	}
}
