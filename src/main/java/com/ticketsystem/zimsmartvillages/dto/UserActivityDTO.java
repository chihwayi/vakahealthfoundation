package com.ticketsystem.zimsmartvillages.dto;

import java.time.LocalDateTime;

public class UserActivityDTO {
    private String username;
    private String fullName;
    private int ticketsCreated;
    private int ticketsResolved;
    private int commentsAdded;
    private LocalDateTime lastActive;

    public UserActivityDTO(String username, String fullName, int ticketsCreated, int ticketsResolved, int commentsAdded, LocalDateTime lastActive) {
        this.username = username;
        this.fullName = fullName;
        this.ticketsCreated = ticketsCreated;
        this.ticketsResolved = ticketsResolved;
        this.commentsAdded = commentsAdded;
        this.lastActive = lastActive;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getTicketsCreated() {
        return ticketsCreated;
    }

    public void setTicketsCreated(int ticketsCreated) {
        this.ticketsCreated = ticketsCreated;
    }

    public int getTicketsResolved() {
        return ticketsResolved;
    }

    public void setTicketsResolved(int ticketsResolved) {
        this.ticketsResolved = ticketsResolved;
    }

    public int getCommentsAdded() {
        return commentsAdded;
    }

    public void setCommentsAdded(int commentsAdded) {
        this.commentsAdded = commentsAdded;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String fullName;
        private int ticketsCreated;
        private int ticketsResolved;
        private int commentsAdded;
        private LocalDateTime lastActive;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder ticketsCreated(int ticketsCreated) {
            this.ticketsCreated = ticketsCreated;
            return this;
        }

        public Builder ticketsResolved(int ticketsResolved) {
            this.ticketsResolved = ticketsResolved;
            return this;
        }

        public Builder commentsAdded(int commentsAdded) {
            this.commentsAdded = commentsAdded;
            return this;
        }

        public Builder lastActive(LocalDateTime lastActive) {
            this.lastActive = lastActive;
            return this;
        }

        public UserActivityDTO build() {
            return new UserActivityDTO(username, fullName, ticketsCreated, ticketsResolved,
                    commentsAdded, lastActive);
        }
    }
}
