package bpi.com.M8Activity3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private LoggerService loggerService;

    // Setter-based DI
    @Autowired
    public void setLoggerService(LoggerService loggerService) {
        this.loggerService = loggerService;
    }
    
    public void processBooks() {
        loggerService.log("This is setter injection ");
    }
}
