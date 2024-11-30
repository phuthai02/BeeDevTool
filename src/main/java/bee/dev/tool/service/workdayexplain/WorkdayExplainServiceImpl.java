package bee.dev.tool.service.workdayexplain;

import bee.dev.tool.model.CellStyleConfig;
import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;
import bee.dev.tool.model.WorkdayExplain;
import bee.dev.tool.utils.Utils;
import bee.dev.tool.utils.ExcelReader;
import bee.dev.tool.utils.ExcelWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WorkdayExplainServiceImpl implements WorkdayExplainService {
    @Override
    public Response upload(List<MultipartFile> files) {
        Response response = Utils.createResponse(ResponseCode.ERROR);
        try {
            List<WorkdayExplain> responseData = new ArrayList<>();
            for (MultipartFile file : files) {
                String[][] data = ExcelReader.readVisibleSheetData(file, 0);
                for (int i = 0; i < data.length; i++) {
                    if (data[i][0] == null) continue;
                    try {
                        Double id = Double.parseDouble(data[i][0]);
                        if (id < 1000) continue;
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    WorkdayExplain workdayExplain = getWorkdayExplain(data[i]);
                    responseData.add(workdayExplain);

                    response = Utils.createResponse(ResponseCode.SUCCESS);
                    response.setData(responseData);
                }
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

    @Override
    public Response export(List<WorkdayExplain> workdayExplains) {
        Response response = Utils.createResponse(ResponseCode.ERROR);
        try (Workbook workbook = ExcelWriter.createWorkbook()) {
            if (workdayExplains == null || workdayExplains.size() == 0) {
                response.setMessage("Không có dữ liệu để xuất file.");
                return response;
            }
            
            Sheet sheet = ExcelWriter.createSheet(workbook, "GIẢI TRÌNH CÔNG");
            ExcelWriter.setColumnWidth(sheet, 0, 10);
            ExcelWriter.setColumnWidth(sheet, 1, 30);
            ExcelWriter.setColumnWidth(sheet, 2, 10);
            ExcelWriter.setColumnWidth(sheet, 3, 20);
            ExcelWriter.setColumnWidth(sheet, 4, 10);
            ExcelWriter.setColumnWidth(sheet, 5, 10);
            ExcelWriter.setColumnWidth(sheet, 6, 10);
            ExcelWriter.setColumnWidth(sheet, 7, 10);
            ExcelWriter.setColumnWidth(sheet, 8, 10);
            ExcelWriter.setColumnWidth(sheet, 9, 10);
            ExcelWriter.setColumnWidth(sheet, 10, 10);
            ExcelWriter.setColumnWidth(sheet, 11, 10);
            ExcelWriter.setColumnWidth(sheet, 12, 30);
            ExcelWriter.setColumnWidth(sheet, 13, 30);

            CellStyleConfig cellStyleHeader = new CellStyleConfig();
            cellStyleHeader.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cellStyleHeader.setIsBold(true);
            cellStyleHeader.setBackgroundColor(new Color(141, 215, 255));
            cellStyleHeader.setIsWrapText(true);

            int rowIndex = 4;
            Row headerRow = ExcelWriter.createRow(sheet, rowIndex++);
            ExcelWriter.createCell(headerRow, 0, "Mã nhân viên", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 1, "Họ và tên", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 2, "Bộ phân", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 3, "Ngày", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 4, "Ca", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 5, "Chi tiết ca", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 6, "Công", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 7, "Giờ vào", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 8, "Giờ ra", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 9, "Số giờ", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 10, "Số phút đi muộn", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 11, "Số phút về sớm", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 12, "Lý do tăng ca", cellStyleHeader);
            ExcelWriter.createCell(headerRow, 13, "Ghi chú", cellStyleHeader);


            CellStyleConfig cellStyleNormal = new CellStyleConfig();
            cellStyleNormal.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cellStyleNormal.setIsWrapText(true);

            for (WorkdayExplain workdayExplain : workdayExplains) {
                Row row = ExcelWriter.createRow(sheet, rowIndex++);
                ExcelWriter.createCell(row, 0, workdayExplain.getStaffCode(), cellStyleNormal);
                ExcelWriter.createCell(row, 1, workdayExplain.getStaffName(), cellStyleNormal);
                ExcelWriter.createCell(row, 2, workdayExplain.getStaffPart(), cellStyleNormal);
                ExcelWriter.createCell(row, 3, workdayExplain.getExplainDay(), cellStyleNormal);
                ExcelWriter.createCell(row, 4, workdayExplain.getShift(), cellStyleNormal);
                ExcelWriter.createCell(row, 5, workdayExplain.getShiftCode(), cellStyleNormal);
                ExcelWriter.createCell(row, 6, workdayExplain.getWorkDayCode(), cellStyleNormal);
                ExcelWriter.createCell(row, 7, workdayExplain.getTimeIn(), cellStyleNormal);
                ExcelWriter.createCell(row, 8, workdayExplain.getTimeOut(), cellStyleNormal);
                ExcelWriter.createCell(row, 9, workdayExplain.getTimeCount(), cellStyleNormal);
                ExcelWriter.createCell(row, 10, workdayExplain.getLateMinutes(), cellStyleNormal);
                ExcelWriter.createCell(row, 11, workdayExplain.getEarlyMinutes(), cellStyleNormal);
                ExcelWriter.createCell(row, 12, workdayExplain.getReason(), cellStyleNormal);
                ExcelWriter.createCell(row, 13, workdayExplain.getNote(), cellStyleNormal);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            response = Utils.createResponse(ResponseCode.SUCCESS);
            response.setData(bos.toByteArray());
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return response;
    }
}
