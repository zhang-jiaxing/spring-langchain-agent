package cn.jason.pojo;

import cn.jason.dto.ChatMessageWrapper;
import dev.langchain4j.data.message.ChatMessage;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Document(collection = "memory")
public class ChatMemoryDocument {

    @Id
    private String memoryId;

    @Field("messages")
    private List<ChatMessageWrapper> messages;

    @Field("updated_at")
    private Instant updatedAt;
}



