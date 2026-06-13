package com.aplicacion.login.component;



import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;

@Component
public class DatabaseSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final UrlPermissionService urlPermissionService;

    public DatabaseSecurityMetadataSource(UrlPermissionService urlPermissionService) {
        this.urlPermissionService = urlPermissionService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        // extrae el request y delega en UrlPermissionService
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        return urlPermissionService.getAttributes(request);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        // no es necesario para nuestro caso
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
