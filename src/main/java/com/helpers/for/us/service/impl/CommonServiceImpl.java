package com.you.and.me.service.impl;

import com.you.and.me.exception.UnAuthenticateErrorExceptionHandler;
import com.you.and.me.security.model.CustomUserDetails;
import com.you.and.me.security.model.User;
import com.you.and.me.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Override
    public Long getUserIdBySecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info(authentication.toString());
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UnAuthenticateErrorExceptionHandler("No authenticated user");
        }
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = principal.getDomainUser();
        return user.getId();
    }
}
