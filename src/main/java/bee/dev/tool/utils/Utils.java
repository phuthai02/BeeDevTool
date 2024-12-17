package bee.dev.tool.utils;

import bee.dev.tool.library.excel.ExcelWriter;
import bee.dev.tool.model.CellStyleConfig;
import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.awt.*;
import java.util.Objects;

@Slf4j
public class Utils {
    private static CellStyleConfig CELL_STYLE_HEADER;
    private static CellStyleConfig CELL_STYLE_NORMAL;

    public static void init() {
        // SET STYLE CELL HEADER
        CELL_STYLE_HEADER.setHorizontalAlignment(HorizontalAlignment.CENTER);
        CELL_STYLE_HEADER.setIsBold(true);
        CELL_STYLE_HEADER.setBackgroundColor(new Color(141, 215, 255));
        CELL_STYLE_HEADER.setIsWrapText(true);

        // SET STYLE CELL NORMAL
        CELL_STYLE_NORMAL.setHorizontalAlignment(HorizontalAlignment.CENTER);
        CELL_STYLE_NORMAL.setIsWrapText(true);
    }

    public static Response createResponse(final ResponseCode responseCode) {
        final Response response = new Response();
        response.setCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        return response;
    }

    public static byte[] exportExcel(String sheetName, String[][] excelData) {
        try (Workbook workbook = ExcelWriter.createWorkbook()) {
            if (excelData == null || excelData.length == 0) {
                return null;
            }
            Sheet sheet = ExcelWriter.createSheet(workbook, sheetName);

            int rowIndex = 0;
            Row headerRow = ExcelWriter.createRow(sheet, rowIndex++);

            for (int col = 0; col < excelData[0].length; col++) {
                ExcelWriter.createCell(headerRow, col, excelData[0][col], CELL_STYLE_HEADER);
                int width = 25;
                ExcelWriter.setColumnWidth(sheet, col, width);
            }

            for (int i = 1; i < excelData.length; i++) {
                Row row = ExcelWriter.createRow(sheet, rowIndex++);
                for (int j = 0; j < excelData[0].length; j++) {
                    String cellValue = (j < excelData[i].length) ? Objects.toString(excelData[i][j], "") : "";
                    ExcelWriter.createCell(row, j, cellValue, CELL_STYLE_NORMAL);
                }
            }



            
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }

        return null;
    }
}
