package bpi.com.M9Activity6;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @PreAuthorize("#username == authentication.name")
    @GetMapping(value = "/profile/username/{username}", produces = "text/plain")
    public String getProfileByUsername(@PathVariable String username) {
        return "Profile of " + username;
    }
}
