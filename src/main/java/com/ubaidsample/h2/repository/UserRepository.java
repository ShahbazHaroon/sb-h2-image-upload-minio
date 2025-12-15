/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.repository;

import com.ubaidsample.h2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    Optional<User> findByIdempotencyKey(String key);

    List<User> findAllByAuditHistoryDTO_DeletedFalse();

    Optional<User> findByEmailAndAuditHistoryDTO_DeletedFalse(String email);

    Optional<User> findByUserNameAndAuditHistoryDTO_DeletedFalse(String userName);

    Optional<User> findByUserIdAndAuditHistoryDTO_DeletedFalse(Long userId);

    List<User> findByDateOfLeavingAndAuditHistoryDTO_DeletedFalse(LocalDate dateOfLeaving);

    List<User> findByPostalCodeAndAuditHistoryDTO_DeletedFalse(Integer postalCode);
}