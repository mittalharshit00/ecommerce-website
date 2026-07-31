package com.ecommerce.platform.service.impl;


import com.ecommerce.platform.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
public class FileStorageServiceImpl 
        implements FileStorageService {


    private static final String UPLOAD_DIR =
            "uploads/products";


    @Override
    public String storeProductImage(
            MultipartFile file
    ) {


        try {


            Path uploadPath =
                    Paths.get(
                            UPLOAD_DIR
                    );


            if (!Files.exists(uploadPath)) {

                Files.createDirectories(
                        uploadPath
                );

            }



            String filename =
                    UUID.randomUUID()
                    + "_"
                    + file.getOriginalFilename();



            Path filePath =
                    uploadPath.resolve(
                            filename
                    );



            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );



            return "/uploads/products/"
                    + filename;



        } catch (IOException exception) {


            throw new RuntimeException(
                    "Failed to store image",
                    exception
            );


        }


    }

}