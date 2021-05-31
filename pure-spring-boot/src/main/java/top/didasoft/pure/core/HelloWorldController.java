package top.didasoft.pure.core;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {



    @GetMapping("/helloworld")
    public String helloWorld(String name) {
        return "Hello " + name;
    }

    @GetMapping("/model")
    public ResponseEntity<SimpleModel> model() {
        SimpleModel simpleModel = new SimpleModel();
        simpleModel.setName("My model");
        return new ResponseEntity<SimpleModel>(simpleModel, HttpStatus.OK);
    }

    @GetMapping("/delay")
    public String delay(long delayMs) {
        PureApplication.pause(delayMs);
        return "Delayed " + delayMs;
    }
}
