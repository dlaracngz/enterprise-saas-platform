package com.saasplatform.company.controller;

import com.saasplatform.company.dto.CompanyCreateRequest;
import com.saasplatform.company.dto.CompanyResponse;
import com.saasplatform.company.dto.CompanyUpdateRequest;
import com.saasplatform.company.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// @Valid, DTO üzerindeki doğrulama kurallarını çalıştırır.
// * @NotBlank : Boş alanı engeller.
// * @Email : Geçersiz e-posta formatını engeller
// * @Size : Karakter sınırını kontrol eder.
// Bu doğrulama başarısız olursa Service metoduna geçilmez.

@RestController // Sınıfın REST  API kontrolcüsü olduğunu belirtir
@RequestMapping("/api/companies") // APİ'nin temel adresi
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping // Yeni kayıt oluşturma
    public CompanyResponse createCompany(@Valid @RequestBody CompanyCreateRequest request) {
        return companyService.createCompany(request);
    }
    // @RequestBody : İstek gövdesindeki JSON verisini Java nesnesine dönüştürür.

    @GetMapping // Veri getirme isteği
    public List<CompanyResponse> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}")
    public CompanyResponse getCompanyById(@PathVariable UUID id) {
        return companyService.getCompanyById(id);
    }
    // PathVariable --> URL içerisindeki değişkeni alır.

    @PutMapping("/{id}") // Kayıt güncelleme isteği
    public CompanyResponse updateCompany(@PathVariable UUID id, @Valid @RequestBody CompanyUpdateRequest request) {
        return companyService.updateCompany(id, request);
    }

    // Şirketi pasife al
    @PatchMapping("/{id}/deactivate") // Kaydın belirli bir bölümünü günceller.
    public CompanyResponse deactivateCompany(@PathVariable UUID id) { // @PathVariable : URL içerisindeki değişkeni alır.
        return companyService.deactivateCompany(id);
    }

    // Şirketi tekrar aktifleştir
    @PatchMapping("/{id}/activate")
    public CompanyResponse activateCompany(@PathVariable UUID id) {
        return companyService.activateCompany(id);
    }

}
