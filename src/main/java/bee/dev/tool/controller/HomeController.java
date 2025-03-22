package bee.dev.tool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/beedev")
public class HomeController {

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String getHomeLayout() {
        return "home/home";
    }

    @GetMapping("/workday-explain")
    public String getWorkdayExplainLayout() {
        return "workday-explain/workday-explain";
    }

    @GetMapping("/workday-compare")
    public String getWorkdayCompareLayout() {
        return "workday-compare/workday-compare";
    }

    @GetMapping("/workday-summary")
    public String getWorkdaySummaryLayout() {
        return "workday-summary/workday-summary";
    }

    @GetMapping("/workday-standardize")
    public String getWorkdayStandardizeLayout() {
        return "workday-standardize/workday-standardize";
    }
}
