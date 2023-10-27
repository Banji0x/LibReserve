package dev.banji.LibReserve.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.model.AllowedFaculties;
import dev.banji.LibReserve.service.LibrarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/lib-reserve/librarian")
@RequiredArgsConstructor
public class LibrarianController {
    private final LibraryConfigurationProperties libraryConfigurationProperties;
    private final LibrarianService librarianService;
    private final ObjectMapper objectMapper;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    public String homePage(JwtAuthenticationToken authentication) {
        return "homePage " + authentication.getName();
    }

    @GetMapping("/welcome")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    public String welcome(JwtAuthenticationToken authentication) {
        return "Welcome " + authentication.getName();
    }

    @PostMapping("/json/addNewFaculty")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    public Boolean addNewFaculties(@RequestBody String jsonData) throws JsonProcessingException {
        Set<AllowedFaculties> newFaculties = objectMapper.readValue(jsonData, new TypeReference<>() {
        });
//        return librarianService.addNewFaculties(newFaculties);
        return null;
    }

    @PostMapping("/json/removeFaculty")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    public Boolean removeFaculties(List<AllowedFaculties> removeFacultiesList) {
//        return librarianService.removeFaculties(removeFacultiesList);
        return null;
    }

}
