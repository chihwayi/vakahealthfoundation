package com.ticketsystem.zimsmartvillages.dto;

import java.util.Map;

public class StatusDistributionDTO {
    private Map<String, Integer> statusCounts;

    public Map<String, Integer> getStatusCounts() {
        return statusCounts;
    }

    public StatusDistributionDTO(Map<String, Integer> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public void setStatusCounts(Map<String, Integer> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, Integer> statusCounts;

        public Builder statusCounts(Map<String, Integer> statusCounts) {
            this.statusCounts = statusCounts;
            return this;
        }

        public StatusDistributionDTO build() {
            return new StatusDistributionDTO(statusCounts);
        }
    }

}
