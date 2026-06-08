package cn.jason;

import reactor.core.publisher.Flux;

public interface HelloAgent {

    Flux<String> chat(String message);
}
