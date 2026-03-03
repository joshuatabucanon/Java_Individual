package bpi.com.M8Activity4.service;

import org.springframework.stereotype.Service;

@Service 
public class LoggerService {

    public void log(String msg) {
        System.out.println("[LoggerService] " + msg);
    }
}