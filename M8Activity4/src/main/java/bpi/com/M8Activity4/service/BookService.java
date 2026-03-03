package bpi.com.M8Activity4.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    // Field-based DI
    @Autowired
    private LoggerService loggerService;

    public void processBooks() {
        loggerService.log("This is field injection ");
    }
}
