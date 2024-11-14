package bee.dev.tool.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public static List<List<String>> readVisibleSheetData(MultipartFile file, int sheetIndex) throws IOException {
        List<List<String>> sheetData = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            if (sheetIndex >= 0 && sheetIndex < workbook.getNumberOfSheets()) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);

                // Đọc các dòng visible
                for (Row row : sheet) {
                    // Kiểm tra xem dòng có bị ẩn hay không
                    if (row.getZeroHeight()) {
                        continue; // Bỏ qua dòng bị ẩn
                    }

                    List<String> rowData = new ArrayList<>();

                    // Đọc các cell của dòng, chỉ các cột không bị ẩn
                    for (Cell cell : row) {
                        // Kiểm tra nếu cột bị ẩn
                        if (sheet.getColumnWidth(cell.getColumnIndex()) == 0) {
                            continue; // Bỏ qua cột bị ẩn
                        }

                        // Kiểm tra nếu ô thuộc một vùng gộp (merged cell)
                        if (isMergedCell(sheet, cell)) {
                            continue; // Bỏ qua cell thuộc vùng gộp
                        }

                        rowData.add(getCellValueAsString(cell));
                    }
                    sheetData.add(rowData);
                }
            }
        }

        return sheetData;
    }

    private static boolean isMergedCell(Sheet sheet, Cell cell) {
        for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
            if (mergedRegion.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return true; // Cell thuộc vùng gộp
            }
        }
        return false;
    }

    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
