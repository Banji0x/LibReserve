package dev.banji.LibReserve.config.authenticationproviders;

import dev.banji.LibReserve.config.tokens.StudentAuthenticationToken;
import dev.banji.LibReserve.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Component
public class StudentAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService studentUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final StudentRepository studentRepository;

    @Value("${university.url}")
    private String universityUrl;

    public StudentAuthenticationProvider(UserDetailsService studentUserDetailsService, StudentRepository studentRepository, PasswordEncoder passwordEncoder, RestTemplate restTemplate) {
        this.studentUserDetailsService = studentUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.studentRepository = studentRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof StudentAuthenticationToken studentAuthenticationToken)) //delegate the authentication to another provider...
            return null;
        if ((authentication.getPrincipal() == null || authentication.getCredentials() == null))
            throw new BadCredentialsException("Bad Credentials");
        var matricNumber = (String) studentAuthenticationToken.getPrincipal();
        var rawCredentials = (String) studentAuthenticationToken.getCredentials();
        var studentDetails = studentUserDetailsService.loadUserByUsername(matricNumber);
        if (studentDetails == null) {
            //call external api to verify student
            //if valid create the student in the database
            //else throw exception
            //return an authenticatedStudentToken...
            ResponseEntity<String> responseEntity = postRequest(restTemplate, matricNumber, rawCredentials);
            if (!responseEntity.getStatusCode().is2xxSuccessful())
                throw new BadCredentialsException("Bad Credentials");
            String body = responseEntity.getBody();
            //create a new Student from this...
//            studentRepository.save();  //save student to repository...
            return StudentAuthenticationToken.authenticatedToken("", "",
                    List.of(() -> "ROLE_STUDENT")
            );
        } else {
            if (!passwordEncoder.matches(rawCredentials, studentDetails.getPassword()))
                throw new BadCredentialsException("Credentials do not match.");
            if (!studentDetails.isEnabled())
                throw new DisabledException("Account is disabled.");
            if (!studentDetails.isAccountNonLocked())
                throw new LockedException("Account is locked.");
            return StudentAuthenticationToken.authenticatedToken(studentDetails.getUsername(), studentDetails.getPassword(), studentDetails.getAuthorities());
        }
    }

    private ResponseEntity<String> postRequest(RestTemplate restTemplate, String matricNumber, String password) {//this block of code sends
        // a POST request to the university's website to verify if the principal is a registered student or not.
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
        return StudentAuthenticationToken.class.isAssignableFrom(authentication);
    }//
}
