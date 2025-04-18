package com.ticketsystem.vakahealthfoundation.controller;

import com.ticketsystem.vakahealthfoundation.dto.TicketDto;
import com.ticketsystem.vakahealthfoundation.model.Ticket;
import com.ticketsystem.vakahealthfoundation.model.User;
import com.ticketsystem.vakahealthfoundation.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<Page<TicketDto>> getAllTickets(Pageable pageable) {
        return ResponseEntity.ok(ticketService.getAllTickets(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@RequestBody TicketDto ticketDto,
                                                  @AuthenticationPrincipal User currentUser) {
        return new ResponseEntity<>(ticketService.createTicket(ticketDto, currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable Long id,
                                                  @RequestBody TicketDto ticketDto) {
        return ResponseEntity.ok(ticketService.updateTicket(id, ticketDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketDto> updateTicketStatus(@PathVariable Long id,
                                                        @RequestParam Ticket.Status status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, status));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<TicketDto> assignTicket(@PathVariable Long id,
                                                  @RequestParam Long userId) {
        return ResponseEntity.ok(ticketService.assignTicket(id, userId));
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<Page<TicketDto>> getMyTickets(@AuthenticationPrincipal User currentUser,
                                                        Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsByCreator(currentUser, pageable));
    }

    @GetMapping("/assigned-to-me")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<Page<TicketDto>> getAssignedToMe(@AuthenticationPrincipal User currentUser,
                                                           Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsAssignedTo(currentUser, pageable));
    }

    @GetMapping("/by-status")
    public ResponseEntity<Page<TicketDto>> getTicketsByStatus(@RequestParam Ticket.Status status,
                                                              Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status, pageable));
    }

    @GetMapping("/by-priority")
    public ResponseEntity<Page<TicketDto>> getTicketsByPriority(@RequestParam Ticket.Priority priority,
                                                                Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsByPriority(priority, pageable));
    }
}
