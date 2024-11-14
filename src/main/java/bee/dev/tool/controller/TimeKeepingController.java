package bee.dev.tool.controller;

import bee.dev.tool.service.TimeKeepingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("timekeeping")
@Slf4j
public class TimeKeepingController {

    @Autowired
    private TimeKeepingService timeKeepingService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            for (MultipartFile file : files) {
                System.out.println("Uploaded file: " + file.getOriginalFilename());
            }
            return ResponseEntity.ok("Files uploaded successfully!");
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files.");
        }
    }
}
