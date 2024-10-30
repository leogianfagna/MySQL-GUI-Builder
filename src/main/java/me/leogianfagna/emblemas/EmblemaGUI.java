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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EmblemaGUI {

    private static final String NEXT_BUTTON_KEY = "next-page";
    private static final String PREVIOUS_BUTTON_KEY = "previous-page";
    private static final String MENU_TESTE_KEY = "back";
    private static final String CONFIG_FILE_NAME = "emblemas_gui.yml";
    private static final String CONFIG_FILL_KEY = "fill";
    private static final String CONFIG_TITLE_KEY = "title";
    private static final String CONFIG_SIZE_KEY = "size";
    private static final int DEFAULT_GUI_SIZE = 45;

    public static void openAlbum(Player player, List<Emblema> emblemas, JavaPlugin plugin,
            EmblemaManager emblemaManager, int raridadeFiltro, int pagina) {
        FileConfiguration config = getConfig(plugin);
        List<Integer> fillSlots = config.getIntegerList(CONFIG_FILL_KEY);
        Set<Integer> fillSet = new HashSet<>(fillSlots);

        String unicodeTitle = config.getString(CONFIG_TITLE_KEY, "Álbum de Emblemas");
        int guiSize = config.getInt(CONFIG_SIZE_KEY, DEFAULT_GUI_SIZE);

        if (guiSize % 9 != 0) {
            guiSize = DEFAULT_GUI_SIZE;
        }

        Inventory album = Bukkit.createInventory(null, guiSize, unicodeTitle);
        UUID playerUUID = player.getUniqueId();

        // Configura os botões
        Set<String> buttonKeys = config.getConfigurationSection(NEXT_BUTTON_KEY).getKeys(false);
        for (String key : buttonKeys) {
            int slotBotao = Integer.parseInt(key);

            ItemStack itemBotao = new ItemStack(Material.JIGSAW);
            ItemMeta meta = itemBotao.getItemMeta();
            meta.setDisplayName("§7Próxima página §a»");
            itemBotao.setItemMeta(meta);

            album.setItem(slotBotao, itemBotao);
            fillSet.add(slotBotao);
        }

        Set<String> buttonKeys2 = config.getConfigurationSection(PREVIOUS_BUTTON_KEY).getKeys(false);
        for (String key : buttonKeys2) {
            int slotBotao = Integer.parseInt(key);

            ItemStack itemBotao = new ItemStack(Material.JIGSAW);
            ItemMeta meta = itemBotao.getItemMeta();
            meta.setDisplayName("§c« §7Página anterior");
            itemBotao.setItemMeta(meta);

            album.setItem(slotBotao, itemBotao);
            fillSet.add(slotBotao);
        }

        Set<String> buttonKeys3 = config.getConfigurationSection(MENU_TESTE_KEY).getKeys(false);
        for (String key : buttonKeys3) {
            int slotBotao = Integer.parseInt(key);

            ItemStack itemBotao = new ItemStack(Material.JIGSAW);
            ItemMeta meta = itemBotao.getItemMeta();
            meta.setDisplayName("§7Voltar");
            itemBotao.setItemMeta(meta);

            album.setItem(slotBotao, itemBotao);
            fillSet.add(slotBotao);
        }

        // Preenche os emblemas ignorando os slots de botões
        int slot = 0;
        int paginaAtual = pagina;
        int maxEmblemasPag = 28;

        int primeiroElemento = (paginaAtual - 1) * maxEmblemasPag;
        int ultimoElemento = Math.min(primeiroElemento + maxEmblemasPag, emblemas.size());

        for (int contador = primeiroElemento; contador < ultimoElemento; contador++) {
            Emblema emblema = emblemas.get(contador);

            // Filtra por raridade, se necessário
            if (raridadeFiltro > 0 && emblema.getRaridade() != raridadeFiltro) {
                continue;
            }

            // Ignora slots reservados em fillSet
            while (fillSet.contains(slot)) {
                slot++;
            }

            album.setItem(slot, createEmblemaItem(emblema, playerUUID, emblemaManager));
            slot++;
        }

        player.openInventory(album);

        // Registra o listener para clique nos botões
        Bukkit.getPluginManager().registerEvents(new AlbumMenuListener(album, config, raridadeFiltro, pagina), plugin);
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

            // Cria uma lista para a lore
            List<String> lore = new ArrayList<>();
            lore.add(LoreUtils.emblemaRaridade(emblema.getRaridade()));
            lore.add("");
            lore.add("§x§D§B§D§B§7§9" + emblema.getDescricaoRapida());
            lore.add("");
            lore.add("§a» §fLançamento: §7" + emblema.getDataLancamento());
            lore.add("§a» §fExclusividade: §7" + emblema.getLocalLancamento());
            lore.add("§a» §fConquistável: §7" + emblema.getModoConquista());
            lore.add("§a» §fPossuído por: §7" + emblemaManager.contarPossuidoresEmblema(emblema.getIdentificador()));

            // Condições de pertencimento
            if (emblemaManager.possuiEmblema(playerUUID, emblema.getIdentificador())) {
                meta.setCustomModelData(emblema.getCustomModelData());
            } else {
                lore.add("");
                lore.add("§8" + emblema.getDescricaoCompleta());
                meta.setCustomModelData(emblema.getCustomBlack());
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static void fillMenuButtons(List<Integer> menuSlots) {
    }

}
