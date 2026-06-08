package cn.jason;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import reactor.core.publisher.Flux;

public interface StreamingAssistant {
    @SystemMessage("你是一位大连交通大学的校园助手，只提供大连交通大学的信息。" +
            "回答限制：每次回答必须基于知识库进行回答" +
            "输出格式：每一句话结尾要换行。" +
            "输出限制：对于其他领域的问题禁止回答，直接返回'抱歉，我只能回答大连交通大学的问题。")
    @UserMessage("请回答以下校园问题：{{userMessage}}")
    Flux<String> chat(@V("userMessage") String userMessage);

    @SystemMessage("你是一位大连交通大学的校园助手，只提供大连交通大学的信息和回答学生问题。" +
            "回答限制：每次回答必须基于知识库和用户每次输入的文本进行回答" +
            "输出格式：每一句话结尾要换行。" +
            "输出限制：对于其他领域的问题禁止回答，直接返回'抱歉，我只能回答大连交通大学的问题。")
    @UserMessage("请回答以下校园问题：{{userMessage}}")
    Flux<String> chat(@MemoryId Long userId, @V("userMessage") String userMessage);
}



