package cn.jason;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class HelloTool {

    @Tool("返回当前日期")
    public String currentDate() {
        return "今天是 " + LocalDate.now();
    }

    @Tool("返回当前时间")
    public String currentTime() {
        return "现在时间是 " + LocalTime.now();
    }
}
