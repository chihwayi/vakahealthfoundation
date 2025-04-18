package com.ticketsystem.vakahealthfoundation.repository;

import com.ticketsystem.vakahealthfoundation.model.Comment;
import com.ticketsystem.vakahealthfoundation.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketOrderByCreatedDateDesc(Ticket ticket);
}
