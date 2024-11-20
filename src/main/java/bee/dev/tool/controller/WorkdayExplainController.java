package bee.dev.tool.controller;

import bee.dev.tool.model.Response;
import bee.dev.tool.service.workdayexplain.WorkdayExplainService;
import bee.dev.tool.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.List;

@RestController
@RequestMapping("/workday-explain")
public class WorkdayExplainController {

    @Autowired
    private WorkdayExplainService workdayExplainService;

    @PostMapping("/upload")
    public ResponseEntity<Response> upload(
            @RequestParam("files") List<MultipartFile> files
    ) {
        Response response = workdayExplainService.upload(files);
        return ResponseEntity.ok(response);
    }
}
