package me.leogianfagna.emblemas;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddEmblemaCommand implements CommandExecutor {

    private final Connection connection;

    public AddEmblemaCommand(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por um jogador.");
            return true;
        }

        if (args.length < 10) {
            sender.sendMessage(
                    "Uso incorreto! Use: /addemblema <nome> <categoria> <idConquistado> <idNaoConquistado> <descrição rápida> <descrição completa> <raridade> <data de lançamento> <local de lançamento> <modo de conquista>");
            return true;
        }

        // Parseando os argumentos
        String nome = args[0];
        String categoria = args[1];
        int idConquistado;
        int idNaoConquistado;
        try {
            idConquistado = Integer.parseInt(args[2]);
            idNaoConquistado = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("IDs devem ser números inteiros.");
            return true;
        }
        String descricaoRapida = args[4];
        String descricaoCompleta = args[5];
        String raridade = args[6];
        String dataLancamento = args[7];
        String localLancamento = args[8];
        String modoConquista = args[9];

        // Inserindo no banco de dados
        String sql = "INSERT INTO emblemas (nome, categoria, custom_conquistado, custom_black, descricao_rapida, descricao_completa, raridade, data_lancamento, local_lancamento, modo_conquista) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, categoria);
            stmt.setInt(3, idConquistado);
            stmt.setInt(4, idNaoConquistado);
            stmt.setString(5, descricaoRapida);
            stmt.setString(6, descricaoCompleta);
            stmt.setString(7, raridade);
            stmt.setString(8, dataLancamento);
            stmt.setString(9, localLancamento);
            stmt.setString(10, modoConquista);
            stmt.executeUpdate();
            sender.sendMessage("Emblema adicionado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("Erro ao adicionar o emblema. Verifique o console para mais detalhes.");
        }

        return true;
    }
}
