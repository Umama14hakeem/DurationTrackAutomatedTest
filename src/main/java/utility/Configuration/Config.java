/*
 * defing values for all the constants
 */

package utility.Configuration;

public class Config {

	public static final String username = "testengi123@gmail.com";
	public static final String password = "demos123";
	
	public static final String User_Dir = "user.dir";
	public static final String Browser = "chrome";
	public static final String URL = "http://www.demosondemand.com/dod_staging/";
	
	public static final String Browser_DriverFirefox = "webdriver.gecko.driver";
	public static final String Browser_DriverChrome = "webdriver.chrome.driver";
	public static final String Browser_DriverIE = "webdriver.ie.driver";
	
	public static final String Browser_PathChrome = "/src/main/resources/drivers/chromedriver.exe";
	public static final String Browser_PathFirefox  = "/src/main/resources/drivers/geckodriver.exe";
	
	public static final String Browser_PathIE = "\\src\\main\\resources\\drivers\\IEDriverServer.exe";
	public static final String processKill = "taskkill /F /IM ChromeDriver.exe";
	public static final String processKillBrowser = "taskkill /F /IM Chrome.exe";
		
	public static final long Implicit_Wait = 10;
	public static final long wait = 10;
	
	public static final String TimeFormat = "dd-MM-yy HH-mm-ss";

	
	public static final String ExtentReport = "/src/test/resources/report/DurationTrackAutomatedTest-";
	public static final String ExtentReportXml = "/extent-config.xml";
	
	public static final String Path_ScreenShot = "/src/test/resources/screenshots/";
	public static final String ScreenShotExtension = ".png";
	
	public static final String Path_Video = "/src/test/resources/videos/";
	public static final String Path_TestVideo = "/src/test/resources/videos/";

	public static final String File_Path_LaunchAngularPlayerTest = "/src/main/java/testData/LaunchAngularPlayerTest.xls";
	public static final String File_Sheet_LaunchAngularPlayerTest = "LaunchAngularPlayerTest";

	
}
