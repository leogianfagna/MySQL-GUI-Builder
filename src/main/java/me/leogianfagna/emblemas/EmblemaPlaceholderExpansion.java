package me.leogianfagna.emblemas;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmblemaPlaceholderExpansion extends PlaceholderExpansion {

    private final ConcurrentHashMap<Integer, Integer> cache = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Connection connection;
    private final EmblemaPlugin plugin;

    public EmblemaPlaceholderExpansion(Connection connection, EmblemaPlugin plugin) {
        this.connection = connection;
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "austvemblemas";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /*
     * Cria o placeholder %austvemblemas_qtd_<num>%, num sendo a raridade de 1-6 e
     * conta quantos emblemas possuem naquela categoria. Usa cache e tarefa
     * assíncrona para não pesar em nada no desempenho do servidor.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.startsWith("qtd_")) {
            try {
                int raridade = Integer.parseInt(identifier.substring("qnt_".length()));

                // Verifica o cache primeiro
                Integer cachedCount = cache.get(raridade);
                if (cachedCount != null) {
                    return String.valueOf(cachedCount);
                } else {
                    cache.put(raridade, -1); // Indicador de carregamento
                    executor.submit(() -> {
                        int count = getEmblemaCountByRaridade(raridade);
                        cache.put(raridade, count);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> cache.remove(raridade), 600L); // Limpa o cache
                    });
                    return "...";
                }
            } catch (NumberFormatException e) {
                return "Erro: raridade inválida.";
            }
        }
        return null;
    }

    private int getEmblemaCountByRaridade(int raridade) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM emblemas WHERE raridade = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, raridade);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) { // Move para a primeira linha, se existir
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

}
