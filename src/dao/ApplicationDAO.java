package dao;

import model.Application;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    public boolean applyForScholarship(Application app) {
        String sql = "INSERT INTO applications (user_id, scholarship_id, gpa, income, statement_of_purpose, documents_path) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, app.getUserId());
            pst.setInt(2, app.getScholarshipId());
            pst.setDouble(3, app.getGpa());
            pst.setDouble(4, app.getIncome());
            pst.setString(5, app.getStatementOfPurpose());
            pst.setString(6, app.getDocumentsPath());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.err.println("Already applied for this scholarship!");
            } else {
                System.err.println("Apply error: " + e.getMessage());
            }
            return false;
        }
    }

    public boolean hasAlreadyApplied(int userId, int scholarshipId) {
        String sql = "SELECT COUNT(*) FROM applications WHERE user_id = ? AND scholarship_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, scholarshipId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Check error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateApplicationStatus(int applicationId, String status, String remarks) {
        String sql = "UPDATE applications SET status = ?, remarks = ? WHERE application_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setString(2, remarks);
            pst.setInt(3, applicationId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update status error: " + e.getMessage());
            return false;
        }
    }

    public List<Application> getApplicationsByUser(int userId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, s.name AS scholarship_name, s.amount AS scholarship_amount " +
                     "FROM applications a JOIN scholarships s ON a.scholarship_id = s.scholarship_id " +
                     "WHERE a.user_id = ? ORDER BY a.application_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(extractApplication(rs));
            }
        } catch (SQLException e) {
            System.err.println("Fetch user applications error: " + e.getMessage());
        }
        return list;
    }

    public List<Application> getAllApplications() {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name AS user_name, s.name AS scholarship_name, s.amount AS scholarship_amount " +
                     "FROM applications a " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "JOIN scholarships s ON a.scholarship_id = s.scholarship_id " +
                     "ORDER BY a.application_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Application app = extractApplication(rs);
                app.setUserName(rs.getString("user_name"));
                list.add(app);
            }
        } catch (SQLException e) {
            System.err.println("Fetch all applications error: " + e.getMessage());
        }
        return list;
    }

    public List<Application> filterApplications(String status) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name AS user_name, s.name AS scholarship_name, s.amount AS scholarship_amount " +
                     "FROM applications a " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "JOIN scholarships s ON a.scholarship_id = s.scholarship_id " +
                     "WHERE a.status = ? ORDER BY a.application_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Application app = extractApplication(rs);
                app.setUserName(rs.getString("user_name"));
                list.add(app);
            }
        } catch (SQLException e) {
            System.err.println("Filter error: " + e.getMessage());
        }
        return list;
    }

    public int getTotalApplications() {
        String sql = "SELECT COUNT(*) FROM applications";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Count error: " + e.getMessage());
        }
        return 0;
    }

    public int getCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM applications WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Count by status error: " + e.getMessage());
        }
        return 0;
    }

    public boolean deleteApplication(int applicationId) {
        String sql = "DELETE FROM applications WHERE application_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, applicationId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete error: " + e.getMessage());
            return false;
        }
    }

    private Application extractApplication(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setApplicationId(rs.getInt("application_id"));
        app.setUserId(rs.getInt("user_id"));
        app.setScholarshipId(rs.getInt("scholarship_id"));
        app.setApplicationDate(rs.getTimestamp("application_date"));
        app.setStatus(rs.getString("status"));
        app.setGpa(rs.getDouble("gpa"));
        app.setIncome(rs.getDouble("income"));
        app.setStatementOfPurpose(rs.getString("statement_of_purpose"));
        app.setDocumentsPath(rs.getString("documents_path"));
        app.setRemarks(rs.getString("remarks"));
        app.setScholarshipName(rs.getString("scholarship_name"));
        app.setScholarshipAmount(rs.getDouble("scholarship_amount"));
        return app;
    }
}