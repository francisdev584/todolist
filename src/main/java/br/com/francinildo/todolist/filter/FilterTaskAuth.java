package br.com.francinildo.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.francinildo.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var servletPath = request.getServletPath();

        if (servletPath.equals("/tasks/")) {
            var authorization = request.getHeader("Authorization");
            String authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
            String authString = new String(authDecoded);
            String[] Credentials = authString.split(":");
            String username = Credentials[0];
            String password = Credentials[1];

            // Validar usuário
            var user = userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401);
            } else {
                // Validar senha
                var passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerified.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }

    }

}
