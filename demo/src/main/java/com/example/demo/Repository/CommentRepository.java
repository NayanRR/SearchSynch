package com.example.demo.Repository;
import com.example.demo.Entity.FileComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<FileComments, Long>{

}
