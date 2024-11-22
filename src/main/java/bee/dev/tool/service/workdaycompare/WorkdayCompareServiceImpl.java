package bee.dev.tool.service.workdaycompare;

import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;
import bee.dev.tool.utils.ExcelReader;
import bee.dev.tool.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WorkdayCompareServiceImpl implements WorkdayCompareService {

    @Override
    public Response compare(String month, List<MultipartFile> systemFiles, List<MultipartFile> compareFiles) {
        Response response = Utils.createResponse(ResponseCode.ERROR);
        try {
            int daysInMonth = YearMonth.of(Year.now().getValue(), Integer.parseInt(month)).lengthOfMonth();
            Map<Double, String[]> dataSystem = readDataSystemFiles(systemFiles, daysInMonth);
            Map<Double, String[]> dataCompare = readDataCompareFiles(systemFiles, daysInMonth);
            response = Utils.createResponse(ResponseCode.SUCCESS);
            response.setData(null);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return response;
    }

    private Map<Double, String[]> readDataCompareFiles(List<MultipartFile> systemFiles, int daysInMonth) {
        return null;
    }

    private Map<Double, String[]> readDataSystemFiles(List<MultipartFile> systemFiles, int daysInMonth) throws IOException {
        Map<Double, String[]> dataSystem = new HashMap<>();
        for (MultipartFile file : systemFiles) {
            String[][] data = ExcelReader.readVisibleSheetData(file, 0);
            for (int i = 0; i < data.length; i++) {
                if (data[i][1] == null) {
                    continue;
                }
                try {
                    Double id = Double.parseDouble(data[i][1].trim());
                    if (id < 1000) {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
                String[] dataStaff = new String[daysInMonth + 3];
                dataStaff[0] = data[i][1].trim();
                dataStaff[1] = data[i][2];

                for (int t = 2; t < daysInMonth + 2; t++) {
                    dataStaff[t] = data[i][t + 2];
                }
                dataStaff[daysInMonth + 2] = data[i][4 + daysInMonth + 6];
                dataSystem.put(Double.parseDouble(data[i][1].trim()), dataStaff);
            }
        }
        return dataSystem;
    }
}
