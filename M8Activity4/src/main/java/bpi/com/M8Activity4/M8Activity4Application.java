package bpi.com.M8Activity4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import bpi.com.M8Activity4.service.BookService;

@SpringBootApplication
public class M8Activity4Application {

    public static void main(String[] args) {
    	ApplicationContext context = SpringApplication.run(M8Activity4Application.class, args);

        BookService bs = context.getBean(BookService.class);
        bs.processBooks();
    }
}
