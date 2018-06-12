package br.com.vanhoz.ricardo.securitydemo.auth;

import br.com.vanhoz.ricardo.securitydemo.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    @Autowired
    private AuthService authService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = ((HttpServletRequest) servletRequest);
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (isUnProtected(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String auth = request.getHeader("authorization");

        if (auth != null) {
            String token = auth.substring("Token ".length());
            if (authService.authenticate(token, request)) {
                System.out.println("User with token [" + token + "] authenticated");
                request.getServletContext().setAttribute("session-user",
                        authService.getUserFromToken(token).get());
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            } else {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }

        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    private boolean isUnProtected(HttpServletRequest request) {
        return request.getRequestURI().equals("/login");
    }

    @Override
    public void destroy() {

    }

}
