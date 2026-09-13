package ru.itmo.securitylab.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import ru.itmo.securitylab.dto.NoteResponse;
import ru.itmo.securitylab.entity.Note;

@Component
public class NoteMapper {
    public NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                HtmlUtils.htmlEscape(note.getTitle()),
                HtmlUtils.htmlEscape(note.getContent()),
                note.getCreatedAt()
        );
    }
}
