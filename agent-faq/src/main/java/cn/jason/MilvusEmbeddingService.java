package cn.jason;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MilvusEmbeddingService {

    private final EmbeddingStore<TextSegment> embeddingStore;

    public void insert(String text, Embedding embedding) {
        String id = embeddingStore.add(embedding, TextSegment.from(text));
        System.out.println("插入成功，ID: " + id);
    }

    public List<EmbeddingMatch<TextSegment>> search(Embedding query, int k) {
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(query)
                .maxResults(k)
                .build();
        return embeddingStore.search(request).matches();
    }
}

