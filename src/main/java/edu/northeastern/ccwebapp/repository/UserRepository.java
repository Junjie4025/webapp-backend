package edu.northeastern.ccwebapp.repository;

import edu.northeastern.ccwebapp.pojo.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User , Long> {
        User findByUsername(String username);
}