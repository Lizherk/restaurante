package isa.restaurante.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImagenService {

    private final Cloudinary cloudinary;

    // El constructor lee las claves que pusiste en application.properties
    public ImagenService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        this.cloudinary = new Cloudinary(config);
    }

    public String subirImagen(MultipartFile archivo) throws IOException {
        // Sube la foto a internet y devuelve el Link (https://...)
        Map resultado = cloudinary.uploader().upload(archivo.getBytes(), ObjectUtils.emptyMap());
        return resultado.get("url").toString();
    }
}