package com.ticket.shop.security;

import com.ticket.shop.command.auth.PrincipalDto;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.repository.CompanyRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuthorizationValidatorService {

    private final CompanyRepository companyRepository;

    public AuthorizationValidatorService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public boolean hasRole(String role) {
        return getPrincipal().getRoles().stream()
                .anyMatch(r -> r.name().equals(role));
    }

    public boolean isUser(Long userId) {
        return userId.equals(getPrincipal().getUserId());
    }

    private PrincipalDto getPrincipal() {
        return (PrincipalDto) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    public boolean isOnCompany(Long companyId) {
        Long principalCompanyId = getPrincipal().getCompanyId();
        if (principalCompanyId == null) {
            return false;
        }

        Optional<CompanyEntity> companyEntity = this.companyRepository.findById(principalCompanyId);
        return companyEntity.map(entity -> entity.getCompanyId().equals(companyId)).orElse(false);
    }
}
