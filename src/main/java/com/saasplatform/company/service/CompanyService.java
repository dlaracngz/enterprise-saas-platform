package com.saasplatform.company.service;

import com.saasplatform.common.exception.BusinessRuleException;
import com.saasplatform.common.exception.DuplicateResourceException;
import com.saasplatform.common.exception.ResourceNotFoundException;
import com.saasplatform.common.security.CurrentUser;
import com.saasplatform.company.dto.CompanyCreateRequest;
import com.saasplatform.company.dto.CompanyResponse;
import com.saasplatform.company.dto.CompanyUpdateRequest;
import com.saasplatform.company.entity.Company;
import com.saasplatform.company.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service // Spring'e bu sınıfın Service bileşeni olduğunu söyler
public class CompanyService {
    // final : İlk atamadan sonra başka bir nesne atanamaz.
    private final CompanyRepository companyRepository;

    private final CurrentUser currentUser;

    // Constructor Injection
    public CompanyService(
            CompanyRepository companyRepository,
            CurrentUser currentUser
    ) {
        this.companyRepository = companyRepository;
        this.currentUser = currentUser;
    }

    // Şirket oluşturma
    public CompanyResponse createCompany(CompanyCreateRequest request) {

        checkPlatformAdmin();

        if (companyRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Bu e-posta adresi zaten kullanılıyor."
            );
        }

        Company company = new Company(); // Nesne oluşturma

        // Kullanıcıdan gelen DTO içerisindeki bilgileri Entity'ye aktarıyoruz.
        company.setName(request.name());
        company.setEmail(request.email());
        company.setPhone(request.phone());
        company.setTaxNumber(request.taxNumber());
        company.setAddress(request.address());

        // Veritabanına kaydetme
        Company savedCompany = companyRepository.save(company);

        // Veritabanına kaydedilen Entity nesnesini API üzerinden döndüreceğimiz Response DTO'ya dönüştürüyoruz.
        return toResponse(savedCompany);
    }

    public List<CompanyResponse> getAllCompanies() {
        // stream() -> Bir koleksiyon üzerinde sıralı veri işlemleri yapmamızı sağlar.
        // map(this::toResponse) -> Her Company Entity nesnesini CompanyResponse DTO'suna dönüştürür.
        // toList() -> İşlenen sonuçları bir listeye dönüştürür.
        checkPlatformAdmin();
        return companyRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CompanyResponse getCompanyById(UUID id) {
        checkCompanyAccess(id);
        Company company = findCompany(id);
        return toResponse(company);
    }

    // Şirket bilgilerini güncelleme
    public CompanyResponse updateCompany(UUID id, CompanyUpdateRequest request) {

        checkCompanyAccess(id);

        Company company = findCompany(id);

        boolean emailUsed = companyRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .isPresent();

        if (emailUsed) {
            throw new DuplicateResourceException(
                    "Bu e-posta başka bir şirkete ait."
            );
        }

        company.setName(request.name());
        company.setEmail(request.email());
        company.setPhone(request.phone());
        company.setTaxNumber(request.taxNumber());
        company.setAddress(request.address());

        return toResponse(companyRepository.save(company));
    }

    // Şirketi pasife alma
    public CompanyResponse deactivateCompany(UUID id) {
        checkPlatformAdmin();
        Company company = findCompany(id);
        company.setActive(false);
        return toResponse(companyRepository.save(company));
    }

    // Şirketi yeniden aktifleştirme
    public CompanyResponse activateCompany(UUID id) {
        checkPlatformAdmin();
        Company company = findCompany(id);
        company.setActive(true);
        return toResponse(companyRepository.save(company));
    }

    // Yalnızca PLATFOORM_ADMIN işlem yapabilir.
    private void checkPlatformAdmin() {
        if (!"PLATFORM_ADMIN".equals(currentUser.getRole())) {
            throw new BusinessRuleException(
                    "Bu işlem için yetkiniz yok."
            );
        }
    }

    // PLATFORM_ADMIN tüm şirketlere, TENANT_ADMIN yalnızca kendi şirketlerine erişebilir
    private void checkCompanyAccess(UUID companyId) {
        if ("PLATFORM_ADMIN".equals(currentUser.getRole())) {
            return;
        }

        if (!companyId.equals(currentUser.getCompanyId())) {
            throw new BusinessRuleException(
                    "Bu şirkete erişim hakkınız yok."
            );
        }
    }

    // ID ile şirketi bulma işlemini ortaklaştırma
    private Company findCompany(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Şirket bulunamadı."));
    }

    // Entity nesnesini Response DTO'ya dönüştürme
    private CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getEmail(),
                company.getPhone(),
                company.getTaxNumber(),
                company.getAddress(),
                company.isActive(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}
