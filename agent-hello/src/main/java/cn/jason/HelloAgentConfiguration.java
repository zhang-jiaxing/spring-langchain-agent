package cn.jason;


import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class HelloAgentConfiguration {

    @Value("${langchain4j.chat-model.dashscope.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.chat-model.dashscope.chat-model.model-name}")
    private String modelName;

    @Bean
    public StreamingChatLanguageModel streamingChatLanguageModel() {
        return QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }

    @Bean
    public HelloAgent helloAgent(StreamingChatLanguageModel streamingChatLanguageModel) {
        return AiServices.builder(HelloAgent.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .tools(List.of(new HelloTool()))
                .build();
    }
}


