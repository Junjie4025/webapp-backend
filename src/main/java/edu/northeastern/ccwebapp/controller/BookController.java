package edu.northeastern.ccwebapp.controller;

import edu.northeastern.ccwebapp.pojo.Book;
import edu.northeastern.ccwebapp.service.BookService;
import edu.northeastern.ccwebapp.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Profile("local")
@RestController
public class BookController {

    private final BookService bookService;
    private final UserService userService;

    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @PostMapping(value = "/book", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createBook(@RequestBody Book book, HttpServletRequest request) {
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        HttpStatus status = responseEntity.getStatusCode();
        if (status.equals(HttpStatus.OK)) return bookService.addBookDetails(book);
        else return responseEntity;
    }

    @GetMapping(value = "/book", produces = "application/json")
    public ResponseEntity<?> returnBookDetails(HttpServletRequest request) {
        return bookService.getBooks();
    }

    @GetMapping(value = "/book/{id}", produces = "application/json")
    public ResponseEntity<?> getBookById(@PathVariable String id, HttpServletRequest request) {
        return bookService.getBook(id);
    }

    @PutMapping(value = "/book", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> UpdateBook(@RequestBody Book book, HttpServletRequest request) {
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        HttpStatus status = responseEntity.getStatusCode();
        if (status.equals(HttpStatus.OK)) return bookService.updateBook(book);
        else return responseEntity;
    }

    @DeleteMapping(value = "/book/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable("id") String id, HttpServletRequest request) {
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        HttpStatus status = responseEntity.getStatusCode();
        if (status.equals(HttpStatus.OK)) return bookService.deleteBook(id);
        else return responseEntity;
    }
}
