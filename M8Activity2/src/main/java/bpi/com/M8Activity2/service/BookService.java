package bpi.com.M8Activity2.service;

import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final LoggerService loggerService;

    // Constructor-based DI 
    public BookService(LoggerService loggerService) {
        this.loggerService = loggerService;
    }
    
    public void processBooks() {
        loggerService.log("This is constructor injection ");
    }
}
