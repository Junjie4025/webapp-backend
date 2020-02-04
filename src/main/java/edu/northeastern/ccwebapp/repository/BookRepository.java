package edu.northeastern.ccwebapp.repository;

import edu.northeastern.ccwebapp.pojo.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookRepository extends CrudRepository<Book , Long>{
    Book findById(String id);
    List<Book> findAll();
    @Transactional
    void deleteById(String id);
}