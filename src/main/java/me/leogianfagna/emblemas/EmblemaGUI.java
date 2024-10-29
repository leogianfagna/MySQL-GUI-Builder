package me.leogianfagna.emblemas;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmblemaGUI {

    public static void openAlbum(Player player, List<Emblema> emblemas, JavaPlugin plugin) {
        // Carrega o arquivo de configuração emblemas_gui.yml
        File configFile = new File(plugin.getDataFolder(), "emblemas_gui.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                config.set("fill", Arrays.asList(0, 1, 3, 34, 35, 36, 37));
                config.set("title", "Álbum de Emblemas");
                config.set("size", "45");
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Carrega a configuração do arquivo
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        List<Integer> fillSlots = config.getIntegerList("fill");
        Set<Integer> fillSet = new HashSet<>(fillSlots); // Usamos um Set para busca rápida

        // Cria o inventário do álbum
        String unicodeTitle = config.getString("title");
        int guiSize = config.getInt("size");
        Inventory album = Bukkit.createInventory(null, guiSize, unicodeTitle);

        int slot = 0; // Controle do slot em que o item será colocado
        for (Emblema emblema : emblemas) {
            // Encontra o próximo slot que não esteja na lista de preenchimento
            while (fillSet.contains(slot)) {
                slot++;
            }

            // Cria o item do emblema
            ItemStack item = new ItemStack(Material.DIAMOND);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(emblema.getCustomModelData());
                meta.setDisplayName("§a" + emblema.getNome());
                meta.setLore(Arrays.asList(
                        LoreUtils.emblemaRaridade(emblema.getRaridade()),
                        "",
                        "§x§D§B§D§B§7§9" + emblema.getDescricaoRapida(),
                        "",
                        "§a» §fLançamento: §7" + emblema.getDataLancamento(),
                        "§a» §fExclusividade: §7" + emblema.getLocalLancamento(),
                        "§a» §fConquistável: §7" + emblema.getModoConquista(),
                        "§a» §fPossuído por: §7200"));
                item.setItemMeta(meta);
            }

            // Coloca o item no próximo slot disponível
            album.setItem(slot, item);
            slot++; // Avança para o próximo slot
        }

        // Abre o inventário para o jogador
        player.openInventory(album);
    }
}
