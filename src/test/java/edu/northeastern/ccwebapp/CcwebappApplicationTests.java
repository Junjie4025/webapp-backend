package edu.northeastern.ccwebapp;

import edu.northeastern.ccwebapp.pojo.Book;
import edu.northeastern.ccwebapp.pojo.User;
import edu.northeastern.ccwebapp.repository.BookRepository;
import edu.northeastern.ccwebapp.repository.UserRepository;
import edu.northeastern.ccwebapp.service.BookService;
import edu.northeastern.ccwebapp.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CcwebappApplicationTests {
	//User details
	private String email = "test@gmail.com";
	//Book Details
	private String title = "Networks and cloud computing";
	private String isbn = "786-1289213";
	private String author = "Barrie Sosinsky";
	private int quantity = 10;
	private UUID uuid = UUID.randomUUID();

	private User user;

	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	@InjectMocks
	BookService bookService;

	@Mock
	BookRepository bookRepository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		createUser();
		findUser();
		createBook();
	}

	private void createUser() {
		user = new User();
		String password = "12345678";
		user.setUsername(email);
		user.setPassword(password);
		when(userRepository.save(user)).thenReturn(user);
	}

	private void findUser() {
		when(userService.findByUserName(email)).thenReturn(user);
	}

	private void createBook() {
		Book book = new Book();
		book.setQuantity(quantity);
		book.setIsbn(isbn);
		book.setAuthor(author);
		book.setTitle(title);
		book.setId(uuid.toString());
		when(bookService.getBookById(uuid.toString())).thenReturn(book);
	}

	@Test
	public void checkBookDetails() {
		Book receivedBook = bookService.getBookById(uuid.toString());
		assertEquals(receivedBook.getAuthor(), author);
		assertEquals(receivedBook.getTitle(), title);
		assertEquals(receivedBook.getIsbn(), isbn);
		assertEquals(receivedBook.getQuantity(), quantity);
	}

	@Test
	public void registerUserDetails() {
		User receivedUser = userService.findByUserName(email);
		assertEquals(receivedUser.getUsername(), email);
	}

}
