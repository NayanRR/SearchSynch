package com.example.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentRetrivalDTO {

    private String fileName;
    private String commments;
    private String commentedBy;
    private String employeeId;
    private LocalDateTime commentTiming;

}
