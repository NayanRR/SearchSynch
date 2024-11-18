package com.example.demo.Controller;

import com.example.demo.Repository.CommentRepository;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.FileEntityRepository;
import com.example.demo.Repository.TagRepository;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

public class AdminController {

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Operation(summary = "Summary of whole system", description = "Gives the summary of all the data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid employee details")
    })
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getSystemSummary() {
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalFiles", fileEntityRepository.count());
        summary.put("totalTags", tagRepository.count());
        summary.put("totalComments", commentRepository.count());
        summary.put("totalEmployees", employeeRepository.count());

        return ResponseEntity.ok(summary);
    }
}
