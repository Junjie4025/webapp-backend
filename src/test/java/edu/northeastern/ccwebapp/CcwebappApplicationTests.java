package edu.northeastern.ccwebapp;

import edu.northeastern.ccwebapp.controller.BookController;
import edu.northeastern.ccwebapp.pojo.User;
import edu.northeastern.ccwebapp.repository.BookRepository;
import edu.northeastern.ccwebapp.repository.UserRepository;
import edu.northeastern.ccwebapp.service.BookService;
import edu.northeastern.ccwebapp.service.ImageS3Service;
import edu.northeastern.ccwebapp.service.ImageService;
import edu.northeastern.ccwebapp.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = BookController.class,secure = false)
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
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	UserService userService;

	@Mock
	UserRepository userRepository;

	@MockBean
	BookService bookService;
	@MockBean
	ImageS3Service imageS3Service;
	@MockBean
	ImageService imageService;

	@MockBean
	BookRepository bookRepository;

	@Test
	public void emailChecker(){
		User user = new User();
		user.setUsername("qwert@gmail.com");
		user.setPassword("4ar@@@@@@");

		Mockito.when(userService.findByUserName(Mockito.anyString())).thenReturn(user);
		String testName = userService.findByUserName("qwert@gmail.com").getUsername();
		Assert.assertEquals("qwert@gmail.com", testName);
	}
}
