package bee.dev.tool.service.workdaycompare;

import bee.dev.tool.model.Response;
import bee.dev.tool.model.ResponseCode;
import bee.dev.tool.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class WorkdayCompareServiceImpl implements WorkdayCompareService{
    @Override
    public Response compare(List<MultipartFile> systemFile, List<MultipartFile> compareFiles) {




        return Utils.createResponse(ResponseCode.SUCCESS);
    }
}
