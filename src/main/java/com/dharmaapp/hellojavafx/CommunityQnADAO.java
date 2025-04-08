package com.dharmaapp.hellojavafx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommunityQnADAO {
    private final String url = "jdbc:mysql://localhost:3306/abhishree";
    private final String dbUsername = "root";      // Replace with your actual username
    private final String dbPassword = "GosAbhi007#";      // Replace with your actual password

    // Post a new question
    public void postQuestion(String username, String question) {
        String sql = "INSERT INTO community_qna (username, question) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, question);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Post an answer for an existing question
    public void postAnswer(int questionId, String answer) {
        String sql = "UPDATE community_qna SET answer = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, answer);
            pstmt.setInt(2, questionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve all QnA entries
    public List<QnAEntry> getAllEntries() {
        List<QnAEntry> list = new ArrayList<>();
        String sql = "SELECT id, username, question, answer, timestamp FROM community_qna ORDER BY timestamp DESC";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                QnAEntry entry = new QnAEntry(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("question"),
                        rs.getString("answer"),
                        rs.getTimestamp("timestamp")
                );
                list.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

