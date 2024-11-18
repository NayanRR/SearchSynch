package com.example.demo.Controller;

import com.example.demo.Dtos.AddTagDTO;
import com.example.demo.Dtos.TagRetrivalDTO;
import com.example.demo.Service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class TagController {

    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);
    @Autowired
    private TagService fileTagService;

    @Operation(summary = "Add a tag to a file", description = "Associate a tag with a specific file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid tag details")
    })
    @PostMapping("/addTag")
    public ResponseEntity<String> addTagToFile(@RequestBody AddTagDTO addTagDTO) {
        LOG.info("Request received to add tag with ID '{}' to file with ID '{}'", addTagDTO.getTagName(), addTagDTO.getFileId());
        fileTagService.addTagToFile(addTagDTO);
        return ResponseEntity.ok("Tag added to file successfully");
    }

    @Operation(summary = "Delete a tag ", description = "Remove a tag by its ID from a particular file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Tag not found")
    })

    @DeleteMapping("/{fileId}/tags/{tagId}")
    public ResponseEntity<String> removeTagFromFile(@PathVariable Long fileId, @PathVariable Long tagId) {
        String response=fileTagService.removeTagFromFile(fileId, tagId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all tags", description = "Fetch all tags associated with files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tags fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No tags found")
    })
    @GetMapping("/all")
    public ResponseEntity<List<TagRetrivalDTO>> getAllTags() {
        // Your logic here
        List<TagRetrivalDTO> tags=fileTagService.getAllTags();
        return ResponseEntity.ok(List.of());
    }


}
