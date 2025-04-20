package com.ticketsystem.zimsmartvillages.controller;

import com.ticketsystem.zimsmartvillages.dto.TicketDto;
import com.ticketsystem.zimsmartvillages.model.Ticket;
import com.ticketsystem.zimsmartvillages.model.User;
import com.ticketsystem.zimsmartvillages.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketDto> createTextTicket(@RequestBody TicketDto ticketDto,
                                                      @AuthenticationPrincipal User currentUser) {
        return new ResponseEntity<>(ticketService.createTicket(ticketDto, currentUser), HttpStatus.CREATED);
    }

    @PostMapping(value = "/with-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TicketDto> createTicketWithMedia(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("priority") Ticket.Priority priority,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("contentType") Ticket.ContentType contentType,
            @AuthenticationPrincipal User currentUser) throws IOException {

        return new ResponseEntity<>(
                ticketService.createTicketWithMedia(title, description, priority, file, contentType, currentUser),
                HttpStatus.CREATED);
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

    @GetMapping("/by-content-type")
    public ResponseEntity<Page<TicketDto>> getTicketsByContentType(
            @RequestParam Ticket.ContentType contentType,
            Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsByContentType(contentType, pageable));
    }
}
