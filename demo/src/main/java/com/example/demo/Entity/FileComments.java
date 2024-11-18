package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
@Setter
@Builder
public class FileComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commentText;  // Comment content

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;   // Employee who added the comment

    @ManyToOne
    @JoinColumn(name = "file_id")
    @JsonIgnore
    private FileEntity fileEntity; // File on which the comment is made

    @CreationTimestamp
    private LocalDateTime createdAt; // Timestamp of when the comment was added

    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}
