package cn.jason.configuration;

import cn.jason.MongoChatMemoryStore;
import cn.jason.StreamingAssistant;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class EmbeddingConfiguration {
    @Value("${langchain4j.dashscope.embedding.api-key}")
    private String apiKey;
    @Value("${langchain4j.dashscope.embedding.model-name}")
    private String modelName;
    @Value("${langchain4j.chat-model.dashscope.chat-model.model-name}")
    private String model;

    @Bean
    public EmbeddingModel embeddingModel() {
        return QwenEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return MilvusEmbeddingStore.builder()
                .uri("http://localhost:19530")
                .collectionName("faq_collection")
                .dimension(1024)
                .autoFlushOnInsert(true)
                .build();
    }
    @Bean
    public DocumentSplitter customSplitter() {
        return document -> {
            List<TextSegment> segments = new ArrayList<>();
            String[] lines = document.text().split("。|！|？|\\n"); // 中文句号、换行
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    segments.add(TextSegment.from(line.trim()));
                }
            }
            return segments;
        };
    }
    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(customSplitter())
                .build();
    }
    @Bean
    public StreamingChatLanguageModel streamingChatLanguageModel() {
        return QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .build();
    }
    @Bean
    public StreamingAssistant streamingAssistant(
            EmbeddingStore<TextSegment> embeddingStore,
            StreamingChatLanguageModel streamingModel,
            EmbeddingModel embeddingModel,
            MongoChatMemoryStore ChatMemoryStore
    ) {
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();

        return AiServices.builder(StreamingAssistant.class)
                .contentRetriever(contentRetriever)
                .streamingChatLanguageModel(streamingModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .chatMemoryStore(ChatMemoryStore)
                        .maxMessages(10)
                        .build())
                .build();
    }

}
