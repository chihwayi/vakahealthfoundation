package com.ticketsystem.zimsmartvillages.dto;

public class PerformanceMetricsDTO {
    private String agentName;
    private MetricsData metrics;
    private MetricsData teamAverage;
    private StatsData stats;

    public PerformanceMetricsDTO(String agentName, MetricsData metrics, MetricsData teamAverage, StatsData stats) {
        this.agentName = agentName;
        this.metrics = metrics;
        this.teamAverage = teamAverage;
        this.stats = stats;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String agentName;
        private MetricsData metrics;
        private MetricsData teamAverage;
        private StatsData stats;

        public Builder agentName(String agentName) {
            this.agentName = agentName;
            return this;
        }

        public Builder metrics(MetricsData metrics) {
            this.metrics = metrics;
            return this;
        }

        public Builder teamAverage(MetricsData teamAverage) {
            this.teamAverage = teamAverage;
            return this;
        }

        public Builder stats(StatsData stats) {
            this.stats = stats;
            return this;
        }

        public PerformanceMetricsDTO build() {
            return new PerformanceMetricsDTO(agentName, metrics, teamAverage, stats);
        }
    }

    public static class MetricsData {
        private int resolutionSpeed;
        private int firstResponseTime;
        private int ticketsResolved;
        private int customerSatisfaction;
        private int communicationQuality;
        private int ticketQuality;

        public MetricsData(int resolutionSpeed, int firstResponseTime, int ticketsResolved, int customerSatisfaction, int communicationQuality, int ticketQuality) {
            this.resolutionSpeed = resolutionSpeed;
            this.firstResponseTime = firstResponseTime;
            this.ticketsResolved = ticketsResolved;
            this.customerSatisfaction = customerSatisfaction;
            this.communicationQuality = communicationQuality;
            this.ticketQuality = ticketQuality;
        }

        public static MetricsDataBuilder builder() {
            return new MetricsDataBuilder();
        }

        public static class MetricsDataBuilder {
            private int resolutionSpeed;
            private int firstResponseTime;
            private int ticketsResolved;
            private int customerSatisfaction;
            private int communicationQuality;
            private int ticketQuality;

            public MetricsDataBuilder resolutionSpeed(int resolutionSpeed) {
                this.resolutionSpeed = resolutionSpeed;
                return this;
            }

            public MetricsDataBuilder firstResponseTime(int firstResponseTime) {
                this.firstResponseTime = firstResponseTime;
                return this;
            }

            public MetricsDataBuilder ticketsResolved(int ticketsResolved) {
                this.ticketsResolved = ticketsResolved;
                return this;
            }

            public MetricsDataBuilder customerSatisfaction(int customerSatisfaction) {
                this.customerSatisfaction = customerSatisfaction;
                return this;
            }

            public MetricsDataBuilder communicationQuality(int communicationQuality) {
                this.communicationQuality = communicationQuality;
                return this;
            }

            public MetricsDataBuilder ticketQuality(int ticketQuality) {
                this.ticketQuality = ticketQuality;
                return this;
            }

            public MetricsData build() {
                return new MetricsData(resolutionSpeed, firstResponseTime, ticketsResolved,
                        customerSatisfaction, communicationQuality, ticketQuality);
            }
        }

        public int getResolutionSpeed() {
            return resolutionSpeed;
        }

        public void setResolutionSpeed(int resolutionSpeed) {
            this.resolutionSpeed = resolutionSpeed;
        }

        public int getFirstResponseTime() {
            return firstResponseTime;
        }

        public void setFirstResponseTime(int firstResponseTime) {
            this.firstResponseTime = firstResponseTime;
        }

        public int getTicketsResolved() {
            return ticketsResolved;
        }

        public void setTicketsResolved(int ticketsResolved) {
            this.ticketsResolved = ticketsResolved;
        }

        public int getCustomerSatisfaction() {
            return customerSatisfaction;
        }

        public void setCustomerSatisfaction(int customerSatisfaction) {
            this.customerSatisfaction = customerSatisfaction;
        }

        public int getCommunicationQuality() {
            return communicationQuality;
        }

        public void setCommunicationQuality(int communicationQuality) {
            this.communicationQuality = communicationQuality;
        }

        public int getTicketQuality() {
            return ticketQuality;
        }

        public void setTicketQuality(int ticketQuality) {
            this.ticketQuality = ticketQuality;
        }
    }

    public static class StatsData {
        private int ticketsAssigned;
        private int ticketsResolved;
        private String averageResolutionTime;
        private String customerSatisfaction;
        private String responseRate;

        public StatsData(int ticketsAssigned, int ticketsResolved, String averageResolutionTime, String customerSatisfaction, String responseRate) {
            this.ticketsAssigned = ticketsAssigned;
            this.ticketsResolved = ticketsResolved;
            this.averageResolutionTime = averageResolutionTime;
            this.customerSatisfaction = customerSatisfaction;
            this.responseRate = responseRate;
        }

        public static StatsDataBuilder builder() {
            return new StatsDataBuilder();
        }

        public static class StatsDataBuilder {
            private int ticketsAssigned;
            private int ticketsResolved;
            private String averageResolutionTime;
            private String customerSatisfaction;
            private String responseRate;

            public StatsDataBuilder ticketsAssigned(int ticketsAssigned) {
                this.ticketsAssigned = ticketsAssigned;
                return this;
            }

            public StatsDataBuilder ticketsResolved(int ticketsResolved) {
                this.ticketsResolved = ticketsResolved;
                return this;
            }

            public StatsDataBuilder averageResolutionTime(String averageResolutionTime) {
                this.averageResolutionTime = averageResolutionTime;
                return this;
            }

            public StatsDataBuilder customerSatisfaction(String customerSatisfaction) {
                this.customerSatisfaction = customerSatisfaction;
                return this;
            }

            public StatsDataBuilder responseRate(String responseRate) {
                this.responseRate = responseRate;
                return this;
            }

            public StatsData build() {
                return new StatsData(ticketsAssigned, ticketsResolved, averageResolutionTime,
                        customerSatisfaction, responseRate);
            }
        }

        public int getTicketsAssigned() {
            return ticketsAssigned;
        }

        public void setTicketsAssigned(int ticketsAssigned) {
            this.ticketsAssigned = ticketsAssigned;
        }

        public int getTicketsResolved() {
            return ticketsResolved;
        }

        public void setTicketsResolved(int ticketsResolved) {
            this.ticketsResolved = ticketsResolved;
        }

        public String getAverageResolutionTime() {
            return averageResolutionTime;
        }

        public void setAverageResolutionTime(String averageResolutionTime) {
            this.averageResolutionTime = averageResolutionTime;
        }

        public String getCustomerSatisfaction() {
            return customerSatisfaction;
        }

        public void setCustomerSatisfaction(String customerSatisfaction) {
            this.customerSatisfaction = customerSatisfaction;
        }

        public String getResponseRate() {
            return responseRate;
        }

        public void setResponseRate(String responseRate) {
            this.responseRate = responseRate;
        }
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public MetricsData getMetrics() {
        return metrics;
    }

    public void setMetrics(MetricsData metrics) {
        this.metrics = metrics;
    }

    public MetricsData getTeamAverage() {
        return teamAverage;
    }

    public void setTeamAverage(MetricsData teamAverage) {
        this.teamAverage = teamAverage;
    }

    public StatsData getStats() {
        return stats;
    }

    public void setStats(StatsData stats) {
        this.stats = stats;
    }
}
