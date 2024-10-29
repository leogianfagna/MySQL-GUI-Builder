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
import java.util.UUID;

public class EmblemaGUI {

    private static final String CONFIG_FILE_NAME = "emblemas_gui.yml";
    private static final String CONFIG_FILL_KEY = "fill";
    private static final String CONFIG_TITLE_KEY = "title";
    private static final String CONFIG_SIZE_KEY = "size";
    private static final int DEFAULT_GUI_SIZE = 45;

    public static void openAlbum(Player player, List<Emblema> emblemas, JavaPlugin plugin, EmblemaManager emblemaManager) {
        FileConfiguration config = getConfig(plugin);
        List<Integer> fillSlots = config.getIntegerList(CONFIG_FILL_KEY);
        Set<Integer> fillSet = new HashSet<>(fillSlots); // Usamos um Set para busca rápida

        String unicodeTitle = config.getString(CONFIG_TITLE_KEY, "Álbum de Emblemas");
        int guiSize = config.getInt(CONFIG_SIZE_KEY, DEFAULT_GUI_SIZE);
        
        // Garante que o tamanho do inventário seja um múltiplo de 9 (requisito para inventários no Minecraft)
        if (guiSize % 9 != 0) {
            guiSize = DEFAULT_GUI_SIZE;
        }
        
        Inventory album = Bukkit.createInventory(null, guiSize, unicodeTitle);
        UUID playerUUID = player.getUniqueId();

        int slot = 0;
        for (Emblema emblema : emblemas) {
            // Encontra o próximo slot que não esteja na lista de preenchimento
            while (fillSet.contains(slot)) {
                slot++;
            }

            // Adiciona o emblema ao slot disponível
            album.setItem(slot, createEmblemaItem(emblema, playerUUID, emblemaManager));
            slot++;
        }

        player.openInventory(album);
    }

    private static FileConfiguration getConfig(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    private static void createDefaultConfig(File configFile) {
        try {
            configFile.createNewFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.set(CONFIG_FILL_KEY, Arrays.asList(0, 1, 3, 34, 35, 36, 37));
            config.set(CONFIG_TITLE_KEY, "Álbum de Emblemas");
            config.set(CONFIG_SIZE_KEY, DEFAULT_GUI_SIZE);
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ItemStack createEmblemaItem(Emblema emblema, UUID playerUUID, EmblemaManager emblemaManager) {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
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

            // Verifica se o jogador possui o emblema
            if (emblemaManager.possuiEmblema(playerUUID, emblema.getIdentificador())) {
                meta.setCustomModelData(emblema.getCustomModelData());
            } else {
                meta.setCustomModelData(emblema.getCustomBlack());
            }

            item.setItemMeta(meta);
        }
        return item;
    }
}
