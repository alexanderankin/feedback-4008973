package springapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {
        //noinspection resource
        SpringApplication.run(SpringApp.class, args);
    }

    @Controller
    public static class ExampleController {
        @ResponseBody
        @RequestMapping
        Mono<String> get() {
            return Mono.just("hi");
        }
    }
}
