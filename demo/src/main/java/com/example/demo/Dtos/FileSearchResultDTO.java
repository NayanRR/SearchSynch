package com.example.demo.Dtos;

import com.example.demo.Entity.FileComments;
import com.example.demo.Entity.FileEntity;
import com.example.demo.Entity.Tag;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FileSearchResultDTO {

    private String fileName;
    private String fileUrl;
    private List<String> tags;
    private List<String> comments;
    private String uploadedBy;
    private LocalDateTime uploadedAt;



}
