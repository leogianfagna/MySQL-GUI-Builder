package me.leogianfagna.emblemas;

import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EmblemaPlugin extends JavaPlugin {

    private Connection connection;
    private EmblemaManager emblemaManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (connectToDatabase()) {
            emblemaManager = new EmblemaManager(connection);
            getCommand("album").setExecutor(new EmblemaCommand(emblemaManager, this));
            getCommand("entregaremblema").setExecutor(new EntregarEmblemaCommand(connection));
        } else {
            getLogger().severe("Falha ao conectar ao banco de dados. O plugin será desativado.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean connectToDatabase() {
        String host = getConfig().getString("mysql.host");
        int port = getConfig().getInt("mysql.port");
        String database = getConfig().getString("mysql.database");
        String user = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");

        // Inclui o parâmetro de SSL falso
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";

        try {
            connection = DriverManager.getConnection(url, user, password);
            getLogger().info("Conectado ao banco de dados MySQL com sucesso!");

            String emblemasListTableSql = "CREATE TABLE IF NOT EXISTS emblemas (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100), " +
                    "categoria VARCHAR(20), " +
                    "custom_conquistado INT, " +
                    "custom_black INT, " +
                    "descricao_rapida VARCHAR(255), " +
                    "descricao_completa TEXT, " +
                    "raridade INT, " +
                    "data_lancamento VARCHAR(50), " +
                    "local_lancamento VARCHAR(50), " +
                    "modo_conquista VARCHAR(50) " +
                    ")";

            String donosListTableSql = "CREATE TABLE IF NOT EXISTS user_emblemas (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "player_uuid CHAR(36), " +
                    "emblema_id VARCHAR(50), " +
                    "UNIQUE (player_uuid, emblema_id) " +
                    ")";

            connection.prepareStatement(emblemasListTableSql).executeUpdate();
            connection.prepareStatement(donosListTableSql).executeUpdate();

            return true;
        } catch (SQLException e) {
            getLogger().severe("Falha ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
