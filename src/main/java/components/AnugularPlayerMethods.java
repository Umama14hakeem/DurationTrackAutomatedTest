package components;

import org.openqa.selenium.By;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import objectRepository.AnugularPlayerLocators;
import utility.Configuration.BaseClass;

public class AnugularPlayerMethods extends BaseClass {

	public AnugularPlayerLocators playerLocator = new AnugularPlayerLocators();

	public void launchPlayer(String browser, String url) throws Exception {
		try {
			openBrowser(browser);
			openURL(url);
		} catch (Exception e) {
			logger.log(Status.FATAL, "Unable to open the URL");
		}
	}
	
	
	public void enterPlayerId(String playerId) {
		try {
			type(playerLocator.playerId, playerId);
			waitForLoad();
		} catch (Exception e) {
			logger.log(Status.FAIL, e.getMessage());
	    }
    }
	
	public void clickPlayerDemoButton() {
		try {
			click(playerLocator.playerDemoButton);
			waitForLoad();
		} catch (Exception e) {
			logger.log(Status.FAIL, e.getMessage());
	    }
    }
	
	public void moveMouse() {
		try {
			MouseMoveHover(playerLocator.movemouse);
			waitForElement();
		} catch (Exception e) {
			logger.log(Status.FAIL, e.getMessage());
	    }
    }
	
	public void LaunchAngularPlayerTest(String playerId) {
		try {
			enterPlayerId(playerId);
			logger.log(Status.INFO, ("Enter Player ID: " +playerId));
			clickPlayerDemoButton();
			logger.log(Status.INFO, "Click Player Demo Button");
			waitForLoad();
			
			String parentHandle = driver.getWindowHandle(); 
			for (String winHandle : driver.getWindowHandles()) {
			    driver.switchTo().window(winHandle); 
			}
			
			waitForLoad();
			String startTime = getText(playerLocator.videoStartTime);
			logger.log(Status.INFO, ("Video Start Duration: " +startTime));
			waitForElement();
			
			String totalVideoDuration = getText(playerLocator.videoDuration);
			logger.log(Status.INFO, ("Video Play Duration: " +totalVideoDuration));
			waitForLoad();
			
			String endTime = getText(playerLocator.videoStartTime);
			logger.log(Status.INFO, ("Video End Duration: " +endTime));
			waitForElement();
			
			driver.close();
			driver.switchTo().window(parentHandle);
		} catch (Exception e) {
			logger.log(Status.FAIL, e.getMessage());
	    }
    }
	
}
