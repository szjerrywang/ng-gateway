package top.didasoft.core.zk.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    @Value("${instanceValue:default}")
    private String instanceValue;

    @GetMapping("/helloworld")
    public String helloWorld() {
        return "Hello World! " + instanceValue;
    }
}
