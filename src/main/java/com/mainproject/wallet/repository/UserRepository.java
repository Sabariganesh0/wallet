package com.mainproject.wallet.repository;

import com.mainproject.wallet.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsernameIgnoreCase(String username); // Existing method
    User findByEmail(String email); // New method for checking email
}
