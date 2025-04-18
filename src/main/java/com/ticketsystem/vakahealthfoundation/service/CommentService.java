package com.ticketsystem.vakahealthfoundation.service;

import com.ticketsystem.vakahealthfoundation.dto.CommentDto;
import com.ticketsystem.vakahealthfoundation.exception.ResourceNotFoundException;
import com.ticketsystem.vakahealthfoundation.model.Comment;
import com.ticketsystem.vakahealthfoundation.model.Ticket;
import com.ticketsystem.vakahealthfoundation.model.User;
import com.ticketsystem.vakahealthfoundation.repository.CommentRepository;
import com.ticketsystem.vakahealthfoundation.repository.TicketRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;

    public CommentService(CommentRepository commentRepository, TicketRepository ticketRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<CommentDto> getCommentsByTicketId(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        return commentRepository.findByTicketOrderByCreatedDateDesc(ticket)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto, User currentUser) {
        Ticket ticket = ticketRepository.findById(commentDto.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + commentDto.getTicketId()));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setTicket(ticket);
        comment.setAuthor(currentUser);

        return convertToDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long id, CommentDto commentDto, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Only the author or an admin can update a comment
        if (!comment.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You are not authorized to update this comment");
        }

        comment.setContent(commentDto.getContent());
        return convertToDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long id, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Only the author or an admin can delete a comment
        if (!comment.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setTicketId(comment.getTicket().getId());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setAuthorName(comment.getAuthor().getFullName());
        return dto;
    }
}
