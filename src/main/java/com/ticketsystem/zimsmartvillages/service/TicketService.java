package com.ticketsystem.zimsmartvillages.service;

import com.ticketsystem.zimsmartvillages.dto.TicketDto;
import com.ticketsystem.zimsmartvillages.exception.ResourceNotFoundException;
import com.ticketsystem.zimsmartvillages.model.Ticket;
import com.ticketsystem.zimsmartvillages.model.User;
import com.ticketsystem.zimsmartvillages.repository.TicketRepository;
import com.ticketsystem.zimsmartvillages.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public Page<TicketDto> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public TicketDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return convertToDto(ticket);
    }

    @Transactional
    public TicketDto createTicket(TicketDto ticketDto, User currentUser) {
        Ticket ticket = new Ticket();
        ticket.setTitle(ticketDto.getTitle());
        ticket.setDescription(ticketDto.getDescription());
        ticket.setPriority(ticketDto.getPriority());
        ticket.setStatus(Ticket.Status.OPEN);
        ticket.setCreator(currentUser);

        if (ticketDto.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(ticketDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ticketDto.getAssignedToId()));
            ticket.setAssignedTo(assignedUser);
        }

        return convertToDto(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketDto updateTicket(Long id, TicketDto ticketDto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        ticket.setTitle(ticketDto.getTitle());
        ticket.setDescription(ticketDto.getDescription());
        ticket.setPriority(ticketDto.getPriority());

        if (ticketDto.getStatus() != null) {
            ticket.setStatus(ticketDto.getStatus());
        }

        if (ticketDto.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(ticketDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ticketDto.getAssignedToId()));
            ticket.setAssignedTo(assignedUser);
        } else {
            ticket.setAssignedTo(null);
        }

        return convertToDto(ticketRepository.save(ticket));
    }

    @Transactional
    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        ticketRepository.delete(ticket);
    }

    @Transactional
    public TicketDto updateTicketStatus(Long id, Ticket.Status status) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        ticket.setStatus(status);
        return convertToDto(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketDto assignTicket(Long id, Long userId) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        ticket.setAssignedTo(user);
        return convertToDto(ticketRepository.save(ticket));
    }

    public Page<TicketDto> getTicketsByCreator(User creator, Pageable pageable) {
        return ticketRepository.findByCreator(creator, pageable)
                .map(this::convertToDto);
    }

    public Page<TicketDto> getTicketsAssignedTo(User assignedTo, Pageable pageable) {
        return ticketRepository.findByAssignedTo(assignedTo, pageable)
                .map(this::convertToDto);
    }

    public Page<TicketDto> getTicketsByStatus(Ticket.Status status, Pageable pageable) {
        return ticketRepository.findByStatus(status, pageable)
                .map(this::convertToDto);
    }

    public Page<TicketDto> getTicketsByPriority(Ticket.Priority priority, Pageable pageable) {
        return ticketRepository.findByPriority(priority, pageable)
                .map(this::convertToDto);
    }

    private TicketDto convertToDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setPriority(ticket.getPriority());
        dto.setStatus(ticket.getStatus());
        dto.setCreatedDate(ticket.getCreatedDate());
        dto.setUpdatedDate(ticket.getUpdatedDate());

        dto.setCreatorId(ticket.getCreator().getId());
        dto.setCreatorName(ticket.getCreator().getFullName());

        if (ticket.getAssignedTo() != null) {
            dto.setAssignedToId(ticket.getAssignedTo().getId());
            dto.setAssignedToName(ticket.getAssignedTo().getFullName());
        }

        System.out.println("DTO: " + dto);
        return dto;
    }
}
