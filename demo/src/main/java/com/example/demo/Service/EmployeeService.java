package com.example.demo.Service;

import com.example.demo.Dtos.EmployeeDTO;
import com.example.demo.Dtos.FileRetriveResponseDTO;
import com.example.demo.Dtos.FileSearchResultDTO;
import com.example.demo.Dtos.FileUploadResponseDTO;
import com.example.demo.Entity.Employee;
import com.example.demo.Entity.FileComments;
import com.example.demo.Entity.FileEntity;
import com.example.demo.Entity.Tag;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.FileEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileEntityRepository fileEntityRepository;

    private final Logger LOG = LoggerFactory.getLogger(EmployeeService.class);

    public String createEmployee(EmployeeDTO employeeDTO) {
        // Implementation to create a new employee record using the provided EmployeeDto object
        // This implementation is currently missing
        LOG.info("Request received for create Employee");
        Employee employee = new Employee();
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setName(employeeDTO.getName());
        employee.setEmailId(employeeDTO.getEmailId());
        employee.setPhoneNo(employeeDTO.getPhoneNo());
        employeeRepository.save(employee);
        LOG.info("New Employee created: `employeeDTO.getName()`");

        return "Employee created successfully";

    }

    public List<Employee> getAllEmployees(int page, int size) {
        LOG.info("Request received for get All Employees");
        try {
            Pageable pageable = PageRequest.of(page, size);
            return employeeRepository.findAll(pageable).getContent();

        } catch (Exception e) {
            // Handle the exception or log it
            return Collections.emptyList();
        }
    }

    public Employee getEmployeeById(Long employeeId) {
        // Retrieve Employee object from database based on employeeId
        // Return the Employee object or null if not found
        LOG.info("Request received Employee with ID: `employeeId`");
        return employeeRepository.findById(employeeId).orElse(null);
    }

    public String updateEmployee(Long employeeId, EmployeeDTO employeeDTO) {
        LOG.info("Update Request received Employee with ID: `employeeId`");
        Employee employee = employeeRepository.findById(employeeId).orElse(null);

        if (employee != null) {
            employee.setName(employeeDTO.getName());
            employee.setEmailId(employeeDTO.getEmailId());
            employee.setPhoneNo(employeeDTO.getPhoneNo());
            employee.setDepartment(employeeDTO.getDepartment());
            employeeRepository.save(employee);
            return "Employee is successfully updated";
        }else{
            return "Invalid Employee ID";
        }

    }

    public String deleteEmployee(Long employeeId) {
        LOG.info("Delete Request received for Employee with ID: `employeeId`");
        if (employeeRepository.existsById(employeeId)) {
            employeeRepository.deleteById(employeeId);
            return "Employee deleted successfully";
        } else {
            return "Employee not found";
        }
    }

    public List<FileSearchResultDTO> getAllFilesByEmployee(Long employeeId) {
        LOG.info("Fetching all files uploaded by employee with ID: {}", employeeId);

        // Find the employee by ID, if not found throw an exception
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        // Fetch all files associated with the employee
        List<FileEntity> fileEntities = fileEntityRepository.findByEmployee(employee);

        List<FileSearchResultDTO> responseDTOs = new ArrayList<>();

        for(FileEntity f:fileEntities){

            List<String> tags = new ArrayList<>();
            for (Tag tag : f.getTags()) {
                tags.add(tag.getName());
            }

            List<String> comments = new ArrayList<>();
            for (FileComments comment : f.getComments()) {
                comments.add(comment.getCommentText());
            }

            FileSearchResultDTO fileSearchResultDTO=new FileSearchResultDTO();
            fileSearchResultDTO.setFileUrl(f.getUrl());
            fileSearchResultDTO.setTags(tags);
            fileSearchResultDTO.setComments(comments);
            fileSearchResultDTO.setFileName(f.getFileName());
            fileSearchResultDTO.setUploadedBy(f.getEmployee().getName());
            fileSearchResultDTO.setUploadedAt(f.getCreatedAt());

            responseDTOs.add(fileSearchResultDTO);

        }

        LOG.info("Found {} files uploaded by employee ID: {}", responseDTOs.size(), employeeId);
        return responseDTOs;

    }


}

//    public void addFile(FileEntity fileRecord, Long employeeId) {
//        LOG.info("Adding file for employee with ID: `employeeId`");
//        if (employeeRepository.existsById(employeeId)) {
//
//        } else {
//            // Handle the case where the employee does not exist
//        }
//
//
//    }




