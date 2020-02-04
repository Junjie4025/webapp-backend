package edu.northeastern.ccwebapp.controller;

import edu.northeastern.ccwebapp.service.ImageService;
import edu.northeastern.ccwebapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ImageController {
	private UserService userService;
	private ImageService imageService;
	
	public ImageController(UserService userService, ImageService imageService) {
		this.userService = userService;
		this.imageService = imageService;
	}
	
	@PostMapping(value = "/book/{idBook}/image", produces = "application/json")
	public ResponseEntity<?> addImageToBook(@PathVariable String idBook, @RequestParam("file") MultipartFile file,
			HttpServletRequest request){
		ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) return imageService.createCoverPage(idBook, file);
		return responseEntity;
	}
	
	@GetMapping(value="/book/{idBook}/image/{idImage}", produces="application/json")
	public ResponseEntity<?> fetchBookImageDetails(@PathVariable String idBook, @PathVariable String idImage, HttpServletRequest request) {
		ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
		if(responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return imageService.getCoverPage(idBook, idImage);
		}
		return responseEntity;
	}

    @PutMapping(value = "/book/{idBook}/image/{idImage}", produces = "application/json")
    public ResponseEntity<?> modifyBookImageDetails(@PathVariable String idBook, @PathVariable String idImage
            , @RequestParam("file") MultipartFile file, HttpServletRequest request) {
		ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK))
            return imageService.updateCoverPage(idBook, idImage, file);
		return responseEntity;
	}

    @DeleteMapping(value = "/book/{idBook}/image/{idImage}")
	public ResponseEntity<?> removeBookImageDetails(@PathVariable String idBook, @PathVariable String idImage, HttpServletRequest request) {
		ResponseEntity<?> responseEntity = userService.resultOfUserStatus(request);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) return imageService.deleteCoverPage(idBook, idImage);
        return responseEntity;
	}
}
