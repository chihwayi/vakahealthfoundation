package com.ticketsystem.vakahealthfoundation.repository;

import com.ticketsystem.vakahealthfoundation.model.User;
import org.springframework.data.domain.Page;
import com.ticketsystem.vakahealthfoundation.model.Ticket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByCreator(User creator, Pageable pageable);
    Page<Ticket> findByAssignedTo(User assignedTo, Pageable pageable);
    Page<Ticket> findByStatus(Ticket.Status status, Pageable pageable);
    Page<Ticket> findByPriority(Ticket.Priority priority, Pageable pageable);
    Page<Ticket> findByCreatedDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    List<Ticket> findTop5ByOrderByCreatedDateDesc();
}

