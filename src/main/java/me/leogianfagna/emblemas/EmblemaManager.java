package me.leogianfagna.emblemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmblemaManager {

    private final Connection connection;

    public EmblemaManager(Connection connection) {
        this.connection = connection;
    }

    /*
     * Faz a query no banco de dados, com condição WHERE da categoria do emblema.
     * Pega todas as informações de cada linha pois serão usadas para criar uma nova
     * instância do tipo Emblema, que será usada para construir cada emblema do
     * álbum. Faz retornar um CompletableFuture para fazer de forma assíncrona, já
     * que esse método está conectado com onCommand na hora de usar /album, que
     * também é uma função assíncrona.
     */
    public CompletableFuture<List<Emblema>> getEmblemasAsync(int categoria) {
        return CompletableFuture.supplyAsync(() -> {
            List<Emblema> emblemas = new ArrayList<>();
            String query = "SELECT * FROM emblemas WHERE raridade = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, categoria);
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
                            rs.getString("identificador"));
                    emblemas.add(emblema);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return emblemas;
        });
    }

    /*
     * Importante para mudar o CustomModelData do emblema para cada jogador, assim
     * como acrescentar a descrição completa.
     */
    public boolean possuiEmblema(String player, String emblemaId) {
        String query = "SELECT 1 FROM user_emblemas WHERE player = ? AND emblema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, player);
            stmt.setString(2, emblemaId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retorna true se houver um resultado
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * Essa contagem é inserida logo na lore do emblema, o que anteriormente era
     * feito por um placeholder externo.
     */
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
        return 0;
    }
}
