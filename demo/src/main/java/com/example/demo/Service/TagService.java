package com.example.demo.Service;

import com.example.demo.Dtos.AddTagDTO;
import com.example.demo.Dtos.TagRetrivalDTO;
import com.example.demo.Entity.FileEntity;
import com.example.demo.Entity.Tag;
import com.example.demo.Repository.FileEntityRepository;
import com.example.demo.Repository.TagRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private static final Logger LOG = LoggerFactory.getLogger(TagService.class);

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    public String addTagToFile(AddTagDTO addTagDTO){


        FileEntity file = fileEntityRepository.findById(addTagDTO.getFileId())
                .orElseThrow(() -> new RuntimeException("File not found"));

        LOG.info("Adding tag '{}' to file '{}'", addTagDTO.getTagName(), file.getFileName());

        Optional<Tag> optionalTag=tagRepository.findByName(addTagDTO.getTagName());

        if(!optionalTag.isPresent()){
            Tag tag=new Tag();
            tag.setTagSummary(addTagDTO.getTagSummary());
            tag.setName(addTagDTO.getTagName());
            // Add tag to file and set bidirectional relationship
            file.addTag(tag);
        }else{
            Tag tag1=optionalTag.get();
            // Add tag to file and set bidirectional relationship
            file.addTag(tag1);
        }

        fileEntityRepository.save(file);// Save to persist changes
        LOG.info("Tag '{}' successfully added to file '{}'", addTagDTO.getTagName(), file.getFileName());

        return "The File is Tagged Successfully";
    }
    @Transactional
    public String deleteTag(Long tagId) {
        LOG.info("Received request to delete Tag with ID: {}", tagId);

        // Find the tag by ID
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (tag == null) {
            LOG.warn("Tag with ID {} not found. Deletion aborted.", tagId);
            return "Tag not found";
        }

        // Delete the tag, JPA will handle the removal of associations with FileEntity
        tagRepository.delete(tag);
        LOG.info("Tag with ID {} deleted successfully.", tagId);

        return "Tag deleted successfully";
    }


    public String removeTagFromFile(Long fileId, Long tagId) {

        FileEntity fileEntity = fileEntityRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new RuntimeException("Tag not found"));

        fileEntity.removeTag(tag);  // Remove the tag from the file (both sides of the relationship)

        fileEntityRepository.save(fileEntity);  // Save the changes
        LOG.info("Tag with tag name{} is removed successfully with file name {}.",tag.getName(),fileEntity.getFileName());
        return "The Tag is successfully removed";

    }

    public List<TagRetrivalDTO> getAllTags() {
        List<Tag> tags=tagRepository.findAll();

        List<TagRetrivalDTO> result=new ArrayList<>();

        for(Tag t:tags){
            TagRetrivalDTO tagRetrivalDTO=new TagRetrivalDTO();
            tagRetrivalDTO.setTagSummary(t.getTagSummary());
            tagRetrivalDTO.setName(t.getName());
            tagRetrivalDTO.setCreatedAt(t.getCreatedAt());
            tagRetrivalDTO.setNoOfFiles(t.getFiles().size());

            result.add(tagRetrivalDTO);
        }

        return result;
    }

    // Query to find all files that are associated with a specific tag ID
  
    
}
