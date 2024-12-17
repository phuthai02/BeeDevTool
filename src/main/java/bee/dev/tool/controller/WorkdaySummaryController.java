package bee.dev.tool.controller;

import bee.dev.tool.service.workdaysummary.WorkdaySummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workday-summary")
public class WorkdaySummaryController {

    @Autowired
    private WorkdaySummaryService workdaySummaryService;



}
