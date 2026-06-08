package cn.jason;

import cn.jason.dto.ChatMessageWrapper;
import cn.jason.pojo.ChatMemoryDocument;
import cn.jason.repository.ChatMemoryRepository;
import cn.jason.util.ChatMessageMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoChatMemoryStore implements ChatMemoryStore {

    private final ChatMemoryRepository repository;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return repository.findById(memoryId.toString())
                .map(doc -> ChatMessageMapper.fromWrappers(doc.getMessages()))
                .orElse(List.of());
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        List<ChatMessageWrapper> wrappers = ChatMessageMapper.fromMessages(messages);
        repository.save(ChatMemoryDocument.builder()
                .memoryId(memoryId.toString())
                .messages(wrappers)
                .updatedAt(Instant.now())
                .build());
    }

    @Override
    public void deleteMessages(Object memoryId) {
        repository.deleteById(memoryId.toString());
    }
}
