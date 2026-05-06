package com.project.HospitalManagement.service;

import com.project.HospitalManagement.Records.DocumentDto;
import com.project.HospitalManagement.entity.Patient;
import com.project.HospitalManagement.entity.PatientDocument;
import com.project.HospitalManagement.exception.ResourceNotFoundException;
import com.project.HospitalManagement.repository.PatientDocumentRepository;
import com.project.HospitalManagement.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PatientDocumentService {

    @Value("${storage.path}")
    private String storagePath;

    private final PatientDocumentRepository patientDocumentRepository;
    private final PatientRepository patientRepository;

    public PatientDocumentService(PatientDocumentRepository patientDocumentRepository, PatientRepository patientRepository) {
        this.patientDocumentRepository = patientDocumentRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public void addDocument(Long userId, MultipartFile document) {
        Patient patient = patientRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("Patient not found for user id: {}", userId);
            return new ResourceNotFoundException("Patient not found");
        });
        UploadDocument(userId,patient,document);
    }

    @Transactional
    public void addMultipleDocuments(Long userId, MultipartFile[] documents){
        if(documents == null){
            throw new RuntimeException("No document Uploaded");
        }

        Patient patient = patientRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("Patient not found for user id: {}", userId);
            return new ResourceNotFoundException("Patient not found");
        });

        for(MultipartFile document: documents){
            UploadDocument(userId,patient,document);
        }
    }

    public PatientDocument getDocument(Long userId, Long documentId){
        return patientDocumentRepository.findByIdAndUserId(userId,documentId).orElseThrow(()->{
            log.warn("Document data not found with user id: {} and document id: {}",userId,documentId);
            return new ResourceNotFoundException("Document data not found");
        });
    }

    public PatientDocument viewDocument(Long documentId){
        return patientDocumentRepository.findById(documentId).orElseThrow(()->{
            log.warn("Document data not found with id: {}",documentId);
            return new ResourceNotFoundException("Document data not found");
        });
    }

    public List<DocumentDto.response> getAllDocumentByUser(Long userId) {
        return patientDocumentRepository.findByUserId(userId);
    }

    public PatientDocument getDocument(Long documentId){
        return patientDocumentRepository.findById(documentId).orElseThrow(()->{
            log.warn("Document not found");
            return new ResourceNotFoundException("Document not found");
        });
    }

    public PatientDocument UploadDocument(Long userId, Patient patient, MultipartFile document){
        Path userDirectory = Paths.get(storagePath, userId.toString());
        try {
            if (!Files.exists(userDirectory)) {
                Files.createDirectories(userDirectory);
            }

            String originalFileName = document.getOriginalFilename();
            String extension = (originalFileName != null && originalFileName.contains("."))
                    ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : "";

            String storedName = UUID.randomUUID().toString() + extension;
            Path targetLocation = userDirectory.resolve(storedName);

            Files.copy(document.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            PatientDocument doc = new PatientDocument();
            doc.setPatient(patient);
            doc.setFileName(originalFileName);
            doc.setStoredName(storedName);
            doc.setFilePath(targetLocation.toString());

            return patientDocumentRepository.save(doc);

        } catch (IOException e) {
            log.error("File system error for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public byte[] generateTypeThumbnail(Long documentId) throws IOException {
        PatientDocument doc = this.getDocument(documentId);

        File file = new File(doc.getFilePath());

        try (PDDocument document = PDDocument.load(file)){
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 72);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            return baos.toByteArray();
        } catch (Exception e){
            throw new IOException("Could not generate thumbnail", e);
        }
    }
}
