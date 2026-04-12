package bpi.com.M9Activity4;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportsController {

    @GetMapping("/reports")
    public String reports() {
        return "Manager reports";
    }
}
