package edu.northeastern.ccwebapp.controller;

import edu.northeastern.ccwebapp.pojo.Book;
import edu.northeastern.ccwebapp.service.BookService;
import edu.northeastern.ccwebapp.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Profile("local")
@RestController
public class BookController {

    private final BookService bookService;
    private final UserService userService;

    private static final Counter getBooksCounter = Metrics.counter("get_books_api_counter");
    private static final Counter postBookCounter = Metrics.counter("post_book_api_counter");
    private static final Counter getBookWithIdCounter = Metrics.counter("get_book_with_id_api_counter");
    private static final Counter putBookCounter = Metrics.counter("put_book_api_counter");
    private static final Counter deleteBookCounter = Metrics.counter("delete_book_api_counter");

    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @PostMapping(value = "/book", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createBook(@RequestBody Book book, HttpServletRequest request) {
        postBookCounter.increment();
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        HttpStatus status = responseEntity.getStatusCode();
        if (status.equals(HttpStatus.OK)) return bookService.addBookDetails(book);
        else return responseEntity;
    }

    @GetMapping(value = "/book", produces = "application/json")
    public ResponseEntity<?> returnBookDetails(HttpServletRequest request) {
        getBooksCounter.increment();
        return bookService.getBooks();
    }

    @GetMapping(value = "/book/{id}", produces = "application/json")
    public ResponseEntity<?> getBookById(@PathVariable String id, HttpServletRequest request) {
        getBookWithIdCounter.increment();
        return bookService.getBook(id);
    }

    @PutMapping(value = "/book", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> UpdateBook(@RequestBody Book book, HttpServletRequest request) {
        putBookCounter.increment();
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        HttpStatus status = responseEntity.getStatusCode();
        if (status.equals(HttpStatus.OK)) return bookService.updateBook(book);
        else return responseEntity;
    }

    @DeleteMapping(value = "/book/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable("id") String id, HttpServletRequest request) {
        deleteBookCounter.increment();
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        HttpStatus status = responseEntity.getStatusCode();
        if (status.equals(HttpStatus.OK)) return bookService.deleteBook(id);
        else return responseEntity;
    }


}
