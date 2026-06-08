package cn.jason.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageWrapper {

    private String role;   // "user", "assistant", "system"
    private String text;   // 消息文本
    private Instant time;  // 可选：用于记录时间戳
}
