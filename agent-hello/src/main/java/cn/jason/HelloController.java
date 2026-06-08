package cn.jason;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HelloController {

    private final HelloAgent helloAgent;
    Logger log = (Logger) LoggerFactory.getLogger(HelloController.class);

    @GetMapping(value = "/ask-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam("input") String input) {
        return helloAgent.chat(input)
                .doOnNext(token -> log.info("Sending: {}", token))
                .doOnError(err -> log.error("Stream error", err))
                .doOnComplete(() -> log.info("Stream completed"));
    }

}
