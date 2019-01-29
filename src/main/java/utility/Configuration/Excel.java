/*
 * Excel method for reading, writing and fetching data from from excel 
 */

package utility.Configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.aventstack.extentreports.Status;
import utility.Configuration.Config;

public class Excel extends BaseClass {

	public static Workbook wb;
	public static Sheet ws;
	public static Cell cell = null;
	public static Row Row;
	public static int i;
	public static Object[] dataLine;
	public static ArrayList<Object> dataList;

	public static void setExcelFile(String fileName, String SheetName) throws Exception {
		try {
			FileInputStream ExcelFile = new FileInputStream(fileName);
			String fileExtensionName = fileName.substring(fileName.indexOf("."));
			// Check condition if the file is xlsx file
			if (fileExtensionName.equals(".xlsx")) {
				// If it is xlsx file then create object of XSSFWorkbook class
				wb = new XSSFWorkbook(ExcelFile);

			}
			// Check condition if the file is xls file
			else if (fileExtensionName.equals(".xls")) {
				// If it is xls file then create object of XSSFWorkbook class
				wb = new HSSFWorkbook(ExcelFile);
			}
			ws = wb.getSheet(SheetName);
			// logger.log(Status.INFO,"Excel sheet opened");
		} catch (Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}
	}

	public static String getCell(int rowIndex, int columnIndex) {
		cell = null;
		try {
			cell = ws.getRow(rowIndex).getCell(columnIndex);

		} catch (Exception e) {
			logger.log(Status.ERROR, "The cell with row '" + rowIndex + "' and column '" + columnIndex
					+ "' doesn't exist in the sheet" + e.getMessage());
		}
		return new DataFormatter().formatCellValue(cell);
	}

	public static int getRowUsed() throws Exception {
		int RowCount = 0;
		try {
			// RowCount = ws.getPhysicalNumberOfRows();
			RowCount = ws.getPhysicalNumberOfRows();
			// int Row = RowCount - 1;
			// logger.log(Status.INFO,"Total number of Row used return as excluding Header
			// Row < " + Row + " >.");
		} catch (Exception e) {
			logger.log(Status.ERROR, "Class ExcelUtil | Method getRowUsed | Exception desc : " + e.getMessage());
		}
		return RowCount;

	}

	public static int getColumnUsed() throws Exception {
		int columnCount = 0;
		try {
			Row row = ws.getRow(i);
			columnCount = row.getLastCellNum();
			// logger.log(Status.INFO,"Column no.:"+ i + "column:"+columnCount);

		} catch (Exception e) {
			logger.log(Status.ERROR, "Class ExcelUtil | Method getColumnUsed | Exception desc : " + e.getMessage());
		}
		return columnCount;

	}

	/*
	 * @SuppressWarnings("static-access") public static void
	 * setCellDataMcpEndToEndFlow(String Result, int RowNum, int ColNum) throws
	 * Exception { try { Row = (HSSFRow) ws.getRow(RowNum); cell =
	 * Row.getCell(ColNum, wb.getMissingCellPolicy()); if (cell == null) { cell =
	 * Row.createCell(ColNum); cell.setCellValue(Result); } else {
	 * cell.setCellValue(Result); } // Constant variables Test Data path and Test
	 * Data file name FileOutputStream fileOut = new
	 * FileOutputStream(System.getProperty(Config.User_Dir) + "");
	 * wb.write(fileOut); fileOut.flush(); fileOut.close(); } catch (Exception e) {
	 * logger.log(Status.ERROR, e.getMessage()); } }
	 */

	public static ArrayList<Object> fetchExcelLaunchAngularPlayers() throws Exception {
		try {
			Excel.setExcelFile(System.getProperty(Config.User_Dir) + Config.File_Path_LaunchAngularPlayerTest,
					Config.File_Sheet_LaunchAngularPlayerTest);
			dataList = new ArrayList<Object>();
			int totalRows = Excel.getRowUsed();
			for (i = 1; i < totalRows; i++) {
				int totalColumns = getColumnUsed();
				dataLine = new Object[totalColumns];
				for (int k = 0; k < totalColumns; k++) {
					dataLine[k] = Excel.getCell(i, k);
				}
				dataList.add(dataLine);
				// i++;
			}
		} catch (Exception e) {
			logger.log(Status.ERROR, e.getMessage());
		}
		return dataList;
	}

}
