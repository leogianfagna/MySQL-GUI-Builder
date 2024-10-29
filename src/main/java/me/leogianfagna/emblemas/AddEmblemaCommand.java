package me.leogianfagna.emblemas;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            sender.sendMessage("Uso incorreto! Use: /addemblema <nome> <identificador> <categoria> <idConquistado> <idNaoConquistado> <\"descrição rápida\"> <\"descrição completa\"> <raridade> <data de lançamento> <local de lançamento> <modo de conquista>");
            return true;
        }

        List<String> argumentos = parseArguments(args);
        if (argumentos.size() < 11) {
            sender.sendMessage("Uso incorreto! Verifique os argumentos fornecidos.");
            return true;
        }

        try {
            inserirEmblemaNoBanco(argumentos, sender);
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("Erro ao adicionar o emblema. Detalhes do erro: " + e.getMessage());
        }

        return true;
    }

    // Possibilitar colocar argumentos entre aspas, pois descrições ou nome dos emblemas exigem espaços e isso causaria a leitura de argumentos a mais
    private List<String> parseArguments(String[] args) {
        String input = String.join(" ", args);
        List<String> argumentos = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(input);

        while (matcher.find() && argumentos.size() < 11) {
            if (matcher.group(1) != null) {
                argumentos.add(matcher.group(1)); // Argumento entre aspas
            } else {
                argumentos.add(matcher.group(2)); // Argumento sem aspas
            }
        }

        return argumentos;
    }

    private void inserirEmblemaNoBanco(List<String> argumentos, CommandSender sender) throws SQLException {
        String nome = argumentos.get(0);
        String identificador = argumentos.get(1);
        String categoria = argumentos.get(2);
        int idConquistado = parseInt(argumentos.get(3), sender);
        int idNaoConquistado = parseInt(argumentos.get(4), sender);
        String descricaoRapida = argumentos.get(5);
        String descricaoCompleta = argumentos.get(6);
        String raridade = argumentos.get(7);
        String dataLancamento = argumentos.get(8);
        String localLancamento = argumentos.get(9);
        String modoConquista = argumentos.get(10);

        String sql = "INSERT INTO emblemas (nome, identificador, categoria, custom_conquistado, custom_black, descricao_rapida, descricao_completa, raridade, data_lancamento, local_lancamento, modo_conquista) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, identificador);
            stmt.setString(3, categoria);
            stmt.setInt(4, idConquistado);
            stmt.setInt(5, idNaoConquistado);
            stmt.setString(6, descricaoRapida);
            stmt.setString(7, descricaoCompleta);
            stmt.setString(8, raridade);
            stmt.setString(9, dataLancamento);
            stmt.setString(10, localLancamento);
            stmt.setString(11, modoConquista);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                sender.sendMessage("Emblema adicionado com sucesso!");
            } else {
                sender.sendMessage("A inserção falhou, nenhuma linha foi afetada.");
            }
        }
    }

    private int parseInt(String input, CommandSender sender) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            sender.sendMessage("IDs devem ser números inteiros.");
            return 0;
        }
    }
}
