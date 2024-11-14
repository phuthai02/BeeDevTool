package bee.dev.tool.utils;

import bee.dev.tool.model.CellStyleConfig;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {

    public static Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

    public static Sheet createSheet(Workbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }

    public static Row createRow(Sheet sheet, int rowIndex) {
        return sheet.createRow(rowIndex);
    }

    public static void setColumnWidth(Sheet sheet, int columnIndex, int width) {
        sheet.setColumnWidth(columnIndex, width * 256);
    }

    public static void setRowHeight(Row row, short height) {
        row.setHeight(height);
    }

    public static Cell createCell(Row row, Integer cellIndex, Object value, CellStyleConfig config) {
        Cell cell = row.createCell(cellIndex);
        setCellValueBasedOnType(cell, value);

        // Tạo Style cho cell
        XSSFCellStyle cellStyle = (XSSFCellStyle) row.getSheet().getWorkbook().createCellStyle();

        // Thiết lập màu nền cho cell
        if (config.getBackgroundColor() != null) {
            cellStyle.setFillForegroundColor(new XSSFColor(config.getBackgroundColor(), null));
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        // Thiết lập chữ cho cell
        Font font = row.getSheet().getWorkbook().createFont();
        font.setColor(config.getTextColor() == null ? IndexedColors.BLACK.getIndex() : config.getTextColor());
        font.setBold(config.getIsBold() == null ? false : true);
        font.setItalic(config.getIsItalic() == null ? false : true);
        cellStyle.setFont(font);


        // Thiết lập căn chỉnh vị trí
        if (config.getHorizontalAlignment() != null) {
            cellStyle.setAlignment(config.getHorizontalAlignment());
        }
        if (config.getVerticalAlignment() != null) {
            cellStyle.setVerticalAlignment(config.getVerticalAlignment());
        } else {
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        }

        cellStyle.setWrapText(config.getIsWrapText());

        // Thiết lập đường viền
        setBorders(cellStyle, BorderStyle.THIN);
        if (config.getMergedRegion() != null) {
            row.getSheet().addMergedRegion(config.getMergedRegion());
            applyBordersToMergedRegion(row.getSheet(), config.getMergedRegion(), cellStyle);
        }

        cell.setCellStyle(cellStyle);
        return cell;
    }

    private static void setBorders(CellStyle cellStyle, BorderStyle borderStyle) {
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
    }

    private static void setCellValueBasedOnType(Cell cell, Object value) {
        CellStyle numberCellStyle = cell.getSheet().getWorkbook().createCellStyle();
        numberCellStyle.setDataFormat(cell.getSheet().getWorkbook().createDataFormat().getFormat("#,##0.00"));

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
            cell.setCellStyle(numberCellStyle);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
            cell.setCellStyle(numberCellStyle);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
            cell.setCellStyle(numberCellStyle);
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
    }

    private static void applyBordersToMergedRegion(Sheet sheet, CellRangeAddress region, CellStyle style) {
        for (int rowIdx = region.getFirstRow(); rowIdx <= region.getLastRow(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row == null) {
                row = sheet.createRow(rowIdx);
            }
            for (int colIdx = region.getFirstColumn(); colIdx <= region.getLastColumn(); colIdx++) {
                Cell cell = row.getCell(colIdx);
                if (cell == null) {
                    cell = row.createCell(colIdx);
                }
                cell.setCellStyle(style);
            }
        }
    }
}
