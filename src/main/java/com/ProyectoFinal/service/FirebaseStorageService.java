package com.ProyectoFinal.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {

    @Value("${firebase.bucket.name}")
    private String bucketName;

    // Carpeta base dentro del bucket
   @Value("${firebase.storage.path}")
    private String storagePath;

    private final Storage storage;

    // @Lazy: la clave de servicio real de Firebase se removio del repositorio (estaba expuesta
    // publicamente en git); el bean solo se construye si de verdad se usa la subida de fotos.
    public FirebaseStorageService(@Lazy Storage storage) {
        this.storage = storage;
    }

    // 📌 Subir imagen a Firebase
    public String uploadImage(MultipartFile localFile, String folder, Integer id) throws IOException {
        String originalName = localFile.getOriginalFilename();
        String fileExtension = "";
        if (originalName != null && originalName.contains(".")) {
            fileExtension = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = "img" + getFormattedNumber(id) + fileExtension;
        File tempFile = convertToFile(localFile);

        try {
            return uploadToFirebase(tempFile, folder, fileName);
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String uploadToFirebase(File file, String folder, String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, storagePath + "/" + folder + "/" + fileName);
        String mimeType = Files.probeContentType(file.toPath());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(mimeType != null ? mimeType : "media")
                .build();

        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        // URL firmada con caducidad de 5 años
        return storage.signUrl(blobInfo, 1825, TimeUnit.DAYS).toString();
    }

    private String getFormattedNumber(long id) {
        return String.format("%014d", id);
    }
}
