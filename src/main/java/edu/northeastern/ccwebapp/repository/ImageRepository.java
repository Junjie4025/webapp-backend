package edu.northeastern.ccwebapp.repository;

import edu.northeastern.ccwebapp.pojo.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, String> {
	List<Image> findAll();

    Optional<Image> findById(String id);

    void deleteById(String id);
}
