package bee.dev.tool.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

public class ExcelReader {

    public static String[][] readVisibleSheetData(MultipartFile file, int sheetIndex) throws IOException {
        String[][] sheetData = null;

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = getWorkbook(inputStream, file.getOriginalFilename());
            // Kiểm tra sheet bị ẩn hay không
            if (workbook.isSheetHidden(sheetIndex)) return new String[0][0];

            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            if (sheetIndex >= 0 && sheetIndex < workbook.getNumberOfSheets()) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);

                // Đếm số dòng và số cột để tạo mảng 2 chiều
                int rowCount = sheet.getPhysicalNumberOfRows();
                int columnCount = 0;

                // Xác định số cột tối đa trong bảng
                for (Row row : sheet) {
                    if (sheet.getColumnWidth(row.getFirstCellNum()) > 0) {
                        columnCount = Math.max(columnCount, row.getPhysicalNumberOfCells());
                    }
                }

                sheetData = new String[rowCount][columnCount];
                int rowIndex = 0;

                // Đọc các dòng visible
                for (Row row : sheet) {
                    // Kiểm tra xem dòng có bị ẩn hay không
                    if (row.getZeroHeight()) {
                        continue; // Bỏ qua dòng bị ẩn
                    }

                    // Đọc các cell của dòng, chỉ các cột không bị ẩn
                    int cellIndex = 0;
                    for (Cell cell : row) {
                        // Kiểm tra nếu cột bị ẩn
                        if (sheet.getColumnWidth(cell.getColumnIndex()) == 0) {
                            continue; // Bỏ qua cột bị ẩn
                        }

                        // Kiểm tra nếu ô thuộc một vùng gộp (merged cell)
                        if (isMergedCell(sheet, cell)) {
                            continue; // Bỏ qua cell thuộc vùng gộp
                        }

                        // Lưu giá trị vào mảng 2 chiều
                        sheetData[rowIndex][cellIndex] = getCellValueAsString(cell, formulaEvaluator);
                        cellIndex++;
                    }
                    rowIndex++;
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

    private static String getCellValueAsString(Cell cell, FormulaEvaluator formulaEvaluator) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString().trim();
                } else {
                    return String.valueOf(cell.getNumericCellValue()).trim();
                }
            case FORMULA:
                return getCellValueAsString(formulaEvaluator.evaluateInCell(cell), formulaEvaluator).trim();
            default:
                return cell.getStringCellValue().trim();
        }
    }

    public static Workbook getWorkbook(InputStream inputStream, String fileName) throws IOException {
        Workbook workbook;

        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xlsm")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".csv")) {
            workbook = convertCsvToExcel(inputStream);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please upload an Excel or CSV file.");
        }

        return workbook;
    }

    private static Workbook convertCsvToExcel(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CSV Data");
        int rowIndex = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                Row row = sheet.createRow(rowIndex++);
                String[] cells = line.split(",");
                for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(cells[cellIndex]);
                }
            }
        }
        File tempFile = File.createTempFile("temp_excel", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            workbook.write(fos);
        }
        try (InputStream tempInputStream = Files.newInputStream(tempFile.toPath())) {
            workbook = new XSSFWorkbook(tempInputStream);
        }
        if (!tempFile.delete()) {
            System.err.println("Could not delete temporary file: " + tempFile.getAbsolutePath());
        }
        return workbook;
    }
}
