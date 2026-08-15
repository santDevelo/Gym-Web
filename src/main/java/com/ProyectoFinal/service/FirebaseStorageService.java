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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// Capa de servicio para subir imágenes a Firebase Storage (foto de perfil
// de usuario). El bean Storage se configura en StorageConfig con las
// credenciales del archivo JSON de la cuenta de servicio.
@Service
public class FirebaseStorageService {

    @Value("${firebase.bucket.name}")
    private String bucketName;

   @Value("${firebase.storage.path}")
    private String storagePath;

    private final Storage storage;

    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }


    // Sube el archivo recibido del formulario (MultipartFile) y devuelve la
    // URL firmada para guardarla en la entidad (ej. usuario.rutaImagen)
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
            // Borra el archivo temporal aunque falle la subida, para no dejar basura en disco
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    // MultipartFile vive en memoria/temporal de Spring; se copia a un File
    // real porque la librería de Firebase necesita un java.io.File
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


        // URL firmada con vencimiento largo (1825 días = 5 años) en vez de
        // hacer el bucket público
        return storage.signUrl(blobInfo, 1825, TimeUnit.DAYS).toString();
    }

    // Rellena con ceros a la izquierda (id 8 -> "00000000000008") para tener
    // nombres de archivo ordenables y sin colisiones
    private String getFormattedNumber(long id) {
        return String.format("%014d", id);
    }
}
