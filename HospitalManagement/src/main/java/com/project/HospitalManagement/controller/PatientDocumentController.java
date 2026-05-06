package com.project.HospitalManagement.controller;

import com.project.HospitalManagement.Records.DocumentDto;
import com.project.HospitalManagement.entity.PatientDocument;
import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.exception.ResourceNotFoundException;
import com.project.HospitalManagement.service.PatientDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/document")
@Tag(name="Patient Document APIs")
@Slf4j
public class PatientDocumentController {

    private final PatientDocumentService patientDocumentService;

    public PatientDocumentController(PatientDocumentService patientDocumentService) {
        this.patientDocumentService = patientDocumentService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addDocument(
            @AuthenticationPrincipal User user,
            @RequestParam("document") MultipartFile document
    ){
        patientDocumentService.addDocument(user.getId(), document);
        return ResponseEntity.ok().body(Map.of("message", "Document Uploaded"));
    }

    @PostMapping("/add-multiple")
    public ResponseEntity<Map<String, String>> addMultipleDocument(
            @AuthenticationPrincipal User user,
            @RequestParam("documents") MultipartFile[] documents
    ){
        patientDocumentService.addMultipleDocuments(user.getId(), documents);
        return ResponseEntity.ok().body(Map.of("message", "Documents Uploaded"));
    }

    @GetMapping("/patient/download")
    public ResponseEntity<Resource> downloadDocument(
            @AuthenticationPrincipal User user,
            @RequestParam("documentId") Long documentId
    ) throws IOException {
        PatientDocument doc = patientDocumentService.getDocument(user.getId(), documentId);

        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Physical file not found on disk");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(Files.probeContentType(file.toPath())))
                .body(resource);
    }

    @GetMapping("/patient/view")
    public ResponseEntity<Resource> viewDocument(
            @AuthenticationPrincipal User user,
            @RequestParam("documentId") Long documentId
    ) throws IOException {
        PatientDocument doc = patientDocumentService.getDocument(user.getId(), documentId);

        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Physical file not found on disk");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(Files.probeContentType(file.toPath())))
                .body(resource);
    }

    @GetMapping("/doctor/view")
    public ResponseEntity<Resource> DoctorViewDocument(
            @RequestParam("documentId") Long documentId
    ) throws IOException {
        PatientDocument doc = patientDocumentService.viewDocument(documentId);

        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Physical file not found on disk");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(Files.probeContentType(file.toPath())))
                .body(resource);
    }

    @GetMapping("/authenticated/view")
    @Operation(summary = "View Document")
    public ResponseEntity<Resource> viewDocument(
            @RequestParam("documentId") Long documentId
    ) throws IOException {
        PatientDocument doc = patientDocumentService.viewDocument(documentId);

        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Physical file not found on disk");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(Files.probeContentType(file.toPath())))
                .body(resource);
    }

    @GetMapping("/patient")
    public ResponseEntity<List<DocumentDto.response>> getAllDocumentByUser(
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(patientDocumentService.getAllDocumentByUser(user.getId()));
    }


    @GetMapping("/public/{documentId}/thumbnail")
    public ResponseEntity<byte[]> getDocumentThumbnail(@PathVariable Long documentId) throws IOException {
        byte[] imageBytes = patientDocumentService.generateTypeThumbnail(documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }
}
