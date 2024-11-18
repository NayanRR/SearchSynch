package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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
public class Tag {


    private static final Logger LOG = LoggerFactory.getLogger(Tag.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String tagSummary;

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<FileEntity> files = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt; // Timestamp of when the comment was added

    // Helper method to add a file and set bidirectional relationship
    public void addFile(FileEntity file) {
        files.add(file);
        file.getTags().add(this); // Ensure the relationship is bidirectional
        LOG.info("Added file '{}' to tag '{}'", file.getFileName(), this.name);
    }
    // Utility method to remove file
    public void removeFile(FileEntity file) {
        files.remove(file);
        file.getTags().remove(this);
    }


}
