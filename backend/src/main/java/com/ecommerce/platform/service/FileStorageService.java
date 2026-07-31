package com.ecommerce.platform.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeProductImage(
            MultipartFile file
    );

}