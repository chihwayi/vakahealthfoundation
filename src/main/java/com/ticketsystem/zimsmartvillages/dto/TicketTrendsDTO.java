package com.ticketsystem.zimsmartvillages.dto;

import java.util.List;

public class TicketTrendsDTO {
    private List<String> labels;
    private List<Integer> created;
    private List<Integer> resolved;

    public TicketTrendsDTO(List<String> labels, List<Integer> created, List<Integer> resolved) {
        this.labels = labels;
        this.created = created;
        this.resolved = resolved;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> labels;
        private List<Integer> created;
        private List<Integer> resolved;

        public Builder labels(List<String> labels) {
            this.labels = labels;
            return this;
        }

        public Builder created(List<Integer> created) {
            this.created = created;
            return this;
        }

        public Builder resolved(List<Integer> resolved) {
            this.resolved = resolved;
            return this;
        }

        public TicketTrendsDTO build() {
            return new TicketTrendsDTO(labels, created, resolved);
        }
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Integer> getCreated() {
        return created;
    }

    public void setCreated(List<Integer> created) {
        this.created = created;
    }

    public List<Integer> getResolved() {
        return resolved;
    }

    public void setResolved(List<Integer> resolved) {
        this.resolved = resolved;
    }
}
