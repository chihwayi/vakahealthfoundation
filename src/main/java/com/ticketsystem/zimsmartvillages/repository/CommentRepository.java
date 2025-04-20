package com.ticketsystem.zimsmartvillages.repository;

import com.ticketsystem.zimsmartvillages.model.Comment;
import com.ticketsystem.zimsmartvillages.model.Ticket;
import com.ticketsystem.zimsmartvillages.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketOrderByCreatedDateDesc(Ticket ticket);

    List<Comment> findByAuthorAndCreatedDateAfter(User user, LocalDateTime startDate);

    List<Comment> findByTicketOrderByCreatedDateAsc(Ticket ticket);

    List<Comment> findByTicketAndAuthorOrderByCreatedDateAsc(Ticket ticket, User agent);
}
