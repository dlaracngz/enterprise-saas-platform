package com.saasplatform.company.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Id'nin otomatik oluşmasını sağlar.
    private UUID id;

    @Column(nullable = false,length = 150) // Sütun özelliklerini belirler.
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    private String phone;

    @Column(unique = true)
    private String taxNumber;

    private String address;

    // Şirketin aktiflik durumunu tutar
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Bir Entity ilk kez veritabanına kaydedilmeden hemen önce çalışır.
    // Şirket oluşturulduğunda createdAt otomatik doldurulur.
    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // Mevcut şirket kaydı güncellenmeden hemen önce çalışır.
    // updatedAt alanını güncel tarih ve saatle doldurur.
    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    // Getter ve Setter Metodları

    public UUID getId(){
        return id;
    }

    public void setId(UUID id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getTaxNumber(){
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber){
        this.taxNumber = taxNumber;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
