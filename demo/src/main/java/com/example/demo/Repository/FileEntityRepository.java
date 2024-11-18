package com.example.demo.Repository;

import com.example.demo.Entity.Employee;
import com.example.demo.Entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FileEntityRepository extends JpaRepository<FileEntity, Long> {


    boolean existsByFileNameAndFileHash(String filename, String fileHash);

    Optional<FileEntity> findById(Long employeeId);

    List<FileEntity> findByEmployee(Employee employee);

    @Query("SELECT f FROM FileEntity f " +
            "JOIN f.tags t " +
            "JOIN f.comments c " +
            "WHERE LOWER(f.fileName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(f.summary) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(c.commentText) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(t.tagSummary) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<FileEntity> searchFilesByKeyword(@Param("searchTerm") String searchTerm);

    List<FileEntity> findByTagsId(Long tagId);


    List<FileEntity> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT f FROM FileEntity f "
            + "LEFT JOIN f.tags t "
            + "LEFT JOIN f.comments c "
            + "WHERE (LOWER(f.fileName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "OR LOWER(f.summary) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "OR LOWER(c.commentText) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "OR LOWER(t.tagSummary) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            + "AND f.createdAt BETWEEN :startDate AND :endDate "
            + "AND (:employeeId IS NULL OR f.employee.id = :employeeId) "
            + "AND (:tagNames IS NULL OR t.name IN :tagNames)")
    List<FileEntity> searchFilesWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("employeeId") Long employeeId,
            @Param("tagNames") List<String> tagNames);
}
