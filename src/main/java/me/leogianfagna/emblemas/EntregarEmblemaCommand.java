package me.leogianfagna.emblemas;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class EntregarEmblemaCommand implements CommandExecutor {

    private final Connection connection;

    public EntregarEmblemaCommand(Connection connection) {
        this.connection = connection;
    }

    /*
     * A entrega de emblema adiciona uma linha em uma nova tabela do banco de dados,
     * como se fosse as permissões de um jogador. É feito com UUID para seguir um
     * padrão melhor de qualidade, caso o jogador troque de nick.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Uso: /entregaremblema <nick> <emblema>");
            return true;
        }

        String nick = args[0];
        String emblemaNome = args[1];

        // Busca o jogador pelo nick
        Player target = Bukkit.getPlayer(nick);
        if (target == null) {
            sender.sendMessage("Jogador não encontrado ou offline.");
            return true;
        }

        UUID playerUUID = target.getUniqueId();

        if (entregarEmblema(playerUUID, emblemaNome)) {
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
    private boolean entregarEmblema(UUID playerUUID, String emblemaId) {
        String insertSQL = "INSERT INTO user_emblemas (player_uuid, emblema_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE emblema_id = emblema_id";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, emblemaId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return false;
            }
            e.printStackTrace();
        }
        return false;
    }
}
