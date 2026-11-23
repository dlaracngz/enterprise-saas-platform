package com.saasplatform.user.controller;


import com.saasplatform.user.dto.UserCreateRequest;
import com.saasplatform.user.dto.UserResponse;
import com.saasplatform.user.dto.UserUpdateRequest;
import com.saasplatform.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Şirkete yeni kullanıcı ekleme
    @PostMapping("/companies/{companyId}/users")
    public UserResponse createUser(
            @PathVariable UUID companyId,
            @Valid @RequestBody UserCreateRequest request
    ) {
        return userService.createCompanyUser(companyId, request);
    }

    // Şirkete TENANT_ADMIN ekleme
    @PostMapping("/companies/{companyId}/tenant-admins")
    public UserResponse createTenantAdmin(
            @PathVariable UUID companyId,
            @Valid @RequestBody UserCreateRequest request
    ) {
        return userService.createTenantAdmin(companyId, request);
    }

    // Şirketin kullanıcılarını listeleme
    @GetMapping("/companies/{companyId}/users")
    public List<UserResponse> getCompanyUsers(
            @PathVariable UUID companyId
    ) {
        return userService.getCompanyUsers(companyId);
    }

    // Kullanıcı detayını getirme
    @GetMapping("/users/{userId}")
    public UserResponse getUserById(
            @PathVariable UUID userId
    ) {
        return userService.getUserById(userId);
    }

    // Kullanıcı bilgilerini güncelleme
    @PutMapping("/users/{userId}")
    public UserResponse updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return userService.updateUser(userId, request);
    }

    // Kullanıcıyı pasifleştirme
    @PatchMapping("/users/{userId}/deactivate")
    public UserResponse deactivateUser(
            @PathVariable UUID userId
    ) {
        return userService.deactivateUser(userId);
    }

    // Kullanıcıyı aktifleştirme
    @PatchMapping("/users/{userId}/activate")
    public UserResponse activateUser(
            @PathVariable UUID userId
    ) {
        return userService.activateUser(userId);
    }
}
