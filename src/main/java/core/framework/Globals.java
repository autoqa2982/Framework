package core.framework;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import lib.Stock;


public class Globals {
       
    // General
          public static final String GC_EMPTY = "";
          public static final boolean GC_FALSE = false;
          public static final boolean GC_TRUE = true;
          public static final String GC_KEYAUT = "AUT";
          public static final String GC_KEYTESTTYPE = "TESTTYPE";
          public static final String GC_DEFAULTTESTTYPE = "DefaultTest";
          public static final String GC_KEY_RUNXML = "RUNXML";
          public static final String GC_VAL_RUNALLITR = "ALL";
          public static final String GC_AUT_TYPE = "WEB";
          public static final Map<String,String> DB_TYPE;
          static{
        	  DB_TYPE = new HashMap<String,String>();
        	  DB_TYPE.put("PROJ","DEV");
        	  DB_TYPE.put("QA","QA");        	  
          }
	  	   // FOR CI 
          public static final String GC_COLNAME_TEST_ENV="TEST_ENV";
          public static final String GC_EXECUTION_ENVIRONMENT=System.getProperties().containsKey("env")?System.getProperty("env").toUpperCase(): Stock.getConfigParam(GC_COLNAME_TEST_ENV);


          
		//SpreadSheet
          public static final String GC_CONFIGFILEANDSHEETNAME = "testexecutionconfig";
          public static final String GC_DATASHEET_ERR = "Sheet not found";
          public static final String GC_TESTCASERUNORDERPREFIX = "runorder_";
          public static final String GC_TESTDATAPREFIX = "testdata_";
          public static final String GC_TESTDATATCCOLNAME = "testcasename";
          public static final String GC_ITRCCOLNAME = "iteration";      
          public static final String GC_COLNAME_CONFIG = "config";
          public static final String GC_COLNAME_VALUE = "value";
          public static final String GC_COLNAME_MODULENAME = "modulename";
          public static final String GC_COLNAME_RUNSTATUS = "runstatus";
          public static final String GC_COLNAME_TESTCASES = "testcases";
          public static final String GC_COLNAME_SETPRIORITY = "setpriority";
          public static final String GC_COLNAME_DEPNDTC = "dependenttestcase";
          public static final String GC_RUNSTATUS_YES = "YES";
          public static final String GC_RUNSTATUS_NO = "NO";
          public static final String GC_RUNXML_DEFAULT = "SelectXML";
          public static final String GC_XML_ATTR_VAL_TEST_COL_NM = "ManualTestcases";
          public static final String GC_COLNAME_CLASSPATH = "ClassPath";
          public static String GC_OBJECT_SYNC_TIMEOUT = null ;
          
    //FileLocations
          public static final String GC_TESTCASESLOC = System.getProperty("user.dir")+"\\RunOrderSheets\\";
          public static final String GC_TESTDATALOC = System.getProperty("user.dir")+"\\TestData\\";
          public static final String GC_TESTNG_XML_PATH = System.getProperty("user.dir")+"\\RunXML";
          public static final String GC_PROJECT_BIN_DIR = System.getProperty("user.dir")+"\\bin";         
          public static final String GC_TESTCASE_RELPATH =  ".testcases.";
          public static final String GC_LISTENERS_CLASSNAME =  "core.framework.TestListener";      
        //  public static final String GC_APP_RELPATH =  "app.";
          public static final String GC_PROJECT_DIR = System.getProperty("user.dir")+"\\";
          public static final String GC_TESTCONFIGLOC = GC_PROJECT_DIR;
          public static final String GC_TESTNG_TEST_OUTPUT = System.getProperty("user.dir")+"\\test-output";
          public static final String GC_TEST_REPORT_DIR =  GC_PROJECT_DIR+"TestReport\\";
          public static final String GC_TEST_REPORT_RESOURCE = GC_PROJECT_DIR+"src\\devresource\\reports\\";
          public static final String GC_TEST_REPORT_CSS_RESOURCE = GC_TEST_REPORT_RESOURCE+"CSS";
          public static final String GC_TEST_REPORT_JS_RESOURCE = GC_TEST_REPORT_RESOURCE+"JS";
          public static final String GC_TEST_REPORT_FONTS_RESOURCE = GC_TEST_REPORT_RESOURCE+"FONTS";
          public static final String GC_TEST_REPORT_HTML_RESOURCE = GC_TEST_REPORT_RESOURCE+"HTML";
          public static final String GC_TEST_REPORT_IMG_RESOURCE = GC_TEST_REPORT_RESOURCE+"IMG";
          public static String GBL_SuiteName = "";
          
//          public static String GBL_REPLACE_EXISTING_HTML_REPORT = "true";
          public static String GBL_REPLACE_EXISTING_HTML_REPORT = Stock.getConfigParam("Overwrite_Existing_Report");
          public static String GBL_TestCaseName = "";
          public static int GBL_CurrentIterationNumber = 1;
          public static String GBL_strScreenshotsFolderPath = "";
          public static Exception exception = null;
          public static AssertionError assertionerror = null;
          public  static Error error = null;
          
        //XML  
          public static final String GC_XML_SUITE = "suite";
          public static final String GC_XML_TEST = "test";
          public static final String GC_XML_LISTENERS = "listeners";
          public static final String GC_XML_LISTENER = "listener";
          public static final String GC_XML_CLASSES = "classes";
          public static final String GC_XML_CLASS = "class";
          public static final String GC_XML_METHODS = "methods";
          public static final String GC_XML_INCLUDE = "include";
          public static final String GC_XML_EXCLUDE = "exclude";
          public static final String GC_XML_ATTR_NAME = "name";
          public static final String GC_XML_ATTR_CLASSNAME = "class-name";     
          public static final String GC_XML_ATTR_VERBOSE = "verbose";
          public static final String GC_XML_ATTR_VAL_SUITE = "testsuite";
          public static final String GC_XML_ATTR_VAL_TEST = "testXML";
          public static String GC_MANUAL_TC_NAME ;
          public static final String GC_COL_MANUAL_TC = "ManualTestCaseName";
          
        //LOGGER
          public static final String GC_LOG_DEBUG = "debug";
          public static final String GC_LOG_INFO = "info";
          public static final String GC_LOG_WARN = "warn";
          public static final String GC_LOG_ERR = "error";
          public static final String GC_LOG_TRACE = "trace";
          public static final String GC_LOG_INIT_MSG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ STARTING TEST EXECUTION @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
          public static final String GC_LOG_INITTC_MSG =" ****************************** ";
       
       //EXCEPTION
          public static final String GC_EXPNTYPE_EXCEPTION = "exception";
          public static final String GC_EXPNTYPE_NULLPOINTER = "nullpointerexception";
          public static final String GC_EXPNTYPE_CLASSNOTFOUND = "classnotfoundexception";
          public static final String GC_EXPNTYPE_INTERRUPTED = "interruptedexception";
          public static final String GC_EXPNTYPE_NOSUCHMETHOD = "nosuchmethodexception";
          public static final String GC_EXPNTYPE_NOSUCHFIELD = "nosuchfieldexception";
          public static final String GC_EXPNTYPE_ILLEGALSTATE = "illegalstateexception";
          public static final String GC_EXPNTYPE_IOEXCEPTION = "illegalstateexception";
          
                         
        //AAG Managed Accounts
          public static final String GC_LOG_FILE_VALIDATION_MESSAGE="Successful-> "+"[1-9].*";
          //Log File Patterns
          public static String GC_PROSPECT_LOG_FILE_PATTERN="";
          public static String GC_MEMBER_LOG_FILE_PATTERN="";
          public static String GC_ENROLL_CANCEL_LOG_FILE_PATTERN="";
          public static String GC_TRANSACTION_LOG_FILE_PATTERN="";
          public static String GC_TRANSACTION_COINFIRMATION_LOG_FILE_PATTERN="";
          public static int GC_SELECT_ENVIRONMENT=0;
          public static String GC_SELECT_FOLDER_FOR_RUNNING_JOB=""; 
          public static final String GC_LOG_FILE_TYPE=".log";
          public static final String GC_FILE_TYPE=".xml";
  	   public static String GC_Database_Prefix="";

    	  public static final String GC_TRANSACTION_FUTURE_STATUS="FUTURE";
    	  public static final String GC_TRANSACTION_COMPLETE_STATUS="COMPLETE";
    	  public static final String GC_BATCH_RUN_DATE_FORMAT="yyyy-MM-dd";
    	  public static final String GC_RUN_DATE_TIMESTAMP="yyyyMMdd_HHmmss";
    	  public static final String GC_TRANS_CONF_SUCCESS_STATUS="SUCCESS";
    	  public static final String GC_TRANS_CONF_REQUESTED_STATUS="REQUESTED";
    	  public static final String GC_ENROLL_STATUS="Enroll";
    	  public static final String GC_CANCEL_STATUS="Cancel";
    	  public static final String GC_TRANS_CONF_BEFORE_JOB_STATUS="Before";
    	  public static final String GC_TRANS_CONF_AFTER_JOB_STATUS="After";
    	  public static String GC_TEST_REALPATH="";
    	  
    	  
        //SHELL
          public static final String GC_COMMAND_COMPLETE_STATUS="Complete";
          public static final int GC_SHELL_PORT_NUMBER=22;
          public static final String GC_SHELL_HOST_NAME="gp-das2";
          public static final String GC_SHELL_CHANNEL_NAME="shell";
          public static final String GC_SFTP_CHANNEL_NAME="sftp";
          public static String GC_REMOTE_OUTPUT_PATH="";
          public static String GC_REMOTE_INPUT_PATH="";    	  
    	  public static String GC_REMOTE_ARCHIVE_PATH="";
    	  /*Before Using these Remote Output, Input and Archive variables, 
    	   * call initializeRemoteDirectoryPath() method from SFTP Utils Call to initialize values
    	   */
      	  public static final String GC_REMOTE_OUTPUT_DIRECTORY="/work/batch/output/";
    	  public static final String GC_REMOTE_INPUT_DIRECTORY="/work/batch/input/";    	  
    	  public static final String GC_REMOTE_ARCHIVE_DIRECTORY="/work/batch/input/archive/";
    	  public static final String GC_LOCAL_TEMP_DIRECTORY ="C:\\temp";
//    	  public static final String GC_LOCAL_TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    	  public static final String GC_PROSPECT_FILE_NAMEPATTERN="ma_empower_dc_prosp_";
    	  public static final String GC_MEMBER_FILE_NAMEPATTERN="ma_empower_dc_memb_";
    	  public static final String GC_ENROLLCANCEL_FILE_NAMEPATTERN="ma_fei_enrll_";
    	  public static final String GC_TRANSACTION_INPUT_FILE_NAMEPATTERN="ma_fei_txnpr_"; 
    	  public static final String GC_TRANSACTION_CONFIRMATION_FILE_NAMEPATTERN="ma_empower_txnec_";
          
    	  public static final Map<String, String> databaseConnectionStrings;
    	  static
    	  {
  	    	databaseConnectionStrings = new Hashtable<String, String>();
  	    	//Dev databases
  	    	databaseConnectionStrings.put("D_INST", "jdbc:oracle:thin:@DINSTDB:1521/dinstmain.isis.gwl.com");
  	    	databaseConnectionStrings.put("D_PNP", "jdbc:oracle:thin:@dpnpdb:1521/dpnpmain.isis.gwl.com");
  	    	databaseConnectionStrings.put("D_ISIS", "jdbc:oracle:thin:@disisdb:1521/disisbatch.isis.gwl.com");
  	    	databaseConnectionStrings.put("D_CMNM", "jdbc:oracle:thin:@fsxd01dbad02:1521/dcmnmmain.isis.gwl.com");
  	    	databaseConnectionStrings.put("D_MISC", "jdbc:oracle:thin:@gp-pdb2:1521/D_MISC.ISIS.GWL.COM");
  	    	databaseConnectionStrings.put("D_AMA", "jdbc:oracle:thin:@Damadb:1521/damamain.isis.gwl.com");
  	    	databaseConnectionStrings.put("D_QAMA", "jdbc:oracle:thin:@Qamadb:1521/qamamain.isis.gwl.com");
  	    	
  	    	//to be deleted once the all projects comply
  	    	databaseConnectionStrings.put("D_QASI", "jdbc:oracle:thin:@dqasi:1521/dqasimain.isis.gwl.com");
  	    	databaseConnectionStrings.put("D_QASK", "jdbc:oracle:thin:@dqaskdb:1521/dqaskmain.isis.gwl.com");
  	    	databaseConnectionStrings.put("D_IN02", "jdbc:oracle:thin:@din02db:1521/din02java.isis.gwl.com");
  	    	databaseConnectionStrings.put("D_QAS2", "jdbc:oracle:thin:@dqas2:1521/dqas2main.isis.gwl.com");
  	    	
  	    	//new QA databases
  	    	databaseConnectionStrings.put("Q_CMNM", "jdbc:oracle:thin:@qcmnmdb:1521/QCMNMMAIN.ISIS.GWL.COM");
  	    	databaseConnectionStrings.put("Q_INST", "jdbc:oracle:thin:@qinstdb:1521/qinstmain.isis.gwl.com");
  	    	databaseConnectionStrings.put("Q_IN02", "jdbc:oracle:thin:@qin02db:1521/qin02main.isis.gwl.com");
  	    	databaseConnectionStrings.put("Q_ISIS", "jdbc:oracle:thin:@qisisdb:1521/QISISMAIN.ISIS.GWL.COM");
  	    	}
}
