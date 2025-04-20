package com.ticketsystem.zimsmartvillages.repository;

import com.ticketsystem.zimsmartvillages.model.Comment;
import com.ticketsystem.zimsmartvillages.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketOrderByCreatedDateDesc(Ticket ticket);
}
