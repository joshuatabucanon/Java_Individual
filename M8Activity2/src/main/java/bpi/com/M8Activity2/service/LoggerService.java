package bpi.com.M8Activity2.service;

import org.springframework.stereotype.Service;

@Service // You could also use @Component here
public class LoggerService {

    public void log(String msg) {
        System.out.println("[LoggerService] " + msg);
    }
}