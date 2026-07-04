package model;

import java.sql.Timestamp;

public class Application {
    private int applicationId;
    private int userId;
    private int scholarshipId;
    private Timestamp applicationDate;
    private String status;
    private double gpa;
    private double income;
    private String statementOfPurpose;
    private String documentsPath;
    private String remarks;
    private String userName;
    private String scholarshipName;
    private double scholarshipAmount;

    public Application() {}

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getScholarshipId() { return scholarshipId; }
    public void setScholarshipId(int scholarshipId) { this.scholarshipId = scholarshipId; }

    public Timestamp getApplicationDate() { return applicationDate; }
    public void setApplicationDate(Timestamp applicationDate) { this.applicationDate = applicationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public double getIncome() { return income; }
    public void setIncome(double income) { this.income = income; }

    public String getStatementOfPurpose() { return statementOfPurpose; }
    public void setStatementOfPurpose(String sop) { this.statementOfPurpose = sop; }

    public String getDocumentsPath() { return documentsPath; }
    public void setDocumentsPath(String documentsPath) { this.documentsPath = documentsPath; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getScholarshipName() { return scholarshipName; }
    public void setScholarshipName(String scholarshipName) { this.scholarshipName = scholarshipName; }

    public double getScholarshipAmount() { return scholarshipAmount; }
    public void setScholarshipAmount(double scholarshipAmount) { this.scholarshipAmount = scholarshipAmount; }
}