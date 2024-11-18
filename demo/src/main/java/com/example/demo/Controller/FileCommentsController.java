package com.example.demo.Controller;

import com.example.demo.Dtos.AddCommentDTO;
import com.example.demo.Dtos.CommentRetrivalDTO;
import com.example.demo.Entity.FileComments;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.example.demo.ServiceFile.FileCommentService;
import com.example.demo.ServiceFile.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileCommentsController {

        @Autowired
        private FileStorageService fileStorageService;

        @Autowired
        private FileCommentService fileCommentService;

        @Operation(summary = "Add a comment", description = "Add a comment to a specific file")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Comment added successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid comment details")
        })
        @PostMapping("/{fileId}/comment")
        public ResponseEntity<String> addComment(@PathVariable Long fileId,
                                                 @RequestBody AddCommentDTO addCommentDTO) {
            try {
                // Add the comment to the file
                String Response=fileCommentService.addCommentToFile(fileId,addCommentDTO);
                return new ResponseEntity<>(Response, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Failed to add comment: " + e.getMessage());
            }
        }

    @Operation(summary = "Get comments for a file", description = "Retrieve all comments for a specific file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No comments found for this file")
    })
    @GetMapping("/file/{fileId}")
    public ResponseEntity<List<CommentRetrivalDTO>> getCommentsOnFile(@PathVariable Long fileId) {
        try {
            List<CommentRetrivalDTO> comments = fileCommentService.getCommentsOnFile(fileId);
            if (comments.isEmpty()) {
                return ResponseEntity.noContent().build(); // No comments found
            }
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null); // File not found
        }
    }
    @Operation(summary = "Get comments by Employee", description = "Retrieve all comments by a specific employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No comments found for this file")
    })

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<CommentRetrivalDTO>> getCommentsByEmployee(@PathVariable Long employeeId) {
        try {
            List<CommentRetrivalDTO> comments = fileCommentService.getCommentsByEmployee(employeeId);
            if (comments.isEmpty()) {
                return ResponseEntity.noContent().build(); // No comments found
            }
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null); // Employee not found
        }
    }

    @Operation(summary = "Update Comment", description = "Update Comment by a specific ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No comments found for this file")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestBody AddCommentDTO commentDTO) {
        fileCommentService.updateCommentText(commentId, commentDTO);
        return ResponseEntity.ok("Comment updated successfully.");
    }

    @Operation(summary = "Delete Comment", description = "Delete Comment by a specific ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No comments found ")
    })

        @DeleteMapping("/{commentId}")
        public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
                String responseMessage = fileCommentService.deleteComment(commentId);
                return ResponseEntity.ok(responseMessage);
            }



    }

