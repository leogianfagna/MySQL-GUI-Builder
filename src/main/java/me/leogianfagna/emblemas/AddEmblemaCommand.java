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

        // Verifica se a quantidade mínima de argumentos foi fornecida
        if (args.length < 10) {
            sender.sendMessage(
                    "Uso incorreto pois está com menos de 10 argumentos! Use: /addemblema <nome> <identificador> <categoria> <idConquistado> <idNaoConquistado> <\"descrição rápida\"> <\"descrição completa\"> <raridade> <data de lançamento> <local de lançamento> <modo de conquista>");
            return true;
        }

        // Combina todos os argumentos em uma única string para facilitar a análise
        String input = String.join(" ", args);
        List<String> argumentos = new ArrayList<>();

        // Expressão regular para capturar texto entre aspas ou palavras isoladas
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(input);
        while (matcher.find() && argumentos.size() < 11) {
            if (matcher.group(1) != null) {
                argumentos.add(matcher.group(1)); // Argumento entre aspas
            } else {
                argumentos.add(matcher.group(2)); // Argumento sem aspas
            }
        }

        // Verifica se todos os argumentos necessários foram fornecidos após o parse
        if (argumentos.size() < 11) {
            sender.sendMessage(
                    "Uso incorreto pois entrou no outro if que não sei o que significa! Use: /addemblema <nome> <identificador> <categoria> <idConquistado> <idNaoConquistado> <\"descrição rápida\"> <\"descrição completa\"> <raridade> <data de lançamento> <local de lançamento> <modo de conquista>");
            return true;
        }

        // Parseando os argumentos
        String nome = argumentos.get(0);
        String identificador = argumentos.get(1);
        String categoria = argumentos.get(2);
        int idConquistado;
        int idNaoConquistado;
        try {
            idConquistado = Integer.parseInt(argumentos.get(3));
            idNaoConquistado = Integer.parseInt(argumentos.get(4));
        } catch (NumberFormatException e) {
            sender.sendMessage("IDs devem ser números inteiros.");
            return true;
        }
        String descricaoRapida = argumentos.get(5);
        String descricaoCompleta = argumentos.get(6);
        String raridade = argumentos.get(7);
        String dataLancamento = argumentos.get(8);
        String localLancamento = argumentos.get(9);
        String modoConquista = argumentos.get(10);

        sender.sendMessage("Argumentos processados com sucesso. Iniciando inserção no banco de dados.");

        // Inserindo no banco de dados
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
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("Erro ao adicionar o emblema. Detalhes do erro: " + e.getMessage());
        }

        sender.sendMessage("Finalizando execução do comando.");
        return true;
    }

}
