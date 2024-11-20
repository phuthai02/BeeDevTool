package bee.dev.tool.service.workdayexplain;

import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;
import bee.dev.tool.model.WorkdayExplain;
import bee.dev.tool.utils.Common;
import bee.dev.tool.utils.ExcelReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WorkdayExplainServiceImpl implements WorkdayExplainService {
    @Override
    public Response upload(List<MultipartFile> files) {
        Response response = Common.createResponse(ResponseCode.ERROR);
        try {
            List<WorkdayExplain> responseData = new ArrayList<>();
            String[][] data = ExcelReader.readVisibleSheetData(files.get(0), 0);
            for (int i = 0; i < data.length; i++) {
                if (data[i][0] == null) continue;
                try {
                    Double.parseDouble(data[i][0]);
                } catch (NumberFormatException e) {
                    continue;
                }
                WorkdayExplain workdayExplain = getWorkdayExplain(data[i]);
                responseData.add(workdayExplain);

                response = Common.createResponse(ResponseCode.SUCCESS);
                response.setData(responseData);
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return response;
    }

    private static WorkdayExplain getWorkdayExplain(String[] data) {
        WorkdayExplain workdayExplain = new WorkdayExplain();
        workdayExplain.setStaffCode(data[0]);
        workdayExplain.setStaffName(data[1]);
        workdayExplain.setStaffPart(data[3]);
        workdayExplain.setExplainDay(data[4]);
        workdayExplain.setShift("VP");
        workdayExplain.setShiftCode("SA");
        workdayExplain.setWorkDayCode("VP");
        workdayExplain.setTimeIn(data[5]);
        workdayExplain.setTimeOut(data[7]);
        workdayExplain.setReason(data[8]);
        return workdayExplain;
    }
}
