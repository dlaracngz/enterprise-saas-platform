package com.saasplatform.user.service;

import com.saasplatform.common.exception.BusinessRuleException;
import com.saasplatform.common.exception.DuplicateResourceException;
import com.saasplatform.common.exception.ResourceNotFoundException;
import com.saasplatform.common.security.CurrentUser;
import com.saasplatform.company.entity.Company;
import com.saasplatform.company.repository.CompanyRepository;
import com.saasplatform.user.dto.UserCreateRequest;
import com.saasplatform.user.dto.UserResponse;
import com.saasplatform.user.dto.UserUpdateRequest;
import com.saasplatform.user.entity.User;
import com.saasplatform.user.entity.UserRole;
import com.saasplatform.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;

    public UserService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            CurrentUser currentUser
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
    }

    @Transactional
    public UserResponse createCompanyUser(UUID companyId, UserCreateRequest request) {

        // Giriş yapan kullanıcının şirket bilgisi ile kullanıcı eklemek istediği şirket aynı Id'ye mi sahip. Onun kontrolü yapılır.
        checkCompanyAccess(companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Şirket bulunamadı."));

        if (!company.isActive()) {
            throw new BusinessRuleException(
                    "Pasif şirkete kullanıcı eklenemez."
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Bu e-posta adresi zaten kullanılıyor");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Bu kullanıcı adı zaten kullanılıyor");
        }

        User user = new User();

        user.setCompany(company);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setUsername(request.username());

        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    // TENANT_ADMIN oluşturma
    @Transactional
    public UserResponse createTenantAdmin(UUID companyId, UserCreateRequest request) {

        // Yalnızca PLATFORM_ADMIN , TENANT_ADMIN oluşturabilir
        if (!"PLATFORM_ADMIN".equals(currentUser.getRole())) {
            throw new BusinessRuleException(
                    "Tenant Admin oluşturma yetkiniz yok."
            );
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Şirket bulunamadı."));

        if (!company.isActive()) {
            throw new BusinessRuleException(
                    "Pasif şirkete yönetici eklenemez."
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Bu e-posta adresi zaten kullanılıyor"
            );
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException(
                    "Bu kullanıcı adı zaten kullanılıyor"
            );
        }

        User user = new User();

        user.setCompany(company);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.TENANT_ADMIN);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);

    }


    // Kullanıcı bilgileri güncelleme
    @Transactional
    public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Kullanıcı bulunamadı"));

        checkUserAccess(user);

        boolean emailUsed = userRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(userId))
                .isPresent();

        if (emailUsed) {
            throw new DuplicateResourceException(
                    "Bu e-posta başka bir kullancıya ait"
            );
        }

        boolean usernameUsed = userRepository.findByUsername(request.username())
                .filter(existing -> !existing.getId().equals(userId))
                .isPresent();

        if (usernameUsed) {
            throw new DuplicateResourceException(
                    "Bu kullanıcı adı başka bir kullanıcıya ait"
            );
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setUsername(request.username());

        return toResponse(userRepository.save(user));
    }

    // Şirkete ait tüm kullanıcıları getirme
    @Transactional(readOnly = true)
    public List<UserResponse> getCompanyUsers(UUID companyId) {
        checkCompanyAccess(companyId);
        companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Şirket bulunamadı"));
        return userRepository.findAllByCompany_Id(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Kullanıcı bulunamadı."));

        checkUserAccess(user);
        return toResponse(user);
    }

    // Kullanıcı pasife alma
    @Transactional
    public UserResponse deactivateUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Kullanıcı bulunamadı"));

        checkUserAccess(user);

        user.setActive(false);

        return toResponse(userRepository.save(user));
    }

    // Kullanıcıyı yeniden aktifleştirme
    @Transactional
    public UserResponse activateUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Kullanıcı bulunamadı"));

        checkUserAccess(user);

        user.setActive(true);

        return toResponse(userRepository.save(user));
    }

    //
    private void checkCompanyAccess(UUID companyId) {

        // Kullanıcı PLATFORM_ADMIN ise şirket kontrolünü geçer
        if ("PLATFORM_ADMIN".equals(currentUser.getRole())) {
            return;
        }

        if (!"TENANT_ADMIN".equals(currentUser.getRole())) {
            throw new BusinessRuleException(
                    "Bu işlem için yetkiniz yok"
            );
        }

        // Kullanıcı TENANT_ADMIN ise kendi şirket ID'si işlem yapmak istediği şirketin ID'si karşılaştırılır. ID'ler farklıysa işlem engellenir.
        if (!companyId.equals(currentUser.getCompanyId())) {
            throw new BusinessRuleException(
                    "Bu şirketin kullanıcılarına erişim yetkiniz yok"
            );
        }
    }

    // İşlem yapılmak istenen kullanıcının şirketiyle giriş yapan kullanıcının şirketini karşılaştırma
    private void checkUserAccess(User user) {
        if ("PLATFORM_ADMIN".equals(currentUser.getRole())) {
            return;
        }

        if (!"TENANT_ADMIN".equals(currentUser.getRole())) {
            throw new BusinessRuleException(
                    "Bu işlem için yetkiniz yok"
            );
        }

        if (user.getCompany() == null || !user.getCompany().getId().equals(currentUser.getCompanyId())) {
            throw new BusinessRuleException(
                    "Bu kullanıcıya erişim hakkınız yok."
            );
        }
    }


    // Entity nesnesini DTO'ya dönüştürme
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getCompany() != null
                        ? user.getCompany().getId()
                        : null,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public void createPlatformAdmin(
            String firstName,
            String lastName,
            String email,
            String username,
            String password
    ) {
        if (userRepository.existsByRole(UserRole.PLATFORM_ADMIN)) {
            return;
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Bu e-posta adresi zaten kullanılıyor"
            );
        }

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException(
                    "Bu kullanıcı adı zaten kullanılıyor"
            );
        }

        User admin = new User();

        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(UserRole.PLATFORM_ADMIN);
        admin.setActive(true);
        admin.setCompany(null);

        userRepository.save(admin);
    }
}
