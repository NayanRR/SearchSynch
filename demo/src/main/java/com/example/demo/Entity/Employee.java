package com.example.demo.Entity;

import com.example.demo.Enums.Department;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
@Setter
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String emailId;

    private String phoneNo;

    @Enumerated(EnumType.STRING)
    private Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<FileEntity> files=new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL,orphanRemoval=true)
    private List<FileComments> comments=new ArrayList<>();




//
//    //    The addFile and removeFile helper methods manage the association from the employee’s side as well.
//    public void addFile(FileEntity file) {
//        files.add(file);
//        file.setEmployee(this);
//    }
//
//    public void removeFile(FileEntity file) {
//        files.remove(file);
//        file.setEmployee(null);
//    }

}
