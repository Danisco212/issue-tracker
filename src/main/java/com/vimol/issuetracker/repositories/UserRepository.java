package com.vimol.issuetracker.repositories;

import com.vimol.issuetracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.stereotype.Repository;

import javax.swing.text.Position;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String number);

    @Query("Select o from User o WHERE o.email = :email AND o.password = :password")
    Optional<User> findByLoginInfo(String email, String password);

    @Query("Select o from User o WHERE o.firstName LIKE %:name% OR o.lastName LIKE %:name%")
    List<User> findByName(String name);

    @Query("SELECT o FROM User o WHERE o.position = :position")
    List<User> findByPosition(User.UserType position);
}
