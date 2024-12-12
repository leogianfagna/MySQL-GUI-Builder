package me.leogianfagna.emblemas;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EntregarEmblemaCommand implements CommandExecutor {

    private final Connection connection;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public EntregarEmblemaCommand(Connection connection) {
        this.connection = connection;
    }

    /*
     * A entrega de emblema adiciona uma linha em uma nova tabela do banco de dados,
     * como se fosse as permissões de um jogador. É feito com nick ao invés do UUID
     * pois não foi encontrado métodos sem ser deprecated ou muito difíceis de fazer
     * para resgatar o UUID de jogadores off-line.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Uso: /entregaremblema <nick> <emblema>");
            return true;
        }

        String nick = args[0];
        String emblemaNome = args[1];

        if (entregarEmblema(nick.toLowerCase(), emblemaNome)) {
            sender.sendMessage("Emblema " + emblemaNome + " entregue para " + nick + " com sucesso!");
        } else {
            sender.sendMessage("O jogador já possui este emblema ou não foi possível entregá-lo.");
        }

        return true;
    }

    /*
     * Comando que insere no banco de dados. Retorna falso se tiver uma exceção do
     * código 1062, que simboliza código de erro para duplicata no MySQL.
     */
    private boolean entregarEmblema(String player, String emblemaId, boolean retry) {
        String insertSQL = "INSERT INTO emblemas_users (player, emblema_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE emblema_id = emblema_id";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, player);
            stmt.setString(2, emblemaId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return false; // Erro de chave duplicada, já existe
            }

            // Executa o comando de novo, pois pode acontecer da conexão estar fechada
            if (!retry) {
                scheduler.schedule(() -> entregarEmblema(player, emblemaId, true), 1, TimeUnit.SECONDS);
            }
        }
        return false;
    }

    public boolean entregarEmblema(String player, String emblemaId) {
        return entregarEmblema(player, emblemaId, false);
    }
}
