package org.c4marathon.assignment.repository;


import org.c4marathon.assignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
