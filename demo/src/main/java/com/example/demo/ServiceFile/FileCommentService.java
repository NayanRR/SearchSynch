package com.example.demo.ServiceFile;

import com.example.demo.Dtos.AddCommentDTO;
import com.example.demo.Dtos.CommentRetrivalDTO;
import com.example.demo.Entity.Employee;
import com.example.demo.Entity.FileComments;
import com.example.demo.Entity.FileEntity;
import com.example.demo.Repository.CommentRepository;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.FileEntityRepository;
import com.example.demo.Service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileCommentService {

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final Logger LOG = LoggerFactory.getLogger(EmployeeService.class);

    public String addCommentToFile(Long fileId, AddCommentDTO addCommentDTO) {
        // Retrieve the file entity by ID
        FileEntity fileEntity = fileEntityRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found with ID: " + fileId));

        // Retrieve the employee who is adding the comment
        Employee employee = employeeRepository.findById(addCommentDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + addCommentDTO.getEmployeeId()));

        // Create a new comment
        FileComments comment = new FileComments();
        comment.setCommentText(addCommentDTO.getComments());
        //Bidirectional Relationship between employee and comments
        comment.setEmployee(employee);
        employee.getComments().add(comment);

        //Bidirectional Relationship between File Entity and comments
        comment.setFileEntity(fileEntity);
        fileEntity.getComments().add(comment);

        commentRepository.save(comment);
        LOG.info("Comment added to file {} by employee {}: {}", fileId, employee.getName(),addCommentDTO.getComments());

        return "Comment Added to file Successfully";

    }

    public List<CommentRetrivalDTO> getCommentsOnFile(Long fileId) {
        LOG.info("Request Received for getting all the comments done on file with fileID: "+fileId);
        FileEntity fileEntity = fileEntityRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found with ID: " + fileId));

        List<FileComments> fileComments=fileEntity.getComments();
        List<CommentRetrivalDTO> commentRetrivalDTOS=new ArrayList<>();
        for(FileComments f:fileComments){

            CommentRetrivalDTO commentRetrivalDTO=new CommentRetrivalDTO();
            commentRetrivalDTO.setFileName(fileEntity.getFileName());
            commentRetrivalDTO.setCommentedBy(f.getEmployee().getName());
            commentRetrivalDTO.setEmployeeId(f.getEmployee().getEmailId());
            commentRetrivalDTO.setCommments(f.getCommentText());
            commentRetrivalDTO.setCommentTiming(f.getCreatedAt());
            commentRetrivalDTOS.add(commentRetrivalDTO);

        }
        return commentRetrivalDTOS; // Returns the list of comments associated with the file
    }

    public List<CommentRetrivalDTO> getCommentsByEmployee(Long employeeId) {
        LOG.info("Request Received for getting all the comments done by employee with employeeID: "+employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));

        List<FileComments> fileComments=employee.getComments();
        List<CommentRetrivalDTO> commentRetrivalDTOS=new ArrayList<>();

        for(FileComments f:fileComments){

            CommentRetrivalDTO commentRetrivalDTO=new CommentRetrivalDTO();
            commentRetrivalDTO.setFileName(f.getFileEntity().getFileName());
            commentRetrivalDTO.setCommentedBy(f.getEmployee().getName());
            commentRetrivalDTO.setEmployeeId(f.getEmployee().getEmailId());
            commentRetrivalDTO.setCommments(f.getCommentText());
            commentRetrivalDTO.setCommentTiming(f.getCreatedAt());
            commentRetrivalDTOS.add(commentRetrivalDTO);

        }
        return commentRetrivalDTOS;

    }

    @Transactional
    public String deleteComment(Long commentId) {
        LOG.info("Request Received for deleting the comment");
        FileComments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        // Delete the comment from the database
        commentRepository.delete(comment);
        LOG.info("Deleting Comment Successfully");
        return "Comment deleted successfully";
    }


    public void updateCommentText(Long commentId, AddCommentDTO commentDTO) {

        Optional<FileComments> commentOpt = commentRepository.findById(commentId);

        if (!commentOpt.isPresent()) {
            throw new EntityNotFoundException("Comment with ID " + commentId + " not found.");
        }

        FileComments comment = commentOpt.get();
        comment.setCommentText(commentDTO.getComments());
        comment.setLastUpdated(LocalDateTime.now());

        commentRepository.save(comment);

    }
}
