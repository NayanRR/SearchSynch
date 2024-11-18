package com.example.demo.Dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTagDTO {

    private Long   fileId;
    private String tagName; // Additional metadata
    private String tagSummary;

}
