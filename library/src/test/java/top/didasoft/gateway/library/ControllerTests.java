package top.didasoft.gateway.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.didasoft.gateway.library.controller.HomeController;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ControllerTests {

    @Autowired
    private HomeController homeController;

    @Test
    public void loadContext() {
        assertThat(homeController).isNotNull();
    }
}
