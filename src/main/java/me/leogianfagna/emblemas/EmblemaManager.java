package me.leogianfagna.emblemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmblemaManager {

    private final Connection connection;

    public EmblemaManager(Connection connection) {
        this.connection = connection;
    }

    public List<Emblema> getEmblemas(int categoria) {
        List<Emblema> emblemas = new ArrayList<>();
        String query = "SELECT * FROM emblemas WHERE raridade = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoria); // Define o parâmetro "categoria"
            
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Emblema emblema = new Emblema(
                    rs.getString("nome"),
                    rs.getInt("raridade"),
                    rs.getInt("custom_conquistado"),
                    rs.getInt("custom_black"),
                    rs.getString("descricao_rapida"),
                    rs.getString("descricao_completa"),
                    rs.getString("data_lancamento"),
                    rs.getString("local_lancamento"),
                    rs.getString("modo_conquista"),
                    rs.getString("identificador")
                );
                emblemas.add(emblema);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emblemas;
    }
    

    public boolean possuiEmblema(UUID playerUUID, String emblemaId) {
        String query = "SELECT 1 FROM user_emblemas WHERE player_uuid = ? AND emblema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, emblemaId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retorna true se houver um resultado, indicando que o emblema existe para o jogador
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int contarPossuidoresEmblema(String emblemaId) {
        String query = "SELECT COUNT(*) FROM user_emblemas WHERE emblema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, emblemaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Retorna a contagem
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Retorna 0 se houver algum problema
    }
}

