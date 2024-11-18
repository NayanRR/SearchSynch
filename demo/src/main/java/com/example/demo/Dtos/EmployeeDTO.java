package com.example.demo.Dtos;

import com.example.demo.Enums.Department;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDTO {

    private String name;

    private String emailId;

    private String phoneNo;

    @Enumerated(EnumType.STRING)
    private Department department;
}
