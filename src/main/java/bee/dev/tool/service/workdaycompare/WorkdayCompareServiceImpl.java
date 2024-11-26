package bee.dev.tool.service.workdaycompare;

import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;
import bee.dev.tool.model.WorkdayCompare;
import bee.dev.tool.utils.ExcelReader;
import bee.dev.tool.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Year;
import java.time.YearMonth;
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


                String lastCompare = dataCompare[dataCompare.length - 1];
                String lastSystem = dataSystem[dataSystem.length - 1];

                System.out.println();
                System.out.println(Arrays.toString(dataCompare));
                System.out.println(Arrays.toString(dataSystem));
                System.out.println();

                if (lastCompare != null && lastSystem != null) {
                    if (Double.parseDouble(lastCompare) != Double.parseDouble(lastSystem)) {
                        WorkdayCompare workdayCompare = new WorkdayCompare();
                        workdayCompare.setStaffCode(String.valueOf(key));
                        workdayCompare.setStaffCompare(dataCompare);
                        workdayCompare.setStaffSystem(dataSystem);
                        compares.add(workdayCompare);
                        continue;
                    }
                }

                for (int i = 2; i < dataCompare.length; i++) {
                    if (!dataCompare[i].trim().equalsIgnoreCase(dataSystem[i].trim())) {
                        WorkdayCompare workdayCompare = new WorkdayCompare();
                        workdayCompare.setStaffCode(String.valueOf(key));
                        workdayCompare.setStaffCompare(dataCompare);
                        workdayCompare.setStaffSystem(dataSystem);
                        compares.add(workdayCompare);
                        break;
                    }
                }
            }

            log.info("Tổng số dữ liệu hệ thống: {}", dataSystems.size());
            log.info("Tổng số dữ liệu so sánh: {}", dataCompares.size());
            log.info("Tổng số người sai phát hiện sai sót: {}", compares.size());

            response = Utils.createResponse(ResponseCode.SUCCESS);
            response.setData(compares);
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
                getData(daysInMonth, dataCompare, data, 8);
            }
        }
        return dataCompare;
    }

    private Map<Double, String[]> readDataSystemFiles(List<MultipartFile> systemFiles, int daysInMonth) throws IOException {
        Map<Double, String[]> dataSystem = new HashMap<>();
        for (MultipartFile file : systemFiles) {
            if (file.isEmpty()) continue;
            String[][] data = ExcelReader.readVisibleSheetData(file, 0);
            getData(daysInMonth, dataSystem, data, 6);
        }
        return dataSystem;
    }

    private void getData(int daysInMonth, Map<Double, String[]> dataCompare, String[][] data, Integer space) {
        for (int i = 0; i < data.length; i++) {
            if (data[i][1] == null) continue;
            try {
                Double id = Double.parseDouble(data[i][1].trim());
                if (id < 1000) continue;
            } catch (NumberFormatException e) {
                continue;
            }
            String[] dataStaff = new String[daysInMonth + 3];
            dataStaff[0] = data[i][1].trim();
            dataStaff[1] = data[i][2];

            for (int t = 2; t < daysInMonth + 2; t++) {
                dataStaff[t] = data[i][t + 2];
            }
            dataStaff[daysInMonth + 2] = data[i][3 + daysInMonth + space];
            dataCompare.put(Double.parseDouble(data[i][1].trim()), dataStaff);
        }
    }
}
