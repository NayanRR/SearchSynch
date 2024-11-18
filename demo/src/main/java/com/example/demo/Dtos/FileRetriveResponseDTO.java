package com.example.demo.Dtos;

import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FileRetriveResponseDTO {

    private String url;
    private String fileName;
    private String Response;
    private String uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private List<String> comments;

}
