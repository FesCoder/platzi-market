package com.platzi.market.web.security.filter;

import com.platzi.market.domain.service.PlatziUserDetailsService;
import com.platzi.market.web.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilterRequest extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PlatziUserDetailsService platziUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Capturamos el encabezado de la petición, el header Authorization
        String authorizationHeader = request.getHeader("Authorization");

        // Preguntamos si comienza con `Bearer`, recordemos que con `JWT` debemos de usar el prefijo `Bearer`
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            // Desde la posición 7 es donde está el `JWT`
            String jwt = authorizationHeader.substring(7);
            // Extraemos el nombre del usuario de la petición
            String username = jwtUtil.extractUsername(jwt);

            // Verifica que en el contexto aún no existe ninguna autenticación para este usuario.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Verifica el usuario si existe dentro de nuestro sistema de autenticación
                UserDetails userDetails = platziUserDetailsService.loadUserByUsername(username);

                // Verificamos si el JWT es correcto
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Levantamos una sesión para ese usuario
                    // Credenciales nulas y getAuthorities para que se envíen ahi todos los roles que tiene nuestro usuario
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // Agregamos detalles de la conexión
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Asignamos autenticación, queda guardada y no necesita pasar por el filtro de nuevo
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        // Para que el filtro sea evaluado
        filterChain.doFilter(request,response);
    }
}
