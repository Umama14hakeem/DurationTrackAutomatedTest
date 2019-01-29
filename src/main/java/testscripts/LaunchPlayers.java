package testscripts;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import components.AnugularPlayerMethods;
import utility.Configuration.BaseClass;
import utility.Configuration.Config;

public class LaunchPlayers extends BaseClass {
	public AnugularPlayerMethods player = new AnugularPlayerMethods();

	@Test(dataProvider = "dpLaunchAngularPlayers", dataProviderClass = utility.Configuration.Dataprovider.class,
			description = "Scenario:1 - Test the functionality of launch Anugular Player")
	public void LaunchPlayerTest(String playerType, String browser, String PlayerID) throws Exception {
		try {
			logger.assignCategory("Smoke");
			player.launchPlayer(browser, Config.URL);
			waitForLoad();
			logger.log(Status.PASS, MarkupHelper.createLabel("Player: "+playerType+" Open", ExtentColor.GREY));
			player.LaunchAngularPlayerTest(PlayerID);
			logger.log(Status.PASS, MarkupHelper.createLabel("Anugular Player Launch Successfully.", ExtentColor.GREEN));
			waitForLoad();
		} catch (Exception ex) {
			logger.log(Status.ERROR, "Error in Lauching Anugular Player." + ex.getMessage());
		}
	}

}
