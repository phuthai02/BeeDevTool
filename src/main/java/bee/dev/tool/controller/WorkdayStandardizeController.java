package bee.dev.tool.controller;

import bee.dev.tool.model.Response;
import bee.dev.tool.service.workdaystandardize.WorkdayStandardizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/workday-standardize")
public class WorkdayStandardizeController {

    @Autowired
    private WorkdayStandardizeService workdayStandardizeService;

    @PostMapping("/handle")
    public ResponseEntity<Response> upload(
            @RequestParam("files") List<MultipartFile> files
    ) {
        Response response = workdayStandardizeService.handleAndExport(files);
        return ResponseEntity.ok(response);
    }
}
