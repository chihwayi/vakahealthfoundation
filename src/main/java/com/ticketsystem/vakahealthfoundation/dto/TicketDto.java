package com.ticketsystem.vakahealthfoundation.dto;

import com.ticketsystem.vakahealthfoundation.model.Ticket;

import java.time.LocalDateTime;

public class TicketDto {
    private Long id;
    private String title;
    private String description;
    private Ticket.Priority priority;
    private Ticket.Status status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long creatorId;
    private String creatorName;
    private Long assignedToId;
    private String assignedToName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Ticket.Priority getPriority() {
        return priority;
    }

    public void setPriority(Ticket.Priority priority) {
        this.priority = priority;
    }

    public Ticket.Status getStatus() {
        return status;
    }

    public void setStatus(Ticket.Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }
}
