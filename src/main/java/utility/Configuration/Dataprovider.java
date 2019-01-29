/*
 * data provide methods for providing data from excel for both admin, home pages
 */

package utility.Configuration;

import java.util.ArrayList;

import org.testng.annotations.DataProvider;

import com.aventstack.extentreports.Status;

public class Dataprovider extends BaseClass {

	public static int i;

	@DataProvider(name = "dpLaunchAngularPlayers")
	public static Object[][] getLaunchAngularPlayerData() throws Exception {
		Object[][] data = null;
		try {
		ArrayList<Object> excelData = Excel.fetchExcelLaunchAngularPlayers();
		data = new Object[excelData.size()][];
		for (i = 0; i < excelData.size(); i++)
			data[i] = (Object[]) excelData.get(i);
		}catch(Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}
		return data;
	}

}
