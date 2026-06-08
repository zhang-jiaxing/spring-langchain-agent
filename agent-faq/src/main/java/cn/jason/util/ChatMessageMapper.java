package cn.jason.util;

import cn.jason.dto.ChatMessageWrapper;
import dev.langchain4j.data.message.*;

import java.time.Instant;
import java.util.List;

public class ChatMessageMapper {

    // LangChain4j -> Wrapper（写入 Mongo 时调用）
    public static ChatMessageWrapper fromMessage(ChatMessage message) {
        String role;
        if (message instanceof SystemMessage) {
            role = "system";
        } else if (message instanceof UserMessage) {
            role = "user";
        } else if (message instanceof AiMessage) {
            role = "assistant";
        } else {
            throw new IllegalArgumentException("Unknown ChatMessage type: " + message.getClass());
        }

        // ✅ 如果是 UserMessage，清理 Answer using... 内容
        String text = message.text();
        if ("user".equals(role) && text != null) {
            int index = text.toLowerCase().indexOf("answer using the following");
            if (index != -1) {
                text = text.substring(0, index).trim();
            }
        }

        return new ChatMessageWrapper(role, text, Instant.now());
    }

    // Wrapper -> LangChain4j（从 Mongo 读出来时调用）
    public static ChatMessage fromWrapper(ChatMessageWrapper wrapper) {
        return switch (wrapper.getRole()) {
            case "system" -> new SystemMessage(wrapper.getText());
            case "user" -> new UserMessage(wrapper.getText());
            case "assistant" -> new AiMessage(wrapper.getText());
            default -> throw new IllegalArgumentException("Unknown role: " + wrapper.getRole());
        };
    }

    // 批量转换
    public static List<ChatMessageWrapper> fromMessages(List<ChatMessage> messages) {
        return messages.stream().map(ChatMessageMapper::fromMessage).toList();
    }

    public static List<ChatMessage> fromWrappers(List<ChatMessageWrapper> wrappers) {
        return wrappers.stream().map(ChatMessageMapper::fromWrapper).toList();
    }
}
