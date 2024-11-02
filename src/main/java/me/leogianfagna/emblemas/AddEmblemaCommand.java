package me.leogianfagna.emblemas;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

    /*
     * Vai receber todos os argumentos do comando /addemblema e tentar inserir no
     * banco baseado nisso. Fica tudo em um try-catch
     * pois como é um plugin pessoal, não precisa ficar tratando cada exceção e só
     * manda uma imagem final.
     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            List<String> argumentos = parseArguments(args);
            inserirEmblemaNoBanco(argumentos, sender);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("Erro ao adicionar o emblema. Confira a documentação do comando coretamente.");
            sender.sendMessage("Detalhes do erro: " + e.getMessage());
        }

        return true;
    }

    // Possibilitar colocar argumentos entre aspas, pois descrições ou nome dos
    // emblemas exigem espaços e isso causaria a leitura de argumentos a mais
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
        int raridade = Integer.parseInt(argumentos.get(2));
        int idConquistado = Integer.parseInt(argumentos.get(3));
        int idNaoConquistado = Integer.parseInt(argumentos.get(4));
        String descricaoRapida = argumentos.get(5);
        String descricaoCompleta = argumentos.get(6);
        String dataLancamento = argumentos.get(7);
        String localLancamento = argumentos.get(8);
        String modoConquista = argumentos.get(9);

        String sql = "INSERT INTO emblemas_list (nome, identificador, raridade, custom_conquistado, custom_black, descricao_rapida, descricao_completa, data_lancamento, local_lancamento, modo_conquista) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, identificador);
            stmt.setInt(3, raridade);
            stmt.setInt(4, idConquistado);
            stmt.setInt(5, idNaoConquistado);
            stmt.setString(6, descricaoRapida);
            stmt.setString(7, descricaoCompleta);
            stmt.setString(8, dataLancamento);
            stmt.setString(9, localLancamento);
            stmt.setString(10, modoConquista);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                sender.sendMessage("Emblema adicionado com sucesso!");
            } else {
                sender.sendMessage("A inserção falhou, nenhuma linha foi afetada.");
            }
        }
    }
}
