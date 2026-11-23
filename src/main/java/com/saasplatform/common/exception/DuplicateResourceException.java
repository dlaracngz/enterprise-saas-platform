package com.saasplatform.common.exception;

// Aynı bilginin ikinci kez kaydedilmesini engellemek
// extends RuntimeException : Java'nın mevcut hata sınıfından miras alması

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
