package bee.dev.tool.service.workdaystandardize;

import bee.dev.tool.model.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WorkdayStandardizeService {
    Response handleAndExport(List<MultipartFile> files);
}
