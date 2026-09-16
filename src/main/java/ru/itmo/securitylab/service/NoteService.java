package ru.itmo.securitylab.service;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.securitylab.dto.CreateNoteRequest;
import ru.itmo.securitylab.dto.NoteResponse;
import ru.itmo.securitylab.entity.Note;
import ru.itmo.securitylab.entity.User;
import ru.itmo.securitylab.exception.UserNotFoundException;
import ru.itmo.securitylab.mapper.NoteMapper;
import ru.itmo.securitylab.repository.NoteRepository;
import ru.itmo.securitylab.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository notes;
    private final UserRepository users;
    private final NoteMapper mapper;

    @Transactional(readOnly = true)
    public List<NoteResponse> list(Long userId) {
        if (!users.existsById(userId)) {
            throw new UserNotFoundException();
        }
        return notes.findAllByOwnerIdOrderByCreatedAtDesc(userId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public NoteResponse create(Long userId, CreateNoteRequest request) {
        User owner = users.findById(userId).orElseThrow(UserNotFoundException::new);
        Note note = new Note(owner, request.title(), request.content(), Instant.now());
        return mapper.toResponse(notes.save(note));
    }
}
