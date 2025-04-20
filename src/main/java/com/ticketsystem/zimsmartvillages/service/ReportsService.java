package com.ticketsystem.zimsmartvillages.service;

import com.ticketsystem.zimsmartvillages.dto.*;
import com.ticketsystem.zimsmartvillages.model.Comment;
import com.ticketsystem.zimsmartvillages.model.Role;
import com.ticketsystem.zimsmartvillages.model.Ticket;
import com.ticketsystem.zimsmartvillages.model.User;
import com.ticketsystem.zimsmartvillages.repository.CommentRepository;
import com.ticketsystem.zimsmartvillages.repository.TicketRepository;
import com.ticketsystem.zimsmartvillages.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ReportsService(TicketRepository ticketRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public TicketTrendsDTO getTicketTrends(LocalDateTime startDate, LocalDateTime endDate) {
        Page<Ticket> ticketsPage = ticketRepository.findByCreatedDateBetween(startDate, endDate, Pageable.unpaged());
        List<Ticket> tickets = ticketsPage.getContent();

        // Calculate date intervals for the chart
        List<LocalDateTime> datePoints = calculateDatePoints(startDate, endDate);
        List<String> labels = datePoints.stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("MMM d")))
                .collect(Collectors.toList());

        // Initialize counts
        List<Integer> created = new ArrayList<>(Collections.nCopies(datePoints.size(), 0));
        List<Integer> resolved = new ArrayList<>(Collections.nCopies(datePoints.size(), 0));

        // Group tickets by date
        for (Ticket ticket : tickets) {
            int createdIndex = findDateIndex(datePoints, ticket.getCreatedDate());
            if (createdIndex >= 0) {
                created.set(createdIndex, created.get(createdIndex) + 1);
            }

            // For resolved tickets
            if (ticket.getStatus() == Ticket.Status.RESOLVED || ticket.getStatus() == Ticket.Status.CLOSED) {
                int resolvedIndex = findDateIndex(datePoints, ticket.getUpdatedDate());
                if (resolvedIndex >= 0) {
                    resolved.set(resolvedIndex, resolved.get(resolvedIndex) + 1);
                }
            }
        }

        return TicketTrendsDTO.builder()
                .labels(labels)
                .created(created)
                .resolved(resolved)
                .build();
    }

    public StatusDistributionDTO getStatusDistribution() {
        List<Ticket> tickets = ticketRepository.findAll();
        Map<String, Integer> statusCounts = new HashMap<>();

        // Initialize all possible statuses with 0 count
        for (Ticket.Status status : Ticket.Status.values()) {
            statusCounts.put(status.name(), 0);
        }

        // Count tickets by status
        for (Ticket ticket : tickets) {
            String status = ticket.getStatus().name();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }

        return StatusDistributionDTO.builder()
                .statusCounts(statusCounts)
                .build();
    }

    public PriorityAnalysisDTO getPriorityAnalysis() {
        List<Ticket> tickets = ticketRepository.findAll();

        Map<String, Integer> newTickets = new HashMap<>();
        Map<String, Integer> resolvedTickets = new HashMap<>();

        // Initialize all priorities with 0 count
        for (Ticket.Priority priority : Ticket.Priority.values()) {
            newTickets.put(priority.name(), 0);
            resolvedTickets.put(priority.name(), 0);
        }

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        for (Ticket ticket : tickets) {
            String priority = ticket.getPriority().name();

            // Count new tickets from last 30 days
            if (ticket.getCreatedDate().isAfter(thirtyDaysAgo)) {
                newTickets.put(priority, newTickets.getOrDefault(priority, 0) + 1);
            }

            // Count resolved tickets from last 30 days
            if ((ticket.getStatus() == Ticket.Status.RESOLVED || ticket.getStatus() == Ticket.Status.CLOSED)
                    && ticket.getUpdatedDate().isAfter(thirtyDaysAgo)) {
                resolvedTickets.put(priority, resolvedTickets.getOrDefault(priority, 0) + 1);
            }
        }

        return PriorityAnalysisDTO.builder()
                .new_(newTickets)
                .resolved(resolvedTickets)
                .build();
    }

    public List<UserActivityDTO> getUserActivity(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        List<User> users = userRepository.findAll();
        List<UserActivityDTO> result = new ArrayList<>();

        for (User user : users) {
            List<Ticket> createdTickets = ticketRepository.findByCreatorAndCreatedDateAfter(user, startDate);
            List<Ticket> resolvedTickets = ticketRepository.findByAssignedToAndStatusInAndUpdatedDateAfter(
                    user,
                    Arrays.asList(Ticket.Status.RESOLVED, Ticket.Status.CLOSED),
                    startDate);
            List<Comment> comments = commentRepository.findByAuthorAndCreatedDateAfter(user, startDate);

            // Find last active date
            LocalDateTime lastActive = startDate;
            if (!comments.isEmpty()) {
                LocalDateTime lastCommentDate = comments.stream()
                        .map(Comment::getCreatedDate)
                        .max(LocalDateTime::compareTo)
                        .orElse(startDate);
                lastActive = lastCommentDate.isAfter(lastActive) ? lastCommentDate : lastActive;
            }

            if (!resolvedTickets.isEmpty()) {
                LocalDateTime lastResolvedDate = resolvedTickets.stream()
                        .map(Ticket::getUpdatedDate)
                        .max(LocalDateTime::compareTo)
                        .orElse(startDate);
                lastActive = lastResolvedDate.isAfter(lastActive) ? lastResolvedDate : lastActive;
            }

            result.add(UserActivityDTO.builder()
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .ticketsCreated(createdTickets.size())
                    .ticketsResolved(resolvedTickets.size())
                    .commentsAdded(comments.size())
                    .lastActive(lastActive)
                    .build());
        }

        // Sort by activity level (tickets resolved + comments)
        result.sort((a, b) -> Integer.compare(
                b.getTicketsResolved() + b.getCommentsAdded(),
                a.getTicketsResolved() + a.getCommentsAdded()));

        return result;
    }

    public PerformanceMetricsDTO getPerformanceMetrics(Long userId) {
        if (userId != null) {
            // Individual agent metrics
            User agent = userRepository.findById(userId).orElse(null);
            if (agent == null) {
                throw new RuntimeException("User not found with ID: " + userId);
            }

            PerformanceMetricsDTO.MetricsData agentMetrics = calculateAgentMetrics(agent);
            PerformanceMetricsDTO.MetricsData teamAverage = calculateTeamAverageMetrics();
            PerformanceMetricsDTO.StatsData agentStats = calculateAgentStats(agent);

            return PerformanceMetricsDTO.builder()
                    .agentName(agent.getFullName())
                    .metrics(agentMetrics)
                    .teamAverage(teamAverage)
                    .stats(agentStats)
                    .build();
        } else {
            // Team metrics
            PerformanceMetricsDTO.MetricsData teamMetrics = calculateTeamAverageMetrics();
            PerformanceMetricsDTO.StatsData teamStats = calculateTeamStats();

            return PerformanceMetricsDTO.builder()
                    .agentName("Team")
                    .metrics(teamMetrics)
                    .teamAverage(teamMetrics) // Same values for comparison
                    .stats(teamStats)
                    .build();
        }
    }

    public ResponseTimeAnalysisDTO getResponseTimeAnalysis(LocalDateTime startDate, LocalDateTime endDate) {
        Page<Ticket> ticketsPage = ticketRepository.findByCreatedDateBetween(startDate, endDate, Pageable.unpaged());
        List<Ticket> tickets = ticketsPage.getContent();

        // Calculate date intervals for the chart
        List<LocalDateTime> datePoints = calculateDatePoints(startDate, endDate);
        List<String> labels = datePoints.stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("MMM d")))
                .collect(Collectors.toList());

        // Initialize data arrays
        List<Double> firstResponseTimes = new ArrayList<>(Collections.nCopies(datePoints.size(), 0.0));
        List<Double> resolutionTimes = new ArrayList<>(Collections.nCopies(datePoints.size(), 0.0));
        int[] firstResponseCounts = new int[datePoints.size()];
        int[] resolutionCounts = new int[datePoints.size()];

        // Calculate total metrics for SLA
        int totalTickets = tickets.size();
        int responseSLAMet = 0;
        int resolutionSLAMet = 0;
        double totalFirstResponseHours = 0;
        double totalResolutionHours = 0;
        int ticketsWithFirstResponse = 0;
        int ticketsResolved = 0;

        for (Ticket ticket : tickets) {
            int dateIndex = findDateIndex(datePoints, ticket.getCreatedDate());
            if (dateIndex < 0) continue;

            // Calculate first response time (using the first comment as a proxy)
            List<Comment> comments = commentRepository.findByTicketOrderByCreatedDateAsc(ticket);
            if (!comments.isEmpty()) {
                Comment firstComment = comments.get(0);
                double hoursToFirstResponse = ChronoUnit.MINUTES.between(
                        ticket.getCreatedDate(), firstComment.getCreatedDate()) / 60.0;

                firstResponseTimes.set(dateIndex, firstResponseTimes.get(dateIndex) + hoursToFirstResponse);
                firstResponseCounts[dateIndex]++;

                totalFirstResponseHours += hoursToFirstResponse;
                ticketsWithFirstResponse++;

                // Check if first response SLA was met (e.g., within 4 hours)
                if (hoursToFirstResponse <= 4) {
                    responseSLAMet++;
                }
            }

            // Calculate resolution time
            if (ticket.getStatus() == Ticket.Status.RESOLVED || ticket.getStatus() == Ticket.Status.CLOSED) {
                double hoursToResolution = ChronoUnit.MINUTES.between(
                        ticket.getCreatedDate(), ticket.getUpdatedDate()) / 60.0;

                resolutionTimes.set(dateIndex, resolutionTimes.get(dateIndex) + hoursToResolution);
                resolutionCounts[dateIndex]++;

                totalResolutionHours += hoursToResolution;
                ticketsResolved++;

                // Check if resolution SLA was met (e.g., within 24 hours)
                if (hoursToResolution <= 24) {
                    resolutionSLAMet++;
                }
            }
        }

        // Calculate averages for each date point
        for (int i = 0; i < datePoints.size(); i++) {
            if (firstResponseCounts[i] > 0) {
                firstResponseTimes.set(i, firstResponseTimes.get(i) / firstResponseCounts[i]);
            }
            if (resolutionCounts[i] > 0) {
                resolutionTimes.set(i, resolutionTimes.get(i) / resolutionCounts[i]);
            }
        }

        // Calculate overall metrics
        String avgFirstResponse = ticketsWithFirstResponse > 0 ?
                formatDuration(totalFirstResponseHours / ticketsWithFirstResponse) : "0 hours";
        String avgResolutionTime = ticketsResolved > 0 ?
                formatDuration(totalResolutionHours / ticketsResolved) : "0 hours";
        String responseSLA = totalTickets > 0 ?
                String.format("%d%%", (responseSLAMet * 100) / totalTickets) : "0%";
        String resolutionSLA = totalTickets > 0 ?
                String.format("%d%%", (resolutionSLAMet * 100) / totalTickets) : "0%";

        ResponseTimeAnalysisDTO.TimelineData timeline = ResponseTimeAnalysisDTO.TimelineData.builder()
                .labels(labels)
                .firstResponseTime(firstResponseTimes)
                .resolutionTime(resolutionTimes)
                .build();

        ResponseTimeAnalysisDTO.MetricsData metrics = ResponseTimeAnalysisDTO.MetricsData.builder()
                .averageFirstResponse(avgFirstResponse)
                .averageResolutionTime(avgResolutionTime)
                .responseSLA(responseSLA)
                .resolutionSLA(resolutionSLA)
                .build();

        return ResponseTimeAnalysisDTO.builder()
                .timeline(timeline)
                .metrics(metrics)
                .build();
    }

    // Helper methods

    private List<LocalDateTime> calculateDatePoints(LocalDateTime startDate, LocalDateTime endDate) {
        List<LocalDateTime> datePoints = new ArrayList<>();
        LocalDateTime current = startDate;

        // Determine appropriate intervals based on date range
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        int intervalDays;

        if (daysBetween <= 14) {
            intervalDays = 1; // Daily intervals for short ranges
        } else if (daysBetween <= 60) {
            intervalDays = 5; // 5-day intervals for medium ranges
        } else {
            intervalDays = 10; // 10-day intervals for longer ranges
        }

        while (!current.isAfter(endDate)) {
            datePoints.add(current);
            current = current.plusDays(intervalDays);
        }

        return datePoints;
    }

    private int findDateIndex(List<LocalDateTime> datePoints, LocalDateTime date) {
        if (date.isBefore(datePoints.get(0)) || date.isAfter(datePoints.get(datePoints.size() - 1))) {
            return -1;
        }

        for (int i = 0; i < datePoints.size() - 1; i++) {
            if ((date.isEqual(datePoints.get(i)) || date.isAfter(datePoints.get(i))) &&
                    date.isBefore(datePoints.get(i + 1))) {
                return i;
            }
        }

        return datePoints.size() - 1;
    }

    private PerformanceMetricsDTO.MetricsData calculateAgentMetrics(User agent) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Get tickets assigned to this agent in the last 30 days
        List<Ticket> assignedTickets = ticketRepository.findByAssignedToAndCreatedDateAfter(agent, thirtyDaysAgo);
        List<Ticket> resolvedTickets = assignedTickets.stream()
                .filter(ticket -> ticket.getStatus() == Ticket.Status.RESOLVED || ticket.getStatus() == Ticket.Status.CLOSED)
                .collect(Collectors.toList());

        // Calculate metrics
        int resolutionSpeed = calculateResolutionSpeedScore(resolvedTickets);
        int firstResponseTime = calculateFirstResponseTimeScore(assignedTickets);
        int ticketsResolved = calculateTicketsResolvedScore(resolvedTickets.size());
        int customerSatisfaction = calculateCustomerSatisfactionScore(resolvedTickets);
        int communicationQuality = calculateCommunicationQualityScore(agent);
        int ticketQuality = calculateTicketQualityScore(resolvedTickets);

        return PerformanceMetricsDTO.MetricsData.builder()
                .resolutionSpeed(resolutionSpeed)
                .firstResponseTime(firstResponseTime)
                .ticketsResolved(ticketsResolved)
                .customerSatisfaction(customerSatisfaction)
                .communicationQuality(communicationQuality)
                .ticketQuality(ticketQuality)
                .build();
    }

    private PerformanceMetricsDTO.MetricsData calculateTeamAverageMetrics() {
        // Replace with a query that fits your data model
        Set<Role> roles = new HashSet<>();
        Role r = new Role();
        r.setName("SUPPORT");
        roles.add(r);
        List<User> supportAgents = userRepository.findByRoles(roles); // Assuming there's a role field
        // Alternatively, get all users and filter if no specific finder method exists
        // List<User> allUsers = userRepository.findAll();
        // List<User> supportAgents = allUsers.stream().filter(user -> "SUPPORT".equals(user.getRole())).collect(Collectors.toList());

        if (supportAgents.isEmpty()) {
            return PerformanceMetricsDTO.MetricsData.builder()
                    .resolutionSpeed(0)
                    .firstResponseTime(0)
                    .ticketsResolved(0)
                    .customerSatisfaction(0)
                    .communicationQuality(0)
                    .ticketQuality(0)
                    .build();
        }

        int totalResolutionSpeed = 0;
        int totalFirstResponseTime = 0;
        int totalTicketsResolved = 0;
        int totalCustomerSatisfaction = 0;
        int totalCommunicationQuality = 0;
        int totalTicketQuality = 0;

        for (User agent : supportAgents) {
            PerformanceMetricsDTO.MetricsData agentMetrics = calculateAgentMetrics(agent);
            totalResolutionSpeed += agentMetrics.getResolutionSpeed();
            totalFirstResponseTime += agentMetrics.getFirstResponseTime();
            totalTicketsResolved += agentMetrics.getTicketsResolved();
            totalCustomerSatisfaction += agentMetrics.getCustomerSatisfaction();
            totalCommunicationQuality += agentMetrics.getCommunicationQuality();
            totalTicketQuality += agentMetrics.getTicketQuality();
        }

        int agentCount = supportAgents.size();

        return PerformanceMetricsDTO.MetricsData.builder()
                .resolutionSpeed(totalResolutionSpeed / agentCount)
                .firstResponseTime(totalFirstResponseTime / agentCount)
                .ticketsResolved(totalTicketsResolved / agentCount)
                .customerSatisfaction(totalCustomerSatisfaction / agentCount)
                .communicationQuality(totalCommunicationQuality / agentCount)
                .ticketQuality(totalTicketQuality / agentCount)
                .build();
    }

    private PerformanceMetricsDTO.StatsData calculateAgentStats(User agent) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Get tickets assigned to this agent in the last 30 days
        List<Ticket> assignedTickets = ticketRepository.findByAssignedToAndCreatedDateAfter(agent, thirtyDaysAgo);
        List<Ticket> resolvedTickets = assignedTickets.stream()
                .filter(ticket -> ticket.getStatus() == Ticket.Status.RESOLVED || ticket.getStatus() == Ticket.Status.CLOSED)
                .collect(Collectors.toList());

        // Calculate average resolution time
        String avgResolutionTime = "0 hours";
        if (!resolvedTickets.isEmpty()) {
            double totalHours = 0;
            for (Ticket ticket : resolvedTickets) {
                totalHours += ChronoUnit.MINUTES.between(
                        ticket.getCreatedDate(), ticket.getUpdatedDate()) / 60.0;
            }
            avgResolutionTime = formatDuration(totalHours / resolvedTickets.size());
        }

        // Customer satisfaction (mock for now)
        String customerSatisfaction = String.format("%.1f/5", 4 + Math.random());

        // Response rate (percentage of tickets with first response within 4 hours)
        int responseCount = 0;
        for (Ticket ticket : assignedTickets) {
            List<Comment> comments = commentRepository.findByTicketAndAuthorOrderByCreatedDateAsc(ticket, agent);
            if (!comments.isEmpty()) {
                Comment firstComment = comments.get(0);
                if (ChronoUnit.HOURS.between(ticket.getCreatedDate(), firstComment.getCreatedDate()) <= 4) {
                    responseCount++;
                }
            }
        }
        String responseRate = assignedTickets.isEmpty() ? "0%" :
                String.format("%d%%", (responseCount * 100) / assignedTickets.size());

        return PerformanceMetricsDTO.StatsData.builder()
                .ticketsAssigned(assignedTickets.size())
                .ticketsResolved(resolvedTickets.size())
                .averageResolutionTime(avgResolutionTime)
                .customerSatisfaction(customerSatisfaction)
                .responseRate(responseRate)
                .build();
    }

    private PerformanceMetricsDTO.StatsData calculateTeamStats() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Get all tickets in the last 30 days
        List<Ticket> recentTickets = ticketRepository.findByCreatedDateAfter(thirtyDaysAgo);
        List<Ticket> resolvedTickets = recentTickets.stream()
                .filter(ticket -> ticket.getStatus() == Ticket.Status.RESOLVED || ticket.getStatus() == Ticket.Status.CLOSED)
                .collect(Collectors.toList());

        // Calculate average resolution time
        String avgResolutionTime = "0 hours";
        if (!resolvedTickets.isEmpty()) {
            double totalHours = 0;
            for (Ticket ticket : resolvedTickets) {
                totalHours += ChronoUnit.MINUTES.between(
                        ticket.getCreatedDate(), ticket.getUpdatedDate()) / 60.0;
            }
            avgResolutionTime = formatDuration(totalHours / resolvedTickets.size());
        }

        // Customer satisfaction (mock or aggregate for now)
        String customerSatisfaction = String.format("%.1f/5", 4.2); // Example value

        // Response rate (percentage of tickets with first response within SLA)
        int responseSLAMet = 0;
        for (Ticket ticket : recentTickets) {
            List<Comment> comments = commentRepository.findByTicketOrderByCreatedDateAsc(ticket);
            if (!comments.isEmpty()) {
                Comment firstComment = comments.get(0);
                if (ChronoUnit.HOURS.between(ticket.getCreatedDate(), firstComment.getCreatedDate()) <= 4) {
                    responseSLAMet++;
                }
            }
        }
        String responseRate = recentTickets.isEmpty() ? "0%" :
                String.format("%d%%", (responseSLAMet * 100) / recentTickets.size());

        return PerformanceMetricsDTO.StatsData.builder()
                .ticketsAssigned(recentTickets.size())
                .ticketsResolved(resolvedTickets.size())
                .averageResolutionTime(avgResolutionTime)
                .customerSatisfaction(customerSatisfaction)
                .responseRate(responseRate)
                .build();
    }

    // Helper methods for performance metric calculations

    private int calculateResolutionSpeedScore(List<Ticket> resolvedTickets) {
        if (resolvedTickets.isEmpty()) {
            return 50; // Neutral score for no data
        }

        // Calculate average resolution time in hours
        double totalHours = 0;
        for (Ticket ticket : resolvedTickets) {
            totalHours += ChronoUnit.MINUTES.between(
                    ticket.getCreatedDate(), ticket.getUpdatedDate()) / 60.0;
        }
        double avgHours = totalHours / resolvedTickets.size();

        // Score based on average hours (example scoring system)
        // Lower is better: <8 hours = 90-100, 8-24 hours = 70-89, 24-48 hours = 50-69, >48 hours = below 50
        if (avgHours < 4) return 100;
        if (avgHours < 8) return 90;
        if (avgHours < 16) return 80;
        if (avgHours < 24) return 70;
        if (avgHours < 36) return 60;
        if (avgHours < 48) return 50;
        if (avgHours < 72) return 40;
        return 30;
    }

    private int calculateFirstResponseTimeScore(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return 50; // Neutral score for no data
        }

        int ticketsWithResponses = 0;
        double totalResponseHours = 0;

        for (Ticket ticket : tickets) {
            List<Comment> comments = commentRepository.findByTicketOrderByCreatedDateAsc(ticket);
            if (!comments.isEmpty()) {
                Comment firstComment = comments.get(0);
                totalResponseHours += ChronoUnit.MINUTES.between(
                        ticket.getCreatedDate(), firstComment.getCreatedDate()) / 60.0;
                ticketsWithResponses++;
            }
        }

        if (ticketsWithResponses == 0) {
            return 50; // No responses yet
        }

        double avgHours = totalResponseHours / ticketsWithResponses;

        // Score based on average hours (example scoring system)
        // Lower is better: <1 hour = 90-100, 1-4 hours = 70-89, 4-8 hours = 50-69, >8 hours = below 50
        if (avgHours < 0.5) return 100;
        if (avgHours < 1) return 90;
        if (avgHours < 2) return 80;
        if (avgHours < 4) return 70;
        if (avgHours < 6) return 60;
        if (avgHours < 8) return 50;
        if (avgHours < 12) return 40;
        return 30;
    }

    private int calculateTicketsResolvedScore(int resolvedCount) {
        // Score based on number of tickets resolved (example scoring system)
        // Higher is better: >30 = 90-100, 20-30 = 70-89, 10-20 = 50-69, <10 = below 50
        if (resolvedCount >= 40) return 100;
        if (resolvedCount >= 30) return 90;
        if (resolvedCount >= 25) return 80;
        if (resolvedCount >= 20) return 70;
        if (resolvedCount >= 15) return 60;
        if (resolvedCount >= 10) return 50;
        if (resolvedCount >= 5) return 40;
        return 30;
    }

    private int calculateCustomerSatisfactionScore(List<Ticket> resolvedTickets) {
        if (resolvedTickets.isEmpty()) {
            return 50; // Neutral score for no data
        }

        // In a real implementation, you would retrieve customer satisfaction ratings
        // This is a placeholder implementation
        int totalRatings = 0;
        int ticketsWithRatings = 0;

        for (Ticket ticket : resolvedTickets) {
            // Assuming tickets might have a rating field or related rating entity
            // For this example, we'll create a mock rating between 1-5
            if (ticket.getStatus() == Ticket.Status.CLOSED) {
                // Mock a satisfaction rating
                int rating = 3 + (ticket.getId().hashCode() % 3); // Generates 1-5 based on ticket ID
                if (rating < 1) rating = 3; // Ensure a reasonable rating
                if (rating > 5) rating = 5;

                totalRatings += rating;
                ticketsWithRatings++;
            }
        }

        if (ticketsWithRatings == 0) {
            return 50; // No ratings yet
        }

        double avgRating = (double) totalRatings / ticketsWithRatings;

        // Score based on average rating (example scoring system)
        // 5 = 100, 4 = 80, 3 = 60, 2 = 40, 1 = 20
        return (int) (avgRating * 20);
    }

    private int calculateCommunicationQualityScore(User agent) {
        // This could be based on number of comments, updates, etc.
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Comment> comments = commentRepository.findByAuthorAndCreatedDateAfter(agent, thirtyDaysAgo);

        if (comments.isEmpty()) {
            return 50; // Neutral score for no data
        }

        // Calculate average comment length and frequency
        List<Ticket> assignedTickets = ticketRepository.findByAssignedToAndCreatedDateAfter(agent, thirtyDaysAgo);

        // Frequency: comments per ticket
        double commentsPerTicket = assignedTickets.isEmpty() ? 0 :
                (double) comments.size() / assignedTickets.size();

        // Average length (assume comments have a content field)
        int totalLength = 0;
        for (Comment comment : comments) {
            if (comment.getContent() != null) {
                totalLength += comment.getContent().length();
            }
        }
        double avgLength = comments.isEmpty() ? 0 : (double) totalLength / comments.size();

        // Combined score based on frequency and length
        int frequencyScore = calculateFrequencyScore(commentsPerTicket);
        int lengthScore = calculateLengthScore(avgLength);

        return (frequencyScore + lengthScore) / 2;
    }

    private int calculateFrequencyScore(double commentsPerTicket) {
        if (commentsPerTicket >= 5) return 100;
        if (commentsPerTicket >= 4) return 90;
        if (commentsPerTicket >= 3) return 80;
        if (commentsPerTicket >= 2.5) return 70;
        if (commentsPerTicket >= 2) return 60;
        if (commentsPerTicket >= 1.5) return 50;
        if (commentsPerTicket >= 1) return 40;
        return 30;
    }

    private int calculateLengthScore(double avgLength) {
        if (avgLength >= 200) return 100;
        if (avgLength >= 150) return 90;
        if (avgLength >= 120) return 80;
        if (avgLength >= 100) return 70;
        if (avgLength >= 80) return 60;
        if (avgLength >= 60) return 50;
        if (avgLength >= 40) return 40;
        return 30;
    }

    private int calculateTicketQualityScore(List<Ticket> resolvedTickets) {
        if (resolvedTickets.isEmpty()) {
            return 50; // Neutral score for no data
        }

        // Factors that could indicate quality:
        // 1. No reopening of tickets
        // 2. Resolution matches actual issue category
        // 3. Clear documentation in resolution notes

        // For this example, we'll focus on reopening rate
        int reopenedCount = 0;
        for (Ticket ticket : resolvedTickets) {
            // Check if ticket has been reopened (simplified)
            // In a real implementation, you would check ticket history
            if (ticket.getStatus() == Ticket.Status.REOPENED ||
                    (ticket.getStatus() == Ticket.Status.RESOLVED && hasMultipleStatusChanges(ticket))) {
                reopenedCount++;
            }
        }

        double reopenRate = (double) reopenedCount / resolvedTickets.size();

        // Score based on reopen rate (lower is better)
        if (reopenRate <= 0.01) return 100; // Less than 1%
        if (reopenRate <= 0.03) return 90;  // Less than 3%
        if (reopenRate <= 0.05) return 80;  // Less than 5%
        if (reopenRate <= 0.08) return 70;  // Less than 8%
        if (reopenRate <= 0.12) return 60;  // Less than 12%
        if (reopenRate <= 0.15) return 50;  // Less than 15%
        if (reopenRate <= 0.20) return 40;  // Less than 20%
        return 30;                          // 20% or higher
    }

    // Helper method to determine if a ticket has multiple status changes
    private boolean hasMultipleStatusChanges(Ticket ticket) {
        // This implementation depends on your data model
        // Option 1: If you have a history or audit table
        // return ticketHistoryRepository.countStatusChangesByTicketId(ticket.getId()) > 2;

        // Option 2: If you can check comments for status changes
        List<Comment> comments = commentRepository.findByTicketOrderByCreatedDateAsc(ticket);
        int statusChangeComments = 0;
        for (Comment comment : comments) {
            // Assuming comments might have a type or content that indicates status change
            // This is just an example, adjust based on your actual data model
            if (comment.getContent() != null &&
                    (comment.getContent().contains("changed status") ||
                            comment.getContent().contains("status update"))) {
                statusChangeComments++;
            }
        }
        return statusChangeComments > 2;
    }

    private String formatDuration(double hours) {
        if (hours < 1) {
            return String.format("%.0f minutes", hours * 60);
        } else if (hours < 24) {
            return String.format("%.1f hours", hours);
        } else {
            return String.format("%.1f days", hours / 24);
        }
    }
}