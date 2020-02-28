package edu.northeastern.ccwebapp.pojo;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@RedisHash(value = "RedisBook", timeToLive = 600)
public class RedisBook implements Serializable {

    @Id
    private String id;

    private String title;

    private String author;

    private int quantity;

    private String isbn;

}
