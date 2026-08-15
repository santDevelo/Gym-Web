package com.ProyectoFinal.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {

    private static final long VIGENCIA_URL_DIAS = 1825;
    private static final String TIPO_CONTENIDO_POR_DEFECTO = "application/octet-stream";

    private final Storage storage;
    private final String bucketName;
    private final String storagePath;

    public FirebaseStorageService(
            Storage storage,
            @Value("${firebase.bucket.name}") String bucketName,
            @Value("${firebase.storage.path}") String storagePath) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.storagePath = storagePath;
    }

    public String uploadImage(MultipartFile archivo, String carpeta, Integer id)
            throws IOException {
        String nombreArchivo = crearNombreArchivo(archivo.getOriginalFilename(), id);
        String rutaArchivo = String.join("/", storagePath, carpeta, nombreArchivo);
        String tipoContenido = archivo.getContentType() == null
                ? TIPO_CONTENIDO_POR_DEFECTO
                : archivo.getContentType();

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, rutaArchivo))
                .setContentType(tipoContenido)
                .build();

        storage.create(blobInfo, archivo.getBytes());
        return storage.signUrl(blobInfo, VIGENCIA_URL_DIAS, TimeUnit.DAYS).toString();
    }

    private String crearNombreArchivo(String nombreOriginal, Integer id) {
        String extension = obtenerExtension(nombreOriginal);
        return "img" + String.format("%014d", id) + extension;
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf('.'));
    }
}
