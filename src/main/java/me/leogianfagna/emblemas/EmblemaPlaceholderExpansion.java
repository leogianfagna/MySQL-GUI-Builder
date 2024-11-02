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

    private final ConcurrentHashMap<String, Integer> cache = new ConcurrentHashMap<>();
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
     * Cria placeholders. Os que possuem buscas no banco de dados usam cache e
     * tarefas assíncronas para não pesar em nada no desempenho do servidor. Usa
     * cacheKey para cria uma variável de cache diferente para cada, pois não pode
     * usar a mesma. Limpa o cache usando cache.remove().
     * 
     * Placeholders:
     * - %austvemblemas_qtd_<num>% => Quantia de emblemas por categoria
     * - %austvemblemas_qtdp_<num>% => Quantia de emblemas do jogador por categoria.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String cacheKey;

        if (identifier.startsWith("qtd_")) {
            try {
                int raridade = Integer.parseInt(identifier.substring("qtd_".length()));
                cacheKey = "qtd_" + raridade; 

                Integer cachedCount = cache.get(cacheKey);
                if (cachedCount != null) {
                    return String.valueOf(cachedCount);
                } else {
                    cache.put(cacheKey, -1);
                    executor.submit(() -> {
                        int count = getEmblemaCountByRaridade(raridade);
                        cache.put(cacheKey, count);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> cache.remove(cacheKey), 600L);
                    });
                    return "...";
                }
            } catch (NumberFormatException e) {
                return "Erro: raridade inválida.";
            }
        }

        if (identifier.startsWith("qtdp_")) {
            try {
                int raridade = Integer.parseInt(identifier.substring("qtdp_".length()));
                cacheKey = "qtdp_" + player.getName() + "_" + raridade;

                Integer cachedCount = cache.get(cacheKey);
                if (cachedCount != null) {
                    return String.valueOf(cachedCount);
                } else {
                    cache.put(cacheKey, -1);
                    executor.submit(() -> {
                        int count = getPlayerCountByRaridade(player.getName(), raridade);
                        cache.put(cacheKey, count);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> cache.remove(cacheKey), 600L);
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
        String query = "SELECT COUNT(*) FROM emblemas_list WHERE raridade = ?";

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

    private int getPlayerCountByRaridade(String player, int raridade) {
        int count = 0;
        String query = "SELECT COUNT(*) AS quantidade_emblemas FROM emblemas_users eu JOIN emblemas_list el ON eu.emblema_id = el.identificador WHERE eu.player = ? AND el.raridade = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player);
            statement.setInt(2, raridade);
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
