package bee.dev.tool.controller;

import bee.dev.tool.model.Response;
import bee.dev.tool.service.workdaycompare.WorkdayCompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/workday-compare")
public class WorkdayCompareController {

    @Autowired
    private WorkdayCompareService workdayCompareService;

    @PostMapping("/compare")
    public ResponseEntity<Response> compare(
            @RequestParam("daysInMonth") Integer daysInMonth,
            @RequestParam("systemFiles") List<MultipartFile> systemFiles,
            @RequestParam("compareFiles") List<MultipartFile> compareFiles
    ) {
        Response response = workdayCompareService.compare(daysInMonth, systemFiles, compareFiles);
        return ResponseEntity.ok(response);
    }


    @PostMapping("review/system")
    public ResponseEntity<Response> system(
            @RequestParam("daysInMonth") Integer daysInMonth,
            @RequestParam("systemFiles") List<MultipartFile> systemFiles
    ) {
        Response response = workdayCompareService.system(daysInMonth, systemFiles);
        return ResponseEntity.ok(response);
    }

    @PostMapping("review/compare")
    public ResponseEntity<Response> compare(
            @RequestParam("daysInMonth") Integer daysInMonth,
            @RequestParam("compareFiles") List<MultipartFile> compareFiles
    ) {
        Response response = workdayCompareService.compare(daysInMonth, compareFiles);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/export")
    public ResponseEntity<Response> export(@RequestBody String[][] tableData) {
        Response response = workdayCompareService.export(tableData);
        return ResponseEntity.ok(response);
    }
}
