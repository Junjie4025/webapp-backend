package edu.northeastern.ccwebapp.repository;

import edu.northeastern.ccwebapp.pojo.RedisBook;
import org.springframework.data.repository.CrudRepository;

public interface RedisBookRepository extends CrudRepository<RedisBook, String> {

}
