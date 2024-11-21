package bee.dev.tool.controller;

import bee.dev.tool.model.Response;
import bee.dev.tool.model.WorkdayExplain;
import bee.dev.tool.service.workdayexplain.WorkdayExplainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/export")
    public ResponseEntity<Response> export(
            @RequestBody List<WorkdayExplain> workdayExplains
    ) {
        Response response = workdayExplainService.export(workdayExplains);
        return ResponseEntity.ok(response);
    }
}
