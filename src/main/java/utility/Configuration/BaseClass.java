package utility.Configuration;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

class ExtentManager {

	private static ExtentReports extent;
	static String dateName = new SimpleDateFormat(Config.TimeFormat).format(Calendar.getInstance().getTime());

	public static ExtentReports getInstance() {
		if (extent == null)
			createInstance(System.getProperty(Config.User_Dir) + Config.ExtentReport + dateName + ".html");

		return extent;
	}

	public static ExtentReports createInstance(String fileName) {
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(fileName);
		extent = new ExtentReports();
		extent.setSystemInfo("Host Name", "Angular Player");
		extent.setSystemInfo("Environment", "QA");
		extent.setSystemInfo("User Name", "Umama Hakeem");
		extent.attachReporter(htmlReporter);

		return extent;
	}
}

public class BaseClass {

	public static String winHandleBefore = null;
	public List<String> desc = new ArrayList<String>();
	ArrayList<String> tabs;
	public int r;
	public int time;

	public static ExtentReports extent;
	public static ExtentTest logger;
	public DateFormat dateFormat;
	public static Date date;
	public static WebDriver driver;
	public WebDriver wait;
	public ScreenRecorder screenRecorder;
	public String dateName = new SimpleDateFormat(Config.TimeFormat).format(Calendar.getInstance().getTime());
	public Method method;

	@BeforeSuite
	public void beforesuite() {
		try {
			extent = ExtentManager
					.createInstance(System.getProperty(Config.User_Dir) + Config.ExtentReport + dateName + ".html");
			ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(
					System.getProperty(Config.User_Dir) + Config.ExtentReport + dateName + ".html");
			htmlReporter.loadXMLConfig(new File(System.getProperty(Config.User_Dir) + Config.ExtentReportXml));
			extent.attachReporter(htmlReporter);
			// To start video recording.
			 startRecording();
		} catch (Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}
	}

	@BeforeMethod
	public synchronized void beforeMethod(Method method) {
		try {
			logger = extent.createTest(method.getName());
			logger.assignAuthor("Umama Hakeem");
		} catch (Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}

	}

	@AfterMethod
	public synchronized void getResult(ITestResult result) throws Exception {
		try {
			if (result.getStatus() == ITestResult.FAILURE) {
				logger.log(Status.FAIL,
						MarkupHelper.createLabel("Failed Test Cases Name is: " + result.getName(), ExtentColor.RED));
				String screenshotPath = getScreenshot(driver, result.getName());
				logger.fail("Failed Test Screenshot: ",
						MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
				logger.log(Status.FAIL,
						MarkupHelper.createLabel("Test Case Failed due to: " + result.getThrowable(), ExtentColor.RED));
			} else if (result.getStatus() == ITestResult.SKIP) {
				logger.log(Status.SKIP,
						MarkupHelper.createLabel("Test Case Skipped is " + result.getName(), ExtentColor.ORANGE));
			}
			closeBrowser();
		} catch (Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}
	}

	@AfterSuite
	public void aftersuite() {
		try {
			// To stop video recording.
			  stopRecording();
			closeProcess();
			extent.flush();
			// closeProcess();
		} catch (Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}
	}

	// Mehtod for browser to open Url

	@SuppressWarnings("deprecation")
	public void openBrowser(String browser) throws Exception {

		try {

			if (browser.equals("firefox")) {
				System.setProperty(Config.Browser_DriverFirefox,
						System.getProperty(Config.User_Dir) + Config.Browser_PathFirefox);

				DesiredCapabilities dsFirefox = DesiredCapabilities.firefox();
				dsFirefox.setCapability("marionette", true);
				FirefoxProfile ffProfile = new FirefoxProfile();
				ffProfile.setPreference("network.cookie.cookieBehavior", 2);
				ffProfile.setPreference("browser.tabs.remote.autostart.2", false);
				ffProfile.setPreference("browser.cache.disk.enable", false);
				ffProfile.setPreference("browser.cache.memory.enable", false);
				ffProfile.setPreference("browser.cache.offline.enable", false);
				ffProfile.setPreference("network.http.use-cache", false);
				ffProfile.setAcceptUntrustedCertificates(true);
				ffProfile.setAssumeUntrustedCertificateIssuer(true);
				dsFirefox.setCapability(FirefoxDriver.PROFILE, ffProfile);

				dsFirefox.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				dsFirefox.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				dsFirefox.setJavascriptEnabled(true);
				driver = new FirefoxDriver(dsFirefox);
				// driver = new FirefoxDriver();
				logger.log(Status.INFO, "Input Browser Open: " + browser);

			} else if (browser.equals("chrome")) {
				System.setProperty(Config.Browser_DriverChrome,
						System.getProperty(Config.User_Dir) + Config.Browser_PathChrome);
				// driver = new ChromeDriver();
				DesiredCapabilities dsChrome = DesiredCapabilities.chrome();

				ChromeOptions options = new ChromeOptions();
				Map<String, Integer> prefs = new HashMap<String, Integer>();
				prefs.put("profile.default_content_settings.cookies", 2);
				options.setExperimentalOption("prefs", prefs);
				options.addArguments("--no-sandbox", "--ignore-certificate-errors", "--homepage=about:blank",
						"--no-first-run");
				options.addArguments("test-type");
				options.addArguments("start-maximized");
				options.addArguments("enable-automation");
				options.addArguments("ignore-certificate-errors");
				options.addArguments("--js-flags=--expose-gc");
				options.addArguments("--enable-precise-memory-info");
				options.addArguments("--disable-popup-blocking");
				options.addArguments("--disable-default-apps");
				options.addArguments("test-type=browser");
				options.addArguments("disable-infobars");
				options.addArguments("disable-extensions");
				options.addArguments("-disable-cache");
				dsChrome.setCapability(ChromeOptions.CAPABILITY, options);
				dsChrome.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				dsChrome.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, false);
				dsChrome.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				driver = new ChromeDriver(dsChrome);
				logger.log(Status.INFO, "Input Browser Open: " + browser);

			} else if (browser.equals("IE")) {
				System.setProperty(Config.Browser_DriverIE,
						System.getProperty(Config.User_Dir) + Config.Browser_PathIE);

				DesiredCapabilities dsIE = DesiredCapabilities.internetExplorer();
				dsIE.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				dsIE.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, true);
				dsIE.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				dsIE.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				driver = new InternetExplorerDriver(dsIE);
				logger.log(Status.INFO, "Input Browser Open: " + browser);
				if (driver.getPageSource().contains("certificate")) {
					driver.navigate().to("javascript:document.getElementById('overridelink').click()");
				}
				driver.manage().deleteAllCookies();
				driver.manage().window().maximize();
			} else {
				logger.log(Status.FATAL, MarkupHelper.createLabel("Browser Not Found", ExtentColor.GREY));
			}

		} catch (Exception e) {
			logger.log(Status.FATAL, MarkupHelper.createLabel(
					"Class Utils | Method OpenBrowser | Exception desc : " + e.getMessage(), ExtentColor.GREY));
		}
	}

	public void openURL(String url) throws Exception {
		try {
			driver.get(url);
			Thread.sleep(randomTime());
			logger.log(Status.INFO, MarkupHelper.createLabel("Input Url: " + url, ExtentColor.ORANGE));
			driver.manage().timeouts().implicitlyWait(Config.Implicit_Wait, TimeUnit.SECONDS);
		} catch (RuntimeException localRuntimeException) {
			localRuntimeException.getMessage();
		}

	}

	public void closeBrowser() throws Exception {
		try {
			driver.quit();
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in closing Browser:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public void closeProcess() throws Exception {
		try {
			Runtime.getRuntime().exec(Config.processKill);
			Runtime.getRuntime().exec(Config.processKillBrowser);
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in closing Process:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public int randomTime() {
		try {
			Random r = new Random();
			int Low = 5;
			int High = 10;
			time = r.nextInt(High - Low) + Low;
		} catch (Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}
		return time;
	}

	public static void waitForLoad() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			logger.log(Status.ERROR, e.getMessage());
		}
	}

	public void waitForElement() throws InterruptedException {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			logger.log(Status.ERROR, e.getMessage());
		}
	}
	
	public static void waitForLongLoad() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.log(Status.ERROR, e.getMessage());
		}
	}

	public void startRecording() throws Exception {
		try {
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration();
			this.screenRecorder = new ScreenRecorder(gc, null,
					new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
					new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
							CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
							Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
					new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
					null, new File(System.getProperty("user.dir") + Config.Path_Video));
			this.screenRecorder.start();
		} catch (Exception e) {
			logger.log(Status.ERROR, "Class Utils | Method startRecording | Exception occured while capturing Videos : "
					+ e.getMessage());
			throw new Exception();
		}
	}

	public void stopRecording() throws Exception {
		try {
			this.screenRecorder.stop();
		} catch (Exception e) {
			logger.log(Status.ERROR, "Class Utils | Method stopRecording | Exception occured while capturing Videos : "
					+ "+ e.getMessage()");
			throw new Exception();
		}
	}

	// Screenshot method for Extent Reporting

	public String getScreenshot(WebDriver webdriver, String screenshotName) throws Exception {
		try {
			TakesScreenshot ts = (TakesScreenshot) driver;
			File source = ts.getScreenshotAs(OutputType.FILE);
			// after execution, you could see a folder "FailedTestsScreenshots" under src
			// folder
			String destination = System.getProperty(Config.User_Dir) + Config.Path_ScreenShot + screenshotName
					+ dateName + Config.ScreenShotExtension;
			File finalDestination = new File(destination);
			FileUtils.copyFile(source, finalDestination);
			return destination;
		} catch (Exception e) {
			logger.log(Status.ERROR,
					"Class Utils | Method takeScreenshot | Exception occured while capturing ScreenShot : "
							+ e.getMessage());
			throw new Exception();
		}

	}

	public void click(By locator) throws Exception {
		try {
			driver.findElement(locator).click();
		} catch (Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}
	}

	public void type(WebElement element, By locator) throws Exception {
		try {
			element = driver.findElement(locator);
			element.clear();
			element.click();
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR,
					"Error in entering the text in element:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public void type(By locator, String data) throws Exception {
		try {
			driver.findElement(locator).clear();
			driver.findElement(locator).sendKeys(data);
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR,
					"Error in entering the text in element:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public void type(WebElement element, By locator, String data) throws Exception {
		try {
			element = driver.findElement(locator);
			driver.findElement(locator).sendKeys(data);
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR,
					"Error in entering the text in element:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public void select(By locator, String data) throws Exception {
		try {
			Select s = new Select(driver.findElement(locator));
			s.selectByVisibleText(data);
		} catch (RuntimeException localRuntimeException) {
			System.out.println("Error in selecting value from dropdown:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public void select(By locator, int no) throws Exception {
		try {
			Select dropdown = new Select(driver.findElement(locator));
			dropdown.selectByIndex(no);
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR,
					"Error in selecting value from dropdown:" + localRuntimeException.getMessage() + "Fail");
		}
	}

	public void switchTowindow() throws Exception {
		try {
			String MainWindow = driver.getWindowHandle();
			// To handle all new opened window.
			Set<String> s1 = driver.getWindowHandles();
			Iterator<String> i1 = s1.iterator();
			while (i1.hasNext()) {
				String ChildWindow = i1.next();
				if (!MainWindow.equalsIgnoreCase(ChildWindow)) {
					// Switching to Child window
					driver.switchTo().window(ChildWindow);
					// driver.manage().window().maximize();
					waitForLoad();
					// Closing the Child Window.
					driver.close();
				}
			}
			// Switching to Parent window i.e Main Window.
			// driver.switchTo().window(MainWindow);
		} catch (RuntimeException localRuntimeException ) {
			logger.log(Status.ERROR, "Error in Switching the window:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}

	}

	public void switchNewWindow() {
		try {
			winHandleBefore = driver.getWindowHandle();
			// System.out.println(winHandleBefore);
			String childHandl = (String) driver.getWindowHandles().toArray()[1];
			driver.switchTo().window(childHandl);
			// driver.manage().window().maximize();
			// logger.log(Status.INFO, "Switched backed to child tab" + "Pass");
		} catch (Exception e) {
			logger.log(Status.ERROR, "Error in Switching to new tab" + "fail");
		}

	}

	public void switchbackWindow() {
		try {
			driver.switchTo().window(winHandleBefore);
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in switching to original window");
			localRuntimeException.getMessage();
		}

	}

	public void switchwindow(int index) throws Exception {
		try {
			driver.switchTo().window(winHandleBefore);
			driver.close();
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR,
					"Error in Switching the Original window:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}

	}
	
	public void switchTab() throws Exception {
		try {
			tabs = new ArrayList<String> (driver.getWindowHandles());
		    driver.switchTo().window(tabs.get(1));
		    driver.close();
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR,
					"Error in Switching the Original window:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}

	}
	
	public void switchbackToTab() {
		try {
			driver.switchTo().window(tabs.get(0));
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in switching to original window");
			localRuntimeException.getMessage();
		}

	}
	
	
	
	public void switchframeByIndex(int index) throws Exception {
		try {
			driver.switchTo().frame(index);
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in Switching the Frame:" + localRuntimeException.getMessage() + "Fail");
		}

	}

	public void switchframe(WebElement elem) throws Exception {
		try {
			driver.switchTo().frame(elem);
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in Switching back to "
					+ "the Frame:" + localRuntimeException.getMessage() + "Fail");
		}

	}

	public void switchToDefaultFrame() throws Exception {
		try {
			driver.switchTo().defaultContent();
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in Switching the Frame:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}

	}

	public boolean js_type(By by, String Text, String LocatorName) throws Throwable {
		boolean flag = true;
		try {

			WebElement location = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].value='" + Text + "'", location);
			return true;

		} catch (Exception e) {
			flag = false;
			return false;
		} finally {
			if (flag) {
				logger.log(Status.INFO, "Type Data into " + LocatorName + "Able to Type Data into : " + LocatorName);
			} else {
				logger.log(Status.ERROR,
						"Type Data into " + LocatorName + "Not able to Type Data into : " + LocatorName);
			}
		}
	}

	public boolean JSClick(By locator, String locatorName) throws Exception {
		boolean flag = false;
		try {
			WebElement element = driver.findElement(locator);
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;

	}

	public void highlight(By locator) throws Exception {
		try {

			WebElement elem = driver.findElement(locator);
			JavascriptExecutor je = (JavascriptExecutor) driver;
			je.executeScript("arguments[0].style.border='3px solid blue'", elem);

		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR,
					"Error in Highlighting the element :" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public void highlight(WebElement elem) throws Exception {
		try {

			JavascriptExecutor je = (JavascriptExecutor) driver;
			je.executeScript("arguments[0].style.border='3px solid blue'", elem);

		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR,
					"Error in Highlighting the element :" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public void waitForElement(By locator, int timer) throws Exception {
		try {
			for (int i = 0; i < timer; i++) {
				try {
					driver.findElement(locator).isDisplayed();
					logger.log(Status.INFO, "Element is available :" + locator);
					break;
				} catch (RuntimeException localRuntimeException) {
					waitForElement();
					logger.log(Status.ERROR, "Waiting for........" + locator);
				}
			}
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in performing Wait:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public boolean IsElementPresent(By locator) {
		if (driver.findElement(locator).isDisplayed()) {
			return true;
		} else {
			return false;
		}
	}

	public void isElementPresent(By locator) {
		Assert.assertTrue(driver.findElement(locator).isDisplayed());
	}

	public void checkPageTitle(String title) {
		Assert.assertEquals(driver.getTitle(), title);
		// SoftAssert softAssertion= new SoftAssert();
		// softAssertion.assertEquals(driver.getTitle(), title);
	}

	public int totalitemsdropdownlist(WebElement elem) {
		List<WebElement> dropdown_values = null;
		try {
			Select dropdownfield = new Select(elem);
			dropdown_values = dropdownfield.getOptions();

		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in finding total no. of elements in dropdown: "
					+ localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
		return dropdown_values.size();
	}

	public static void verifyElementIsEnabled(WebElement elem, boolean paramBoolean) {
		try {
			boolean bool = elem.isEnabled();
			if (bool == paramBoolean)
				logger.log(Status.INFO, "Element is present in expected state" + elem + "Pass");
			else
				logger.log(Status.ERROR, "Element is not present in expected state" + elem + "Fail");
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Element not found:" + elem + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public boolean isAlertPresent() {
		boolean foundAlert = false;
		try {
			WebDriverWait wait = new WebDriverWait(driver, 60L);
			wait.until(ExpectedConditions.alertIsPresent());
			foundAlert = true;
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in finding Alert Is Present:Fail");
			localRuntimeException.getMessage();
		}
		return foundAlert;
	}

	public void handleConfirmation(String paramString) {
		Alert localAlert = driver.switchTo().alert();
		if (localAlert != null) {
			if (paramString.trim().equalsIgnoreCase("Y")) {
				logger.log(Status.ERROR, "Alert accepted!!!");
				localAlert.accept();
			} else if (paramString.trim().equalsIgnoreCase("N")) {
				logger.log(Status.ERROR, "Alert Rejected!!!");
				localAlert.dismiss();
			}
		} else {
			logger.log(Status.ERROR, "Error in finding Alert:Fail");
		}
	}

	public String getAlertMessageText() {
		String str1 = null;
		try {
			str1 = driver.switchTo().alert().getText();
			return str1;
		} catch (Exception e) {
		}
		return str1;
	}

	public void sleep(float paramFloat) {
		try {
			long l1 = (long) (paramFloat * 1000.0F);
			long l2 = System.currentTimeMillis();
			while (l2 + l1 >= System.currentTimeMillis())
				;
		} catch (Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}

	}

	public int getListSize(String tableid) {
		int iRowCount = 0;
		try {
			List<WebElement> iSize = driver.findElements(By.xpath("//table[@id='" + tableid + "']/tbody/tr"));
			iRowCount = iSize.size();
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in fetching no. of rows:" + tableid + "Fail");
			localRuntimeException.getMessage();
		}
		return iRowCount;
	}

	public void pressEnterKey() {
		try {
			Robot r = new Robot();
			r.keyPress(KeyEvent.VK_ENTER);
			r.keyRelease(KeyEvent.VK_ENTER);
		} catch (Exception e) {
			logger.log(Status.ERROR, e);
		}
	}

	public void VerifyText(By locator, String paramString2) {
		WebElement elem = null;
		try {
			elem = driver.findElement(locator);
			String text = elem.getText();
			if (text.equalsIgnoreCase(paramString2)) {
				logger.log(Status.INFO, "Text was found :" + paramString2);
			} else {
				logger.log(Status.FAIL, "Text was not found :" + paramString2);
			}
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Element was not found :" + elem);
			localRuntimeException.getMessage();
		}
	}

	public void VerifyTextDropdown(WebElement elem, String paramString2) {
		try {
			String selectedOption = new Select(elem).getFirstSelectedOption().getText();
			if (selectedOption.trim().equalsIgnoreCase(paramString2)) {
				logger.log(Status.INFO, "Text was found :" + paramString2 + "Pass");
			} else {
				logger.log(Status.INFO, "Text was not found :" + paramString2 + "Fail");
			}
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Element was not found :" + elem + "Fail");
			localRuntimeException.getMessage();
		}
	}

	public String getToolTipText(WebElement elem, String paramString1) {
		String tooltip = null;
		try {
			if (elem != null) {
				tooltip = elem.getAttribute(paramString1);
			}
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in Getting tool tip text:" + localRuntimeException.getMessage() + "Fail");
			localRuntimeException.getMessage();
		}
		return tooltip;
	}

	public void verifyListItems(WebElement elem) {
		try {
			Select listBox = new Select(elem);
			List<WebElement> allItems = listBox.getOptions();
			for (WebElement item : allItems) {
				logger.log(Status.INFO, "Item is available in list:" + item);
			}
		} catch (Exception e) {
			logger.log(Status.INFO, "Issue While Selecting Value in Drop Down Object :" + elem);
		}
	}

	public By getLocators(String paramString1, String paramString2) {
		if (paramString1.trim().equalsIgnoreCase("xpath"))
			return By.xpath(paramString2);
		if (paramString1.trim().equalsIgnoreCase("cssselector"))
			return By.cssSelector(paramString2);
		if (paramString1.trim().equalsIgnoreCase("tagname"))
			return By.tagName(paramString2);
		if (paramString1.trim().equalsIgnoreCase("id"))
			return By.id(paramString2);
		if (paramString1.trim().equalsIgnoreCase("name"))
			return By.name(paramString2);
		if (paramString1.trim().equalsIgnoreCase("linktext"))
			return By.linkText(paramString2);
		return null;
	}

	public String defaultdropdownselecteditem(WebElement elem) {

		Select dropdownfield = new Select(elem);
		String text = dropdownfield.getFirstSelectedOption().getText();
		logger.log(Status.INFO, text.trim());
		return dropdownfield.getFirstSelectedOption().getText().trim();
	}

	public String alldropdownlistvalues(WebElement elem) {
		Select dropdownfield = new Select(elem);
		List<WebElement> dropdownfield_values = dropdownfield.getOptions();

		String allvalues = "";
		for (int i = 0; i < dropdownfield_values.size(); i++) {
			String currentvalue = dropdownfield_values.get(i).getText();
			String concatvalue = allvalues + currentvalue + ",";
			allvalues = concatvalue;
		}

		return allvalues.substring(0, allvalues.length() - 1);
	}

	public String getdate(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Date d = new Date();
		String date = df.format(d);
		logger.log(Status.INFO, date);
		return date;
	}

	public String getattributevalue(WebElement elem, String requiredattribute) throws Exception {
		String attribute = null;
		try {
			attribute = elem.getAttribute(requiredattribute);
		} catch (RuntimeException localRuntimeException) {
		}
		return attribute;
	}

	public void alertaction(String action) {

		try {
			if (action.equals("ok")) {
				driver.switchTo().alert().accept();
			} else if (action.equals("cancel")) {

				driver.switchTo().alert().dismiss();
			}
		} catch (Exception e) {
			logger.log(Status.ERROR, "Error in performing action on Alert box:" + action + "Fail");
		}

	}

	public String getText(By locator) {
		String text = driver.findElement(locator).getText();
		// logger.log(Status.INFO, "The text is :" + text);
		return text;
	}

	public int totallinnks(WebElement elem) {
		return elem.findElements(By.tagName("a")).size();
	}

	public void capturesnapshot(String destinationPath) throws IOException {
		try {
			File f = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(f, new File(destinationPath));
		} catch (Exception e) {
			logger.log(Status.ERROR, "Error in Capturing Screenshot:Fail");
		}

	}

	public void dragAndDrop(By question, By target) {
		WebElement e1 = driver.findElement(question);
		WebElement e2 = driver.findElement(target);
		Actions a = new Actions(driver);
		a.dragAndDrop(e1, e2).build().perform();
	}

	public boolean verifyElementExist(WebElement elem) {
		boolean blnStatus = false;
		WebDriverWait localWebDriverWait = new WebDriverWait(driver, 60L);
		try {
			localWebDriverWait.until(ExpectedConditions.presenceOfElementLocated((By) elem));
			logger.log(Status.INFO, "Element is available:" + elem + "Pass");
			blnStatus = true;

		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in finding Element:" + localRuntimeException.getMessage() + "Pass");
		}
		return blnStatus;
	}
	
	public void MouseMoveHover(By locator) {
		WebElement elem = null;
		try {
			elem = driver.findElement(locator);
			Actions action = new Actions(driver);
			action.moveToElement(elem).build().perform();
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in Hover on element" + localRuntimeException.getMessage() + "Pass");
		}
	}

	public void Mousehover(WebElement elem) {
		try {
			Actions action = new Actions(driver);
			action.moveToElement(elem).build().perform();
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR, "Error in Hover on element" + localRuntimeException.getMessage() + "Pass");
		}
	}

	public void selectListItem(WebElement elem, String paramString) {
		int i = 0;
		try {
			List<WebElement> localList = driver.findElements(By.tagName("option"));
			Iterator<WebElement> localIterator = localList.iterator();
			while (localIterator.hasNext()) {
				WebElement localWebElement2 = (WebElement) localIterator.next();
				if (paramString.trim().equalsIgnoreCase(localWebElement2.getText().trim())) {
					i = 1;
					localWebElement2.click();
					break;
				}
			}
			System.out.println("Selected option:" + paramString + "Successfully" + "Pass");
			if (i == 0) {
				logger.log(Status.INFO, "Selected option:" + paramString + "is not present" + "Fail");
			}
		} catch (RuntimeException localRuntimeException) {
			logger.log(Status.ERROR,
					"Issue while Selected value:" + localRuntimeException.getMessage() + "is not present" + "Fail");
		}
	}

	public void switchToBrowser(String paramString) {
		try {
			winHandleBefore = driver.getWindowHandle();
			String str1 = paramString;
			int i = 0;
			Iterator<String> localIterator = driver.getWindowHandles().iterator();
			while (localIterator.hasNext()) {
				String str2 = localIterator.next();
				if (driver.switchTo().window(str2).getTitle().equalsIgnoreCase(str1.trim())) {
					i = 1;
					driver.switchTo().window(str2);
				} else {
					driver.switchTo().window(winHandleBefore);
				}
			}
			if (i == 0)
				logger.log(Status.INFO, "The Browser Window with title : " + str1 + " is not found");
		} catch (Exception e) {
			logger.log(Status.ERROR, "Error in switching to browser" + e.getMessage());
		}
	}
	
}
