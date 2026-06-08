package cn.jason.repository;

import cn.jason.pojo.ChatMemoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMemoryRepository extends MongoRepository<ChatMemoryDocument, String> {
}

