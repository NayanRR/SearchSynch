package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FileEntity {

    private static final Logger LOG = LoggerFactory.getLogger(FileEntity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url; // Link to Google Cloud Storage file
    private String fileName; // Additional metadata
    private int version = 1; // Version number, starting from 1
    private String summary;
    @Column(unique = true)
    private String fileHash;//Use a hashing algorithm (e.g., SHA-256) to generate a hash of the file content

    @CreationTimestamp
    private LocalDateTime createdAt; // Automatically set when the entity is created

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "employee_id",nullable = false)
    private Employee employee; // Employee who uploaded the file

    @OneToMany(mappedBy = "fileEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<FileComments> comments;  // List of comments for this file

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "file_tag",//Name of the join Table
            joinColumns = @JoinColumn(name = "file_id"),//Column in the join table that references fileEntity
            inverseJoinColumns = @JoinColumn(name = "tag_id")//Column in the join table that references Tag Entity
    )
    private List<Tag> tags = new ArrayList<>();

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getFiles().add(this); // Ensure the relationship is bidirectional
        LOG.info("Added tag '{}' to file '{}'", tag.getName(), this.fileName);

    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getFiles().remove(this);
    }


}
