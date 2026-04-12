package bpi.com.M9Activity6;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @PreAuthorize("hasAnyRole('USER','MANAGER')")
    @GetMapping(value = "/home", produces = "text/plain")
    public String home() {
        return "Welcome to the portal";
    }
}