package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Scholarship {
    private int scholarshipId;
    private String name;
    private String provider;
    private String description;
    private double amount;
    private String eligibilityCriteria;
    private Date deadline;
    private String category;
    private String status;
    private Timestamp createdAt;

    public Scholarship() {}

    public Scholarship(String name, String provider, String description, double amount,
                       String eligibilityCriteria, Date deadline, String category) {
        this.name = name;
        this.provider = provider;
        this.description = description;
        this.amount = amount;
        this.eligibilityCriteria = eligibilityCriteria;
        this.deadline = deadline;
        this.category = category;
        this.status = "OPEN";
    }

    public int getScholarshipId() { return scholarshipId; }
    public void setScholarshipId(int scholarshipId) { this.scholarshipId = scholarshipId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getEligibilityCriteria() { return eligibilityCriteria; }
    public void setEligibilityCriteria(String eligibilityCriteria) { this.eligibilityCriteria = eligibilityCriteria; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " - $" + amount;
    }
}