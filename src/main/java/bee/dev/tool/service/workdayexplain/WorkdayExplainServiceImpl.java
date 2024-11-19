package bee.dev.tool.service.workdayexplain;

import bee.dev.tool.model.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class WorkdayExplainServiceImpl implements WorkdayExplainService{
    @Override
    public Response upload(List<MultipartFile> files) {
        return null;
    }
}
