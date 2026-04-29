package org.example.rssreader.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.rssreader.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private final UserService userService;

    public OAuth2LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            userService.findOrCreateOAuthUser(oauthToken);
        }

        response.sendRedirect(request.getContextPath() + "/posts");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String message = exception.getMessage();

        request.getSession().setAttribute(
                "oauth2Error",
                message == null ? "OAuth2 login failed" : message
        );

        response.sendRedirect(request.getContextPath() + "/login?oauth2Error=true");
    }
}