package edu.northeastern.ccwebapp.controller;

import com.timgroup.statsd.StatsDClient;
import edu.northeastern.ccwebapp.pojo.Book;
import edu.northeastern.ccwebapp.service.BookService;
import edu.northeastern.ccwebapp.service.ImageS3Service;
import edu.northeastern.ccwebapp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ImageS3Controller {

    private UserService userService;
    private ImageS3Service imageS3Service;
    private BookService bookService;
    private StatsDClient statsDClient;
    private final static Logger logger = LogManager.getLogger(ImageS3Controller.class);
    public ImageS3Controller(UserService userService, ImageS3Service imageS3Service, BookService bookService, StatsDClient statsDClient) {
        this.userService = userService;
        this.imageS3Service = imageS3Service;
        this.bookService = bookService;
        this.statsDClient = statsDClient;
    }

    @PostMapping(value = "/book/{idBook}/image", produces = "application/json")
    public ResponseEntity<?> addImageToBook(@PathVariable String idBook, @RequestParam("file") MultipartFile file,
                                            HttpServletRequest request) {
        statsDClient.incrementCounter("book.image.post");
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return imageS3Service.createCoverPage(idBook, file);
        } else {
            logger.info("User not authenticated");
            return responseEntity;
        }

    }

    @GetMapping(value = "/book/{idBook}/image/{idImage}", produces = "application/json")
    public ResponseEntity<?> fetchBookImageDetails(@PathVariable String idBook, @PathVariable String idImage,
                                                   HttpServletRequest request) throws Exception {
        statsDClient.incrementCounter("book.image.get");
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return imageS3Service.getCoverPage(idBook, idImage);
        } else {
            logger.info("User not authenticated");
            return responseEntity;
        }
    }

    @GetMapping(value = "/book", produces = "application/json")
    public ResponseEntity<?> getBooks(HttpServletRequest request)  {
        statsDClient.incrementCounter("book.list");
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return imageS3Service.getAllBooks();
        } else {
            logger.info("User not authenticated");
            return responseEntity;
        }
    }

    @GetMapping(value = "/book/{idBook}", produces = "application/json")
    public ResponseEntity<?> getBookById(@PathVariable String idBook, HttpServletRequest request) {
        statsDClient.incrementCounter("book.get");
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return imageS3Service.getBookById(idBook);
        } else {
            logger.info("User not authenticated");
        }
        return responseEntity;
    }


    @PutMapping(value = "/book/{idBook}/image/{idImage}", produces = "application/json")
    public ResponseEntity<?> modifyBookImageDetails(@PathVariable String idBook, @PathVariable String idImage,
                                                    @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        statsDClient.incrementCounter("book.image.update");
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return imageS3Service.updateCoverPage(idBook, idImage, file);
        } else {
            logger.info("User not authenticated");
            return responseEntity;
        }
    }


    @DeleteMapping(value = "/book/{idBook}/image/{idImage}")
    public ResponseEntity<?> removeBookImageDetails(@PathVariable String idBook, @PathVariable String idImage,
                                                    HttpServletRequest request) {
        statsDClient.incrementCounter("book.image.delete");
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return imageS3Service.deleteCoverPage(idBook, idImage);
        } else {
            logger.info("User not authenticated");
            return responseEntity;
        }
    }

    @PutMapping(value = "/book", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> UpdateBook(@RequestBody Book book, HttpServletRequest request) {
        statsDClient.incrementCounter("book.update");
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        HttpStatus status = responseEntity.getStatusCode();
        if (status.equals(HttpStatus.OK)) return bookService.updateBook(book);
        else {
            logger.info("User not authenticated");
            return responseEntity;
        }
    }

    @DeleteMapping(value = "/book/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable("id") String id, HttpServletRequest request) {
        statsDClient.incrementCounter("book.delete");
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        HttpStatus status = responseEntity.getStatusCode();
        if (status.equals(HttpStatus.OK)) return bookService.deleteBook(id);
        else {
            logger.info("User not authenticated");
            return responseEntity;
        }
    }

    @PostMapping(value = "/book", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createBook(@RequestBody Book book, HttpServletRequest request) {
        ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        statsDClient.incrementCounter("book.delete");
        HttpStatus status = responseEntity.getStatusCode();
        if (status.equals(HttpStatus.OK)) return bookService.addBookDetails(book);
        else {
            logger.info("User not authenticated");
            return responseEntity;
        }
    }
}