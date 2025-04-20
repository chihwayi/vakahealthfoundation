package com.ticketsystem.zimsmartvillages.dto;

import java.util.Map;

public class PriorityAnalysisDTO {
    private Map<String, Integer> new_;
    private Map<String, Integer> resolved;

    public PriorityAnalysisDTO(Map<String, Integer> new_, Map<String, Integer> resolved) {
        this.new_ = new_;
        this.resolved = resolved;
    }

    public Map<String, Integer> getNew_() {
        return new_;
    }

    public void setNew_(Map<String, Integer> new_) {
        this.new_ = new_;
    }

    public Map<String, Integer> getResolved() {
        return resolved;
    }

    public void setResolved(Map<String, Integer> resolved) {
        this.resolved = resolved;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, Integer> new_;
        private Map<String, Integer> resolved;

        public Builder new_(Map<String, Integer> new_) {
            this.new_ = new_;
            return this;
        }

        public Builder resolved(Map<String, Integer> resolved) {
            this.resolved = resolved;
            return this;
        }

        public PriorityAnalysisDTO build() {
            return new PriorityAnalysisDTO(new_, resolved);
        }
    }
}
