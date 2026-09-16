package ru.itmo.securitylab.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.securitylab.dto.CreateNoteRequest;
import ru.itmo.securitylab.dto.NoteResponse;
import ru.itmo.securitylab.service.NoteService;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService notes;

    @GetMapping
    public List<NoteResponse> list(JwtAuthenticationToken authentication) {
        return notes.list(Long.valueOf(authentication.getToken().getSubject()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(JwtAuthenticationToken authentication,
                               @Valid @RequestBody CreateNoteRequest request) {
        return notes.create(Long.valueOf(authentication.getToken().getSubject()), request);
    }
}
