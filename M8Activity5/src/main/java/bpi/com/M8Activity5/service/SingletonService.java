package bpi.com.M8Activity5.service;

import org.springframework.stereotype.Service;

@Service
public class SingletonService {
	public SingletonService() {
		System.out.println("SingletonService Created");
	}
}
