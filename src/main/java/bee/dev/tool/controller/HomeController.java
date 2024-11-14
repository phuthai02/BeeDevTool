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

    @GetMapping("/timekeeping")
    public String getTimekeepingLayout() {
        return "timekeeping/timekeeping";
    }
}
