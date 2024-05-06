package ru.pet.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pet.nursery.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
}
