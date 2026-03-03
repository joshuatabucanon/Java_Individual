package bpi.com.M8Activity5.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class PrototypeService {
	public PrototypeService() {
		System.out.println("PrototypeService Created");
	}
}
