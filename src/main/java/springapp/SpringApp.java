package springapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {
        //noinspection resource
        SpringApplication.run(SpringApp.class, args);
    }

    @Controller
    public static class ExampleController {
        private static final Scheduler schedulerFromLib = Schedulers.boundedElastic();
        private static final Scheduler schedulerCustom = Schedulers.newBoundedElastic(5, 10, "schedulerCustom");

        @SuppressWarnings("DuplicatedCode") // for debugging the ide!
        @ResponseBody
        @RequestMapping
        Mono<String> get(@RequestParam(required = false) String block) {
            // issues warning, the expected way
            //noinspection CallingSubscribeInNonBlockingScope
            Mono.fromCallable(() -> 1).subscribe();

            // no warning issued, also expected
            Mono.fromCallable(() -> 1).subscribeOn(schedulerFromLib).subscribe();

            // no warning issued, unlike the issue I thought I was seeing
            Mono.fromCallable(() -> 1).subscribeOn(schedulerCustom).subscribe();

            // the only real issue, no warning issued
            Mono.fromCallable(() -> { Thread.sleep(100); return 1; }).subscribeOn(Schedulers.immediate()).subscribe();

            return Mono.just("hi");
        }
    }
}
