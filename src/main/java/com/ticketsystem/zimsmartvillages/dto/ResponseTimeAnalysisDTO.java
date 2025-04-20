package com.ticketsystem.zimsmartvillages.dto;

import java.util.List;

public class ResponseTimeAnalysisDTO {
    private TimelineData timeline;
    private MetricsData metrics;

    public ResponseTimeAnalysisDTO(TimelineData timeline, MetricsData metrics) {
        this.timeline = timeline;
        this.metrics = metrics;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TimelineData timeline;
        private MetricsData metrics;

        public Builder timeline(TimelineData timeline) {
            this.timeline = timeline;
            return this;
        }

        public Builder metrics(MetricsData metrics) {
            this.metrics = metrics;
            return this;
        }

        public ResponseTimeAnalysisDTO build() {
            return new ResponseTimeAnalysisDTO(timeline, metrics);
        }
    }

    public static class TimelineData {
        private List<String> labels;
        private List<Double> firstResponseTime;
        private List<Double> resolutionTime;

        public TimelineData(List<String> labels, List<Double> firstResponseTime, List<Double> resolutionTime) {
            this.labels = labels;
            this.firstResponseTime = firstResponseTime;
            this.resolutionTime = resolutionTime;
        }

        public static TimelineDataBuilder builder() {
            return new TimelineDataBuilder();
        }

        public static class TimelineDataBuilder {
            private List<String> labels;
            private List<Double> firstResponseTime;
            private List<Double> resolutionTime;

            public TimelineDataBuilder labels(List<String> labels) {
                this.labels = labels;
                return this;
            }

            public TimelineDataBuilder firstResponseTime(List<Double> firstResponseTime) {
                this.firstResponseTime = firstResponseTime;
                return this;
            }

            public TimelineDataBuilder resolutionTime(List<Double> resolutionTime) {
                this.resolutionTime = resolutionTime;
                return this;
            }

            public TimelineData build() {
                return new TimelineData(labels, firstResponseTime, resolutionTime);
            }
        }

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public List<Double> getFirstResponseTime() {
            return firstResponseTime;
        }

        public void setFirstResponseTime(List<Double> firstResponseTime) {
            this.firstResponseTime = firstResponseTime;
        }

        public List<Double> getResolutionTime() {
            return resolutionTime;
        }

        public void setResolutionTime(List<Double> resolutionTime) {
            this.resolutionTime = resolutionTime;
        }
    }

    public static class MetricsData {
        private String averageFirstResponse;
        private String averageResolutionTime;
        private String responseSLA;
        private String resolutionSLA;

        public MetricsData(String averageFirstResponse, String averageResolutionTime, String responseSLA, String resolutionSLA) {
            this.averageFirstResponse = averageFirstResponse;
            this.averageResolutionTime = averageResolutionTime;
            this.responseSLA = responseSLA;
            this.resolutionSLA = resolutionSLA;
        }

        public static MetricsDataBuilder builder() {
            return new MetricsDataBuilder();
        }

        public static class MetricsDataBuilder {
            private String averageFirstResponse;
            private String averageResolutionTime;
            private String responseSLA;
            private String resolutionSLA;

            public MetricsDataBuilder averageFirstResponse(String averageFirstResponse) {
                this.averageFirstResponse = averageFirstResponse;
                return this;
            }

            public MetricsDataBuilder averageResolutionTime(String averageResolutionTime) {
                this.averageResolutionTime = averageResolutionTime;
                return this;
            }

            public MetricsDataBuilder responseSLA(String responseSLA) {
                this.responseSLA = responseSLA;
                return this;
            }

            public MetricsDataBuilder resolutionSLA(String resolutionSLA) {
                this.resolutionSLA = resolutionSLA;
                return this;
            }

            public MetricsData build() {
                return new MetricsData(averageFirstResponse, averageResolutionTime, responseSLA, resolutionSLA);
            }
        }

        public String getAverageFirstResponse() {
            return averageFirstResponse;
        }

        public void setAverageFirstResponse(String averageFirstResponse) {
            this.averageFirstResponse = averageFirstResponse;
        }

        public String getAverageResolutionTime() {
            return averageResolutionTime;
        }

        public void setAverageResolutionTime(String averageResolutionTime) {
            this.averageResolutionTime = averageResolutionTime;
        }

        public String getResponseSLA() {
            return responseSLA;
        }

        public void setResponseSLA(String responseSLA) {
            this.responseSLA = responseSLA;
        }

        public String getResolutionSLA() {
            return resolutionSLA;
        }

        public void setResolutionSLA(String resolutionSLA) {
            this.resolutionSLA = resolutionSLA;
        }
    }

    public TimelineData getTimeline() {
        return timeline;
    }

    public void setTimeline(TimelineData timeline) {
        this.timeline = timeline;
    }

    public MetricsData getMetrics() {
        return metrics;
    }

    public void setMetrics(MetricsData metrics) {
        this.metrics = metrics;
    }
}
