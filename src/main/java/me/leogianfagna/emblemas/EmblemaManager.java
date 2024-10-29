package me.leogianfagna.emblemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmblemaManager {

    private final Connection connection;

    public EmblemaManager(Connection connection) {
        this.connection = connection;
    }

    public List<Emblema> getEmblemas() {
        List<Emblema> emblemas = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM emblemas");

            while (rs.next()) {
                Emblema emblema = new Emblema(
                    rs.getString("nome"),
                    rs.getString("categoria"),
                    rs.getInt("custom_conquistado"),
                    rs.getInt("custom_black"),
                    rs.getString("descricao_rapida"),
                    rs.getString("descricao_completa"),
                    rs.getInt("raridade"),
                    rs.getString("data_lancamento"),
                    rs.getString("local_lancamento"),
                    rs.getString("modo_conquista")
                );
                emblemas.add(emblema);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emblemas;
    }
}

