package ru.itmo.securitylab.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.securitylab.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
