package com.example.demo.Dtos;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TagRetrivalDTO {


    private String name;

    private String tagSummary;

    private LocalDateTime createdAt;

    private long noOfFiles;


}
