package edu.northeastern.ccwebapp.service;

import edu.northeastern.ccwebapp.Util.ResponseMessage;
import edu.northeastern.ccwebapp.Util.S3GeneratePreSignedURL;
import edu.northeastern.ccwebapp.pojo.Book;
import edu.northeastern.ccwebapp.pojo.Image;
import edu.northeastern.ccwebapp.repository.BookRepository;
import edu.northeastern.ccwebapp.repository.ImageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.*;

@Service
public class ImageS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private BookService bookService;
    private ImageService imageService;
    private S3ServiceImpl s3ServiceImpl;
    private ImageRepository imageRepository;
    private BookRepository bookRepository;

    private final static Logger logger = LogManager.getLogger(ImageS3Service.class);
    public ImageS3Service(ImageRepository imageRepository, BookService bookService,
                          ImageService imageService, S3ServiceImpl s3ServiceImpl, BookRepository bookRepository) {
        this.imageRepository = imageRepository;
        this.bookService = bookService;
        this.s3ServiceImpl = s3ServiceImpl;
        this.imageService = imageService;
        this.bookRepository = bookRepository;
    }

    public ResponseEntity<?> createCoverPage(String bookId, MultipartFile file) {
        ResponseMessage responseMessage = new ResponseMessage();
        Book book = bookService.getBookById(bookId);
        if (book != null) {
            if (book.getImage() == null && imageService.checkContentType(file)) {
                Image uploadedImage;
                try {
                    uploadedImage = saveFileInS3Bucket(file, book);
                    if (uploadedImage != null) {
                        logger.info("Image saved in s3");
                        return new ResponseEntity<>(uploadedImage, HttpStatus.OK);
                    }
                    else {
                        responseMessage.setMessage("Image failed to upload in S3");
                        logger.info("Image failed to upload in S3");
                        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
                    }
                } catch (IOException e) {
                    logger.error("Exception :",e);
                    e.printStackTrace();
                    return new ResponseEntity<>("Image not found", HttpStatus.BAD_REQUEST);
                }
            } else {
                responseMessage.setMessage("Coverpage already added for book or Image format not supported");
                logger.info("Coverpage already added for book or Image format not supported");
                return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
            }
        }
        responseMessage.setMessage("Book with id " + bookId + " not found");
        logger.info("Book with id " + bookId + " not found");
        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
    }

    private Image saveFileInS3Bucket(MultipartFile file, Book book) throws IOException {
        String key = Instant.now().getEpochSecond() + "_" + file.getOriginalFilename();
        String imagePath = "s3://" + bucketName + "/";
        logger.info("Bucket name="+bucketName);
        String pathURL = imagePath + URLEncoder.encode(key, "UTF-8");
        if (s3ServiceImpl.uploadFile(key, file, bucketName)) {
            Image image = new Image();
            UUID id = UUID.randomUUID();
            image.setId(id.toString());
            image.setUrl(pathURL);
            imageService.updateBookByAddingGivenImage(image, book);
            return imageRepository.save(image);
        } else return null;

    }

    public ResponseEntity<?> updateCoverPage(String bookId, String imageId, MultipartFile file) {
        ResponseMessage responseMessage = new ResponseMessage();
        Book currentBook = bookService.getBookById(bookId);
        Optional<Image> currentImage = imageRepository.findById(imageId);
        if (currentBook != null) {
            if (currentImage.isPresent()) {
                if (currentBook.getImage().getId().equals(imageId)) {
                    if (imageService.checkContentType(file)) {
                        String path = currentImage.get().getUrl();
                        String[] fileUrlArray = path.split("/");
                        String keyName = fileUrlArray[fileUrlArray.length - 1];
                        String bucketName = fileUrlArray[fileUrlArray.length - 2];
                        s3ServiceImpl.deleteFile(keyName, bucketName);
                        try {
                            saveFileInS3Bucket(file, currentBook);
                        } catch (IOException e) {
                            e.printStackTrace();
                            logger.error("Exception :",e);
                            return new ResponseEntity<>("Image not found", HttpStatus.BAD_REQUEST);
                        }
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } else {
                    	logger.info("Only .jpg,.png,.jpeg formats are supported");
                        responseMessage.setMessage("Only .jpg,.png,.jpeg formats are supported");
                        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    logger.info("Image with id " + imageId + " not found in mentioned book.");
                    responseMessage.setMessage("Image with id " + imageId + " not found in mentioned book.");
                }
            } else {
                logger.info("Image with id " + imageId + " not found");
                responseMessage.setMessage("Image with id " + imageId + " not found");
            }
        } else {
            logger.info("Book with id " + bookId + " not found");
            responseMessage.setMessage("Book with id " + bookId + " not found");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
    }


    public ResponseEntity<?> getCoverPage(String bookId, String imageId)  {
        ResponseMessage responseMessage = new ResponseMessage();
        Book book = bookService.getBookById(bookId);
        if (book != null) {
            if (book.getImage() != null) {
                if (book.getImage().getId().equals(imageId)) {
                    S3GeneratePreSignedURL s3Url = new S3GeneratePreSignedURL();
                    Optional<Image> mp = imageRepository.findById(imageId);
                    String[] fileUrlArray = mp.get().getUrl().split("/");
                    String keyName = fileUrlArray[fileUrlArray.length - 1];
                    String bucketName = fileUrlArray[fileUrlArray.length - 2];
                    String img_url = s3Url.getPreSignedURL(keyName, bucketName);
                    Map<String, String> urlMap = new HashMap<>();
                    urlMap.put("url", img_url);
                    urlMap.put("id", mp.get().getId());
                    return new ResponseEntity<>(urlMap, HttpStatus.OK);

                } else {
                	logger.info("Image with mentioned id does not match with book's image id..");
                    responseMessage.setMessage("Image with mentioned id does not match with book's image id..");
                }
            } else {
            	logger.info("Image with mentioned id does not exists.");
                responseMessage.setMessage("Image with mentioned id does not exists.");
            }
        } else {
        	logger.info("Book with mentioned id does not exists.");
            responseMessage.setMessage("Book with mentioned id does not exists.");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> deleteCoverPage(String bookId, String imageId) {
        ResponseMessage responseMessage = new ResponseMessage();
        Book currentBook = bookService.getBookById(bookId);
        Optional<Image> currentImage = imageRepository.findById(imageId);
        if (currentBook != null) {
            if (currentImage.isPresent()) {
                if (currentBook.getImage().getId().equals(imageId)) {
                    String path = currentImage.get().getUrl();
                    String[] fileUrlArray = path.split("/");
                    String keyName = fileUrlArray[fileUrlArray.length - 1];
                    String bucketName = fileUrlArray[fileUrlArray.length - 2];
                    s3ServiceImpl.deleteFile(keyName, bucketName);
                    imageService.updateBookByAddingGivenImage(null, currentBook);
                    imageRepository.deleteById(imageId);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                	logger.info("Image with id " + imageId + " not found in mentioned book.");
                    responseMessage.setMessage("Image with id " + imageId + " not found in mentioned book.");
                }
            } else {
            	logger.info("Image with id " + imageId + " not found");
                responseMessage.setMessage("Image with id " + imageId + " not found");
            }
        } else {
        	logger.info("Book with id " + bookId + " not found");
            responseMessage.setMessage("Book with id " + bookId + " not found");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity getAllBooks() {
        S3GeneratePreSignedURL preSignedURL = new S3GeneratePreSignedURL();
        List<Book> bookDetails = bookRepository.findAll();
        for (Book b : bookDetails) {
            if (b.getImage() != null) {
                generatePresignedUrl(preSignedURL, b);
            }
        }
        logger.info("Got all Books - OK");
        return new ResponseEntity(bookDetails, HttpStatus.OK);
    }

    public ResponseEntity getBookById(String bookId) {
        ResponseMessage responseMessage = new ResponseMessage();
        S3GeneratePreSignedURL preSignedURL = new S3GeneratePreSignedURL();
        Book book = bookRepository.findById(bookId);
        if (book == null) {
        	logger.info("Book with id " + bookId + " not found");
            responseMessage.setMessage("Book with id " + bookId + " not found");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
        if (book.getImage() != null) {
            generatePresignedUrl(preSignedURL, book);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    private void generatePresignedUrl(S3GeneratePreSignedURL preSignedURL, Book book) {
        String url = book.getImage().getUrl();
        String[] fileUrlArray = url.split("/");
        String keyName = fileUrlArray[fileUrlArray.length - 1];
        String bucketName = fileUrlArray[fileUrlArray.length - 2];
        book.getImage().setUrl(preSignedURL.getPreSignedURL(keyName, bucketName));
    }

}