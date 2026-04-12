package bpi.com.M9Activity6;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/dashboard", produces = "text/plain")
    public String dashboard() {
        return "User dashboard";
    }
}