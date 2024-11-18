package com.example.demo.Controller;

import com.example.demo.Dtos.EmployeeDTO;
import com.example.demo.Dtos.FileRetriveResponseDTO;
import com.example.demo.Dtos.FileSearchResultDTO;
import com.example.demo.Entity.Employee;
import com.example.demo.Service.EmployeeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    private final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Operation(summary = "Create a new employee", description = "Add a new employee to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid employee details")
    })
    @PostMapping("/create")
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        if(employeeDTO.getName() == null || employeeDTO.getEmailId() == null || employeeDTO.getPhoneNo() == null || employeeDTO.getDepartment() == null){
            return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
        }
        try{
            String res=employeeService.createEmployee(employeeDTO);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }catch(Exception e){
            log.error("An error occurred while creating an employee: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get all employees", description = "Fetch all employees from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No employees found")
    })
    @GetMapping("/GetAllEmployee")
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(defaultValue = "0") Integer pageNo,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) {

        log.info("Received request for getAllEmployees with pageNo: " + pageNo + " and pageSize: " + pageSize);
        try {
            List<Employee> employees = employeeService.getAllEmployees(pageNo,pageSize);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("An error occurred while retrieving employees: " + e.getMessage());
            return null;
        }
    }

    @Operation(summary = "Get employee by ID", description = "Fetch a specific employee by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employee);
    }

    @Operation(summary = "Update employee Information", description = "Fetch a specific employee by their ID and update the employee as per updated information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    //Update Employee
    @PutMapping("/{employeeId}")
    public ResponseEntity<String> updateEmployee(
            @PathVariable Long employeeId,
            @RequestBody EmployeeDTO employeeDTO) {
        try{
            String res=employeeService.updateEmployee(employeeId, employeeDTO);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }catch (Exception e){
            log.error("An error occurred while creating an employee: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    // Delete an employee by ID
    @Operation(summary = "Delete employee ", description = "Delete a specific employee by their ID ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee Deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long employeeId) {
        try{
            String res=employeeService.deleteEmployee(employeeId);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }catch(Exception e){
            log.error("An error occurred while deleting an employee: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    // Retrieve all files uploaded by a specific employee
    @Operation(summary = "Get all files by employee", description = "Get all the files uploaded by a specific employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File  Fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{employeeId}/files")
    public ResponseEntity<List<FileSearchResultDTO>> getFilesByEmployee(@PathVariable Long employeeId) {
        List<FileSearchResultDTO> filesResponse = employeeService.getAllFilesByEmployee(employeeId);
        return new ResponseEntity<>(filesResponse,HttpStatus.FOUND);
    }




}
