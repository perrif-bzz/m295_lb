package com.perrif.m295_lb.util.security;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Method;
import java.util.*;

public class AuthenticationFilter implements ContainerRequestFilter
{
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASIC_AUTH_SCHEME = "Basic";

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext)
    {
        Method resourceMethod = resourceInfo.getResourceMethod();

        if (resourceMethod.isAnnotationPresent(PermitAll.class))
        {
            return;
        }

        if (resourceMethod.isAnnotationPresent(DenyAll.class))
        {
            abortRequest(requestContext, Response.Status.FORBIDDEN, "Access to this resource is blocked for all users.");
            return;
        }

        final List<String> authorizationHeaders = requestContext.getHeaders().get(AUTHORIZATION_HEADER);
        if (authorizationHeaders == null || authorizationHeaders.isEmpty())
        {
            abortRequest(requestContext, Response.Status.UNAUTHORIZED, "You are not permitted to access this resource.");
            return;
        }

        final String encodedCredentials = authorizationHeaders.get(0).replaceFirst(BASIC_AUTH_SCHEME + " ", "");
        String usernameAndPassword = decodeBase64(encodedCredentials, requestContext);
        if (usernameAndPassword == null)
        {
            return;
        }

        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        if (tokenizer.countTokens() != 2)
        {
            abortRequest(requestContext, Response.Status.UNAUTHORIZED, "The authorization token is invalid.");
            return;
        }

        final String basicAuthUsername = tokenizer.nextToken();
        final String basicAuthPassword = tokenizer.nextToken();

        if (resourceMethod.isAnnotationPresent(RolesAllowed.class))
        {
            RolesAllowed rolesAnnotation = resourceMethod.getAnnotation(RolesAllowed.class);
            Set<String> allowedRoles = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

            if (!isUserAuthorized(basicAuthUsername, basicAuthPassword, allowedRoles))
            {
                abortRequest(requestContext, Response.Status.UNAUTHORIZED, "You are not permitted to access this resource.");
            }
        }
    }

    private void abortRequest(ContainerRequestContext requestContext, Response.Status status, String message)
    {
        requestContext.abortWith(Response.status(status).entity(message).build());
    }

    private String decodeBase64(String encodedString, ContainerRequestContext requestContext)
    {
        try
        {
            return new String(Base64.getDecoder().decode(encodedString));
        } catch (IllegalArgumentException e)
        {
            abortRequest(requestContext, Response.Status.UNAUTHORIZED, "The authorization token is invalid.");
            return null;
        }
    }

    private boolean isUserAuthorized(final String username, final String password, final Set<String> allowedRoles)
    {
        Map<String, String> roles = new HashMap<>();
        roles.put("admin", "ADMIN");
        roles.put("tenant", "TENANT");

        String userRole = roles.get(username);
        if (userRole != null &&
                (
                        (username.equals("admin") && password.equals("1234")) ||
                        (username.equals("cleaner") && password.equals("1234"))
                )
        )
        {
            return allowedRoles.contains(userRole);
        }

        return false;
    }
}
