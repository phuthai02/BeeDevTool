package bee.dev.tool.service.workdaycompare;

import bee.dev.tool.model.CellStyleConfig;
import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;
import bee.dev.tool.model.WorkdayCompare;
import bee.dev.tool.library.excel.ExcelReader;
import bee.dev.tool.library.excel.ExcelWriter;
import bee.dev.tool.utils.Utils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;

@Service
@Slf4j
public class WorkdayCompareServiceImpl implements WorkdayCompareService {

    @Override
    public Response compare(Integer daysInMonth, List<MultipartFile> systemFiles, List<MultipartFile> compareFiles) {
        Response response = Utils.createResponse(ResponseCode.ERROR);
        List<WorkdayCompare> compares = new ArrayList<>();
        try {
            Map<Double, String[]> dataSystems = readDataSystemFiles(systemFiles, daysInMonth);
            Map<Double, String[]> dataCompares = readDataCompareFiles(compareFiles, daysInMonth);
            for (Double key : dataCompares.keySet()) {
                String[] dataCompare = dataCompares.get(key);
                String[] dataSystem = dataSystems.get(key);

                if (dataSystem == null || dataSystem.length == 0 || dataSystem[0].length() == 0) {
                    WorkdayCompare workdayCompare = new WorkdayCompare();
                    workdayCompare.setStaffCode(String.valueOf(key));
                    workdayCompare.setStaffCompare(dataCompare);
                    workdayCompare.setStaffSystem(new String[0]);
                    compares.add(workdayCompare);
                    continue;
                }

                if (dataCompare.length > daysInMonth + 3) {
                    dataCompare = Arrays.copyOf(dataCompare, dataCompare.length - 1);
                }

                if (dataSystem.length > daysInMonth + 3) {
                    dataSystem = Arrays.copyOf(dataSystem, dataSystem.length - 1);
                }

                String lastCompare = dataCompare[dataCompare.length - 1];
                String lastSystem = dataSystem[dataSystem.length - 1];

                log.info("HT: {}", Arrays.toString(dataSystem));
                log.info("ĐC: {}", Arrays.toString(dataCompare));

                if (lastCompare != null && lastSystem != null) {
                    if (lastCompare.isEmpty() || lastSystem.isEmpty() || Double.parseDouble(lastCompare) != Double.parseDouble(lastSystem)) {
                        WorkdayCompare workdayCompare = new WorkdayCompare();
                        workdayCompare.setStaffCode(String.valueOf(key));
                        workdayCompare.setStaffCompare(dataCompare);
                        workdayCompare.setStaffSystem(dataSystem);
                        compares.add(workdayCompare);
                        continue;
                    }
                }

                for (int i = 2; i < dataCompare.length; i++) {
                    if (!dataCompare[i].equalsIgnoreCase(dataSystem[i])) {
                        WorkdayCompare workdayCompare = new WorkdayCompare();
                        workdayCompare.setStaffCode(String.valueOf(key));
                        workdayCompare.setStaffCompare(dataCompare);
                        workdayCompare.setStaffSystem(dataSystem);
                        compares.add(workdayCompare);
                        break;
                    }
                }
            }

            log.info("Tong so du lieu he thong: {}", dataSystems.size());
            log.info("Tong so du lieu doi chieu: {}", dataCompares.size());
            log.info("Tong so nguoi sai phat hien sai sot: {}", compares.size());

            response = Utils.createResponse(ResponseCode.SUCCESS);
            response.setData(compares);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return response;
    }

    @Override
    public Response system(Integer daysInMonth, List<MultipartFile> systemFiles) {
        Response response = Utils.createResponse(ResponseCode.ERROR);
        List<WorkdayCompare> systems = new ArrayList<>();
        try {
            Map<Double, String[]> dataSystems = readDataSystemFiles(systemFiles, daysInMonth);
            for (Double key : dataSystems.keySet()) {
                WorkdayCompare workdayCompare = new WorkdayCompare();
                workdayCompare.setStaffCode(String.valueOf(key));
                workdayCompare.setStaffSystem(dataSystems.get(key));
                systems.add(workdayCompare);
            }
            response = Utils.createResponse(ResponseCode.SUCCESS);
            response.setData(systems);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return response;
    }

    @Override
    public Response compare(Integer daysInMonth, List<MultipartFile> compareFiles) {
        Response response = Utils.createResponse(ResponseCode.ERROR);
        List<WorkdayCompare> compares = new ArrayList<>();
        try {
            Map<Double, String[]> dataCompares = readDataCompareFiles(compareFiles, daysInMonth);
            for (Double key : dataCompares.keySet()) {
                WorkdayCompare workdayCompare = new WorkdayCompare();
                workdayCompare.setStaffCode(String.valueOf(key));
                workdayCompare.setStaffCompare(dataCompares.get(key));
                compares.add(workdayCompare);
            }
            response = Utils.createResponse(ResponseCode.SUCCESS);
            response.setData(compares);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return response;
    }

    @Override
    public Response export(String[][] tableData) {
        Response response = Utils.createResponse(ResponseCode.ERROR);
        try (Workbook workbook = ExcelWriter.createWorkbook()) {
            if (tableData == null || tableData.length == 0) {
                response.setMessage("Không có dữ liệu để xuất file.");
                return response;
            }

            Sheet sheet = ExcelWriter.createSheet(workbook, "ĐỐI CHIẾU CÔNG");
            CellStyleConfig cellStyleHeader = new CellStyleConfig();
            cellStyleHeader.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cellStyleHeader.setIsBold(true);
            cellStyleHeader.setBackgroundColor(new Color(141, 215, 255));
            cellStyleHeader.setIsWrapText(true);

            int rowIndex = 0;
            Row headerRow = ExcelWriter.createRow(sheet, rowIndex++);
            for (int col = 0; col < tableData[0].length; col++) {
                ExcelWriter.createCell(headerRow, col, tableData[0][col], cellStyleHeader);
                int width = (col == 1) ? 10 : (col == 2) ? 20 : (col == tableData[0].length - 1) ? 10 : 7;
                ExcelWriter.setColumnWidth(sheet, col, width);
            }

            CellStyleConfig cellStyleNormal = new CellStyleConfig();
            cellStyleNormal.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cellStyleNormal.setIsWrapText(true);

            for (int i = 1; i < tableData.length; i++) {
                Row row = ExcelWriter.createRow(sheet, rowIndex++);
                for (int j = 0; j < tableData[0].length; j++) {
                    String cellValue = (j < tableData[i].length) ? Objects.toString(tableData[i][j], "") : "";
                    ExcelWriter.createCell(row, j, cellValue, cellStyleNormal);
                }
                if (i % 2 == 0 && i < tableData.length - 1) {
                    Row separatorRow = ExcelWriter.createRow(sheet, rowIndex++);
                    for (int j = 0; j < tableData[0].length; j++) {
                        ExcelWriter.createCell(separatorRow, j, "-", cellStyleHeader);
                    }
                }
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            response = Utils.createResponse(ResponseCode.SUCCESS);
            response.setData(bos.toByteArray());
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return response;
    }

    private Map<Double, String[]> readDataCompareFiles(List<MultipartFile> compareFiles, int daysInMonth) throws IOException {
        Map<Double, String[]> dataCompare = new HashMap<>();
        for (MultipartFile file : compareFiles) {
            if (file.isEmpty()) continue;
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            int sheetCount = workbook.getNumberOfSheets();
            for (int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
                String[][] data = ExcelReader.readVisibleSheetData(file, sheetIndex);
                filterData(daysInMonth, dataCompare, data, 8);
            }
        }
        return dataCompare;
    }

    private Map<Double, String[]> readDataSystemFiles(List<MultipartFile> systemFiles, int daysInMonth) throws IOException {
        Map<Double, String[]> dataSystem = new HashMap<>();
        for (MultipartFile file : systemFiles) {
            if (file.isEmpty()) continue;
            String[][] data = ExcelReader.readVisibleSheetData(file, 0);
            filterData(daysInMonth, dataSystem, data, 6);
        }
        return dataSystem;
    }

    private void filterData(int daysInMonth, Map<Double, String[]> dataCompare, String[][] data, Integer space) {
        List<Double> existingKeys = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (StringUtils.isBlank(Objects.toString(data[i][1], ""))) continue;
            try {
                Double id = Double.parseDouble(data[i][1]);
                if (id < 1000) continue;
                String[] dataStaff = new String[daysInMonth + 3];
                if (dataCompare.containsKey(id)) {
                    String[] existingData = dataCompare.get(id);
                    dataStaff[0] = Objects.toString(data[i][1], "");
                    dataStaff[1] = Objects.toString(data[i][2], "");
                    for (int t = 2; t < daysInMonth + 2; t++) {
                        String valueA = Objects.toString(data[i][t + 2], "");
                        String valueB = existingData[t];
                        dataStaff[t] = StringUtils.isNotBlank(valueA) ? transformValue(valueA) : transformValue(valueB);
                    }
                    Double valueA = Double.parseDouble(existingData[daysInMonth + 2]);
                    Double valueB = Double.parseDouble(Objects.toString(data[i][3 + daysInMonth + space], "0"));
                    dataStaff[daysInMonth + 2] = String.valueOf(valueA + valueB);
                    existingKeys.add(id);
                } else {
                    dataStaff[0] = Objects.toString(data[i][1], "");
                    dataStaff[1] = Objects.toString(data[i][2], "");
                    for (int t = 2; t < daysInMonth + 2; t++)
                        dataStaff[t] = transformValue(Objects.toString(data[i][t + 2], ""));
                    dataStaff[daysInMonth + 2] = Objects.toString(data[i][3 + daysInMonth + space], "0");
                }
                dataCompare.put(id, dataStaff);
            } catch (NumberFormatException ignored) {
            }
        }
        String keysInfo = existingKeys.isEmpty() ? "" : ": " + String.join(", ", existingKeys.stream().map(key -> String.valueOf(key.intValue())).toArray(String[]::new));
        log.info("Co " + existingKeys.size() + " ma nhan vien giai trinh cong sai" + keysInfo + ".");
    }

    private String transformValue(String value) {
        if (value.matches("X \\(\\d+(\\.\\d+)?\\)")) {
            String numericPart = value.substring(value.indexOf('(') + 1, value.indexOf(')'));
            try {
                double numericValue = Double.parseDouble(numericPart);
                if (numericValue > 5) {
                    return "X";
                } else {
                    return "X/2";
                }
            } catch (NumberFormatException e) {
                return value;
            }
        }
        return value;
    }
}
