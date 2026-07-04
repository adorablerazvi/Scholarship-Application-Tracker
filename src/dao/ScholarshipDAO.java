package dao;

import model.Scholarship;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScholarshipDAO {

    public boolean addScholarship(Scholarship s) {
        String sql = "INSERT INTO scholarships (name, provider, description, amount, eligibility_criteria, deadline, category) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, s.getName());
            pst.setString(2, s.getProvider());
            pst.setString(3, s.getDescription());
            pst.setDouble(4, s.getAmount());
            pst.setString(5, s.getEligibilityCriteria());
            pst.setDate(6, s.getDeadline());
            pst.setString(7, s.getCategory());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add scholarship error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateScholarship(Scholarship s) {
        String sql = "UPDATE scholarships SET name=?, provider=?, description=?, amount=?, eligibility_criteria=?, deadline=?, category=?, status=? WHERE scholarship_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, s.getName());
            pst.setString(2, s.getProvider());
            pst.setString(3, s.getDescription());
            pst.setDouble(4, s.getAmount());
            pst.setString(5, s.getEligibilityCriteria());
            pst.setDate(6, s.getDeadline());
            pst.setString(7, s.getCategory());
            pst.setString(8, s.getStatus());
            pst.setInt(9, s.getScholarshipId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update scholarship error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteScholarship(int id) {
        String sql = "DELETE FROM scholarships WHERE scholarship_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete scholarship error: " + e.getMessage());
            return false;
        }
    }

    public List<Scholarship> getAllScholarships() {
        List<Scholarship> list = new ArrayList<>();
        String sql = "SELECT * FROM scholarships ORDER BY deadline ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractScholarship(rs));
            }
        } catch (SQLException e) {
            System.err.println("Fetch scholarships error: " + e.getMessage());
        }
        return list;
    }

    public List<Scholarship> getOpenScholarships() {
        List<Scholarship> list = new ArrayList<>();
        String sql = "SELECT * FROM scholarships WHERE status = 'OPEN' AND deadline >= CURDATE() ORDER BY deadline ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractScholarship(rs));
            }
        } catch (SQLException e) {
            System.err.println("Fetch open scholarships error: " + e.getMessage());
        }
        return list;
    }

    public List<Scholarship> searchScholarships(String keyword, String category) {
        List<Scholarship> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM scholarships WHERE status = 'OPEN' AND deadline >= CURDATE()");
        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR provider LIKE ? OR description LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (category != null && !category.equals("All")) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        sql.append(" ORDER BY deadline ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pst.setString(i + 1, params.get(i));
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(extractScholarship(rs));
            }
        } catch (SQLException e) {
            System.err.println("Search error: " + e.getMessage());
        }
        return list;
    }

    public Scholarship getScholarshipById(int id) {
        String sql = "SELECT * FROM scholarships WHERE scholarship_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return extractScholarship(rs);
            }
        } catch (SQLException e) {
            System.err.println("Get scholarship error: " + e.getMessage());
        }
        return null;
    }

    public int getTotalScholarships() {
        String sql = "SELECT COUNT(*) FROM scholarships";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Count error: " + e.getMessage());
        }
        return 0;
    }

    public double getTotalFunding() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM scholarships WHERE status = 'OPEN'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Sum error: " + e.getMessage());
        }
        return 0;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        String sql = "SELECT DISTINCT category FROM scholarships ORDER BY category";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            System.err.println("Categories error: " + e.getMessage());
        }
        return categories;
    }

    private Scholarship extractScholarship(ResultSet rs) throws SQLException {
        Scholarship s = new Scholarship();
        s.setScholarshipId(rs.getInt("scholarship_id"));
        s.setName(rs.getString("name"));
        s.setProvider(rs.getString("provider"));
        s.setDescription(rs.getString("description"));
        s.setAmount(rs.getDouble("amount"));
        s.setEligibilityCriteria(rs.getString("eligibility_criteria"));
        s.setDeadline(rs.getDate("deadline"));
        s.setCategory(rs.getString("category"));
        s.setStatus(rs.getString("status"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}