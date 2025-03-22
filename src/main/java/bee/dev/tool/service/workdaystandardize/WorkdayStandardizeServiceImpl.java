package bee.dev.tool.service.workdaystandardize;

import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;
import bee.dev.tool.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@Log4j2
public class WorkdayStandardizeServiceImpl implements WorkdayStandardizeService {

    @Override
    public Response handleAndExport(List<MultipartFile> files) {
        Response response = Utils.createResponse(ResponseCode.ERROR);
        try {
            MultipartFile multipartFile = files.get(0);
            Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getCellType() == CellType.STRING) {
                            String transformedValue = transform(cell.getStringCellValue());
                            cell.setCellValue(transformedValue);
                        }
                    }
                }
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            response = Utils.createResponse(ResponseCode.SUCCESS, bos.toByteArray());
        } catch (Exception e) {
           log.info(e.getMessage(), e);
        }
        return response;
    }

    private String transform(String value) {
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