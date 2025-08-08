package com.icbt.pahanaedubookshopjavaee.dto;

public class SupportRequestDTO {
    private String ticketId;
    private String issueType;
    private String priority;
    private String subject;
    private String description;
    private String userEmail;
    private String userAgent;
    private String timestamp;

    public SupportRequestDTO() {}

    public SupportRequestDTO(String ticketId, String issueType, String priority, 
                           String subject, String description, String userEmail, 
                           String userAgent, String timestamp) {
        this.ticketId = ticketId;
        this.issueType = issueType;
        this.priority = priority;
        this.subject = subject;
        this.description = description;
        this.userEmail = userEmail;
        this.userAgent = userAgent;
        this.timestamp = timestamp;
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}