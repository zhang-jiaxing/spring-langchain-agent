package cn.jason;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Slf4j
@SpringBootTest
public class MilvusServiceTest {
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private MilvusEmbeddingService milvusEmbeddingService;
    @Autowired
    private EmbeddingStoreIngestor embeddingStoreIngestor;
    @Autowired
    private StreamingAssistant assistant;

    @Test
    public void testIngestDocument() {
        ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("documents/", parser);
        embeddingStoreIngestor.ingest(documents);
        System.out.println("✅ 批量文档已入库完成");
    }
    @Test
    public void testSearch() {
        Embedding embeddedText = embeddingService.embedText("如何申请奖学金");
        List<EmbeddingMatch<TextSegment>> search = milvusEmbeddingService.search(embeddedText, 1);
        System.out.println(search);
    }

    @Test
    public void testEmbed(){
        Embedding embeddedText = embeddingService.embedText("如何申请奖学金");
        System.out.println("向量化后的数据："+embeddedText);
    }

    @Test
    void testStreamingAssistant() {
        Flux<String> flux = assistant.chat(1L,"你好，我是大连交通大学的大一新生,依然。你是谁？");
        flux
                .doOnNext(System.out::print)
                .doOnComplete(() -> System.out.println("\nflux：对话结束"))
                .doOnError(e -> System.out.println("对话出错："+e))
                .blockLast(Duration.ofSeconds(30));
        Flux<String> chat = assistant.chat(1L, "我刚才的问题是什么？");
        chat
                .doOnNext(System.out::print)
                .doOnComplete(() -> System.out.println("\nchat：对话结束"))
                .doOnError(e -> System.out.println("对话出错："+e))
                .blockLast(Duration.ofSeconds(30));
        Flux<String> chat2 = assistant.chat(2L, "我刚才的问题是什么？");
        chat2
                .doOnNext(System.out::print)
                .doOnComplete(() -> System.out.println("\nchat2：对话结束"))
                .doOnError(e -> System.out.println("对话出错："+e))
                .blockLast(Duration.ofSeconds(30));
    }
}
