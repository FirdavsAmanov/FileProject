package com.example.fileproject.controller;

import com.example.fileproject.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/attachment")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Autowired
    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }


    @PostMapping("/upload") // this method adds new file in DB
    private String uploadFile(MultipartHttpServletRequest multipartFile) {
        return attachmentService.add(multipartFile);
    }

    @GetMapping("/download/{id}")
    private String downloadFile(@PathVariable Integer id, HttpServletResponse response) {
        return attachmentService.getFile(id, response);
    }
}
