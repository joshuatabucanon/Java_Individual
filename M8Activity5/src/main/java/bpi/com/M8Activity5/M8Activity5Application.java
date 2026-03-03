package bpi.com.M8Activity5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import bpi.com.M8Activity5.service.DemoService;

@SpringBootApplication
public class M8Activity5Application {

    public static void main(String[] args) {
    	ApplicationContext context = SpringApplication.run(M8Activity5Application.class, args);

        DemoService demo = context.getBean(DemoService.class);
        demo.print();

    }
}
