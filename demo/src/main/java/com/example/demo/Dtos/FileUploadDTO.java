package com.example.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadDTO {

    private Long   employeeId;
    private String fileName; // Additional metadata
    private String summary;
    private String subFolderPath;//Allows hierarchical paths like "teamA/projects/2024"

}
