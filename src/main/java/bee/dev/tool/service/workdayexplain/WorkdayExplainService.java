package bee.dev.tool.service.workdayexplain;

import bee.dev.tool.model.Response;
import bee.dev.tool.model.WorkdayExplain;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WorkdayExplainService{
    Response upload(List<MultipartFile> files);

    Response export(List<WorkdayExplain> workdayExplains);
}
