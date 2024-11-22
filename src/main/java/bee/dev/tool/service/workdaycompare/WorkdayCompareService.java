package bee.dev.tool.service.workdaycompare;

import bee.dev.tool.model.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WorkdayCompareService {
    Response compare(String month, List<MultipartFile> systemFiles, List<MultipartFile> compareFiles);
}
