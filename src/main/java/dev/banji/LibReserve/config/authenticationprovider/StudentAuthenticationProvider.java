package dev.banji.LibReserve.config.authenticationprovider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Component
public class StudentAuthenticationProvider implements AuthenticationProvider {
    private final RestTemplate restTemplate;
    @Value("university.config.internal-university-url")
    private String universityUrl;

    public StudentAuthenticationProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken))
            return null;
        var matricNumber = authentication.getName();
        var password = (String) authentication.getCredentials();
        ResponseEntity<String> responseEntity = postRequest(matricNumber, password);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new BadCredentialsException("Bad Credentials");
        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), password, List.of(() -> "ROLE_STUDENT"));
    }

    private ResponseEntity<String> postRequest(String matricNumber, String password) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("matricNumber", matricNumber);
        map.add("password", password);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        return restTemplate.postForEntity(universityUrl, httpEntity, String.class);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
