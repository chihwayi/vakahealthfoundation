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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository,
                         FileStorageService fileStorageService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
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

        // Set content type and related content
        ticket.setContentType(ticketDto.getContentType());
        if (ticketDto.getContentType() == Ticket.ContentType.TEXT) {
            ticket.setTextContent(ticketDto.getTextContent());
        } else if (ticketDto.getContentType() == Ticket.ContentType.IMAGE) {
            ticket.setImagePath(ticketDto.getImagePath());
        } else if (ticketDto.getContentType() == Ticket.ContentType.AUDIO) {
            ticket.setAudioPath(ticketDto.getAudioPath());
        }

        if (ticketDto.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(ticketDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ticketDto.getAssignedToId()));
            ticket.setAssignedTo(assignedUser);
        }

        return convertToDto(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketDto createTicketWithMedia(String title, String description, Ticket.Priority priority,
                                           MultipartFile file, Ticket.ContentType contentType, User currentUser) throws IOException {
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setStatus(Ticket.Status.OPEN);
        ticket.setCreator(currentUser);
        ticket.setContentType(contentType);

        if (contentType == Ticket.ContentType.IMAGE && file != null) {
            String imagePath = fileStorageService.storeFile(file, "image");
            ticket.setImagePath(imagePath);
        } else if (contentType == Ticket.ContentType.AUDIO && file != null) {
            String audioPath = fileStorageService.storeFile(file, "audio");
            ticket.setAudioPath(audioPath);
        } else if (contentType == Ticket.ContentType.TEXT) {
            ticket.setTextContent(description);
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

        // Update content fields if provided
        if (ticketDto.getContentType() != null) {
            ticket.setContentType(ticketDto.getContentType());

            if (ticketDto.getContentType() == Ticket.ContentType.TEXT && ticketDto.getTextContent() != null) {
                ticket.setTextContent(ticketDto.getTextContent());
            } else if (ticketDto.getContentType() == Ticket.ContentType.IMAGE && ticketDto.getImagePath() != null) {
                ticket.setImagePath(ticketDto.getImagePath());
            } else if (ticketDto.getContentType() == Ticket.ContentType.AUDIO && ticketDto.getAudioPath() != null) {
                ticket.setAudioPath(ticketDto.getAudioPath());
            }
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

    public Page<TicketDto> getTicketsByContentType(Ticket.ContentType contentType, Pageable pageable) {
        return ticketRepository.findByContentType(contentType, pageable)
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
        dto.setContentType(ticket.getContentType());
        dto.setTextContent(ticket.getTextContent());
        dto.setImagePath(ticket.getImagePath());
        dto.setAudioPath(ticket.getAudioPath());

        dto.setCreatorId(ticket.getCreator().getId());
        dto.setCreatorName(ticket.getCreator().getFullName());

        if (ticket.getAssignedTo() != null) {
            dto.setAssignedToId(ticket.getAssignedTo().getId());
            dto.setAssignedToName(ticket.getAssignedTo().getFullName());
        }

        return dto;
    }
}
