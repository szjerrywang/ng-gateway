package top.didasoft.zk.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
 
    @Autowired
    private MyClass helloWorldClient;

    @GetMapping("/get-greeting")
    public String greeting() {
        return helloWorldClient.doOtherStuff();
    }
}