package com.example.myapp.Repository;

import com.example.myapp.Models.Weight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Integer> {

    @Query("SELECT w FROM Weight w WHERE w.username = :username AND FUNCTION('DATE', w.addedTime) = CURRENT_DATE")
    Optional<Weight> findTodayEntry(@Param("username") String username);

    Page<Weight> findByUsernameOrderByAddedTimeDesc(String username, Pageable pageable);

    List<Weight> findByUsernameAndAddedTimeBetweenOrderByAddedTimeAsc(
            String username,
            LocalDateTime start,
            LocalDateTime end
    );
}
