package bpi.com.M8Activity5.service;

import org.springframework.stereotype.Service;

@Service
public class DemoService {

    private final SingletonService s1;
    private final SingletonService s2;
    private final PrototypeService p1;
    private final PrototypeService p2;
    
    //Constructor injection
    public DemoService(
        SingletonService s1,
        SingletonService s2,
        PrototypeService p1,
        PrototypeService p2
    ) {
        this.s1 = s1;
        this.s2 = s2;
        this.p1 = p1;
        this.p2 = p2;
    }

    public void print() {
        System.out.println("s1 == s2 Singleton Same? " + (s1 == s2));   // true
        System.out.println("p1 == p2 Prototype Same? " + (p1 == p2));   // false
    }
}