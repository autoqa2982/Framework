package core.framework;

import java.io.File;
import java.util.HashMap;

import lib.Log;
import lib.Log.Level;
import lib.Reporter;
import lib.Reporter.Status;
import lib.Stock;
import lib.Web;

import org.apache.commons.io.FileUtils;
import org.testng.IConfigurationListener2;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import core.framework.ThrowException.TYPE;

public class TestListener implements ITestListener, IConfigurationListener2,
		ISuiteListener, IInvokedMethodListener {

	int currentTCInvocationCount = 0;
	private static boolean finalTestStatus = true;

	private boolean isFinalTestStatus() {
		return finalTestStatus;
	}

	public static void setFinalTestStatus(boolean testStatus) {
		finalTestStatus = testStatus;
	}

	public void onStart(ISuite suite) {
		try {
			Stock.getParam(Globals.GC_TESTCONFIGLOC
					+ Globals.GC_CONFIGFILEANDSHEETNAME + ".properties");
			if (!Globals.GC_EXECUTION_ENVIRONMENT.isEmpty()) {
				Stock.setConfigParam(Globals.GC_COLNAME_TEST_ENV,
						Globals.GC_EXECUTION_ENVIRONMENT, true);
			}
			if (!Globals.GC_EXECUTION_BROWSER.isEmpty()) {
				Stock.setConfigParam(Globals.GC_COLNAME_BROWSER,
						Globals.GC_EXECUTION_BROWSER, true);
			}
			if (new File(Globals.GC_TEST_REPORT_DIR).exists()) {

				FileUtils.deleteDirectory(new File(Globals.GC_TEST_REPORT_DIR));

				System.out.println("Deleted report folder from directory : "

				+ new File(Globals.GC_TEST_REPORT_DIR)

				.getAbsolutePath());

				Log.Report(Level.INFO,

				"Test Report folder removed on exist on suite level");

			}

			Log.Report(Level.INFO,
					"Test Configuration initialized successfully");
		} catch (Exception e) {
			ThrowException.Report(TYPE.EXCEPTION, e.getMessage());
		}
	}

	public void onStart(ITestContext test) {
		Globals.GC_MANUAL_TC_NAME = test.getName();
		try {
			if (!Web.webdriver.getWindowHandle().isEmpty()) {
				Log.Report(Level.INFO,
						"Web Driver instance found to be active for the Test Case :"
								+ test.getName());
			}
		} catch (Exception t) {
			try {
				Log.Report(Level.INFO,
						"Web Driver instance found to be inactive for the Test Case :"
								+ test.getName() + " ,hence re-initiating");
				Web.webdriver = Web.getWebDriver(Stock
						.getConfigParam("BROWSER"));
				Log.Report(Level.INFO,
						"Web Driver instance re-initiated successfully the Test Case :"
								+ test.getName());
			} catch (Exception e) {
				ThrowException
						.Report(TYPE.EXCEPTION,
								"Failed to re-initialize Web Driver :"
										+ e.getMessage());
			}
		}
	}

	public void onTestStart(ITestResult result) {

	}

	public void onTestSuccess(ITestResult result) {

	}

	public void onTestFailure(ITestResult result) {

	}

	public void onTestSkipped(ITestResult result) {

	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	public void onFinish(ITestContext context) {

	}

	public void onConfigurationSuccess(ITestResult result) {

	}

	public void onConfigurationFailure(ITestResult result) {

	}

	public void onConfigurationSkip(ITestResult result) {

	}

	public void beforeConfiguration(ITestResult result) {

	}

	public void onFinish(ISuite suite) {
		try {
			if (Web.webdriver.getWindowHandles().size() >= 0)
				Web.webdriver.close();
				Web.webdriver.quit();
		} catch (Exception e) {
			ThrowException.Report(TYPE.EXCEPTION, "Failed to quit Web Driver :"
					+ e.getMessage());
		}

	}

	// This belongs to IInvokedMethodListener and will execute before every
	// method including @Before @After @Test
	@SuppressWarnings("unchecked")
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		setFinalTestStatus(true);
		// System.out.println("before Invocation"+isFinalTestStatus());
		if (method.getTestMethod().isTest()) {
			HashMap<String, String> globalTestData = (HashMap<String, String>) testResult
					.getParameters()[1];
			Stock.globalTestdata = globalTestData;
			currentTCInvocationCount = testResult.getMethod()
					.getCurrentInvocationCount();
			Web.setLastIteration((Stock.getIterations() == (currentTCInvocationCount + 1)) ? true
					: false);
			
			try {
				/*Reporter.initializeReportForTC((currentTCInvocationCount + 1), Globals.GC_MANUAL_TC_NAME + "_"
						+ Stock.getConfigParam("BROWSER"));
				lib.Reporter.logEvent(Status.INFO,"Test Data used for this Test Case:",Stock.getTestDataAsString(),false);*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		try {
			if (method.getTestMethod().isTest()) {
				//Reporter.finalizeTCReport();
				if (!isFinalTestStatus()) {
					testResult.setStatus(ITestResult.FAILURE);
				}
				// System.out.println("after Invocation"+isFinalTestStatus());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}