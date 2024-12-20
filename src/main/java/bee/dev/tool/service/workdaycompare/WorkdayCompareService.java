package bee.dev.tool.service.workdaycompare;

import bee.dev.tool.model.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WorkdayCompareService {
    Response compare(Integer daysInMonth, List<MultipartFile> systemFiles, List<MultipartFile> compareFiles);
    Response system(Integer daysInMonth, List<MultipartFile> systemFiles);
    Response compare(Integer daysInMonth, List<MultipartFile> compareFiles);
    Response export(String[][] tableData);
}

