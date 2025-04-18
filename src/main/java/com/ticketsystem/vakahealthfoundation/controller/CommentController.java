package com.ticketsystem.vakahealthfoundation.controller;

import com.ticketsystem.vakahealthfoundation.dto.CommentDto;
import com.ticketsystem.vakahealthfoundation.model.User;
import com.ticketsystem.vakahealthfoundation.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<CommentDto>> getCommentsByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(commentService.getCommentsByTicketId(ticketId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto,
                                                    @AuthenticationPrincipal User currentUser) {
        return new ResponseEntity<>(commentService.createComment(commentDto, currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id,
                                                    @RequestBody CommentDto commentDto,
                                                    @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(commentService.updateComment(id, commentDto, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id,
                                              @AuthenticationPrincipal User currentUser) {
        commentService.deleteComment(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}

