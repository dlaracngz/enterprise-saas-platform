package com.saasplatform.common.exception;

// Aranan bir kayıt veri tabanında bulunmadığında özel hata oluşturmak

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
