package cn.jason.controller;

import cn.jason.StreamingAssistant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {
    @Autowired
    private StreamingAssistant assistant;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam("message") String message) {
        return assistant.chat(1L,message)
                .doOnNext(System.out::println)
                .doOnComplete(()-> System.out.println("对话结束"))
                .doOnError(e -> System.out.println("对话出错: " + e.getMessage()));
    }
}

