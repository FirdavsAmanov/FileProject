package com.example.fileproject.service;

import com.example.fileproject.entity.Attachment;
import com.example.fileproject.entity.AttachmentContent;
import com.example.fileproject.repository.AttachmentRepository;
import com.example.fileproject.repository.AttachmentContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
    }


    public String add(MultipartHttpServletRequest multipartFile) {

        Iterator<String> fileNames = multipartFile.getFileNames();
        MultipartFile file = multipartFile.getFile(fileNames.next());
        if (file == null) return "File name is emty";

        String originalFilename = file.getOriginalFilename();
        long size = file.getSize();
        String contentType = multipartFile.getContentType();
        Attachment attachment = new Attachment();
        attachment.setFileOriginName(originalFilename);
        attachment.setSize(size);
        attachment.setContent_type(contentType);
        Attachment savedAttachment = attachmentRepository.save(attachment);


        try {
            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setContent(file.getBytes());
            attachmentContent.setAttachment(savedAttachment);
            attachmentContentRepository.save(attachmentContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "Successfully saved";
    }

    public String getFile(Integer id, HttpServletResponse response) {

        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isEmpty()) return "Not founded";
        Attachment attachment = optionalAttachment.get();

        Optional<AttachmentContent> optional = attachmentContentRepository.findByAttachmentId(id);
        if (optional.isEmpty()) return "Not founded";

        AttachmentContent attachmentContent = optional.get();
        byte[] content = attachmentContent.getContent();

        response.setHeader("Content-Disposition", "attachment; fileName=" +
                attachment.getFileOriginName());

        response.setContentType(attachment.getContent_type());

        try {
            FileCopyUtils.copy(content, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Downloaded";
    }
}
