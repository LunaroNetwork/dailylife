package nl.openminetopia.modules.language;

import java.sql.*;
import java.util.UUID;

public class LanguageDatabase {
    private Connection connection;

    // Vul hier je database gegevens in
    private final String host = "localhost";     // of IP van je DB server
    private final int port = 3306;               // standaard MySQL poort
    private final String database = "lunaro_language";
    private final String username = "lunaro_dev";
    private final String password = "wachtwoord";

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) return;

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
        connection = DriverManager.getConnection(url, username, password);

        // Maak tabel aan als deze nog niet bestaat
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS players (" +
                    "player VARCHAR(36) PRIMARY KEY," +
                    "language VARCHAR(16)" +
                    ");");
        }
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void setLanguage(UUID playerUUID, String language) throws SQLException {
        String sql = "INSERT INTO players (player, language) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE language = VALUES(language)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, language);
            ps.executeUpdate();
        }
    }

    public String getLanguage(UUID playerUUID) throws SQLException {
        String sql = "SELECT language FROM players WHERE player = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUUID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("language");
            }
        }
        return "english"; // standaard taal
    }
}
