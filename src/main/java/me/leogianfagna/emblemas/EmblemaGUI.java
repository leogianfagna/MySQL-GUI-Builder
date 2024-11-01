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
import java.util.*;

public class EmblemaGUI {

    private static final String NEXT_BUTTON_KEY = "next-page";
    private static final String PREVIOUS_BUTTON_KEY = "previous-page";
    private static final String MENU_TESTE_KEY = "back";
    private static final String CONFIG_FILE_NAME = "emblemas_gui.yml";
    private static final String CONFIG_FILL_KEY = "fill";
    private static final String CONFIG_TITLE_KEY = "title";
    private static final String CONFIG_SIZE_KEY = "size";
    private static final int DEFAULT_GUI_SIZE = 45;
    private static final int MAX_EMBLEMAS_PER_PAGE = 28;

    /*
     * Cria o álbum de eventos usando as configurações da pasta do plugin e os
     * argumentos do construtor. Implementa todos os itens da GUI e depois registra
     * o listener da classe AlbumMenuListener que impede a movimentação de itens.
     * Cria um Set com os slots fill, que vai ignorar slots.
     */
    public static void openAlbum(Player player, List<Emblema> emblemas, JavaPlugin plugin,
            EmblemaManager emblemaManager, int raridadeFiltro, int pagina) {
        FileConfiguration config = getConfig(plugin);
        Set<Integer> fillSet = new HashSet<>(config.getIntegerList(CONFIG_FILL_KEY));
        String title = config.getString(CONFIG_TITLE_KEY, "Álbum de Emblemas");
        int guiSize = validateGuiSize(config.getInt(CONFIG_SIZE_KEY, DEFAULT_GUI_SIZE));

        Inventory album = Bukkit.createInventory(null, guiSize, title);
        UUID playerUUID = player.getUniqueId();

        configureNavigationButtons(album, config, fillSet);
        fillEmblemasInAlbum(album, emblemas, emblemaManager, fillSet, raridadeFiltro, pagina, playerUUID);
        player.openInventory(album);
        Bukkit.getPluginManager().registerEvents(new AlbumMenuListener(album, config, raridadeFiltro, pagina), plugin);
    }

    private static int validateGuiSize(int guiSize) {
        return (guiSize % 9 == 0) ? guiSize : DEFAULT_GUI_SIZE;
    }

    /*
     * Coloca os itens dos 3 tipos de botões nos respectivos slots. Depois usa a
     * função abaixo para definir nome, tipo, etc. É na classe AlbumMenuLister que
     * cria um listener para o clique desses botões, definindo qual o comando que
     * será executado ao clicar.
     */
    private static void configureNavigationButtons(Inventory album, FileConfiguration config, Set<Integer> fillSet) {
        setupButton(album, config, NEXT_BUTTON_KEY, Material.JIGSAW, "§7Próxima página §a»", fillSet);
        setupButton(album, config, PREVIOUS_BUTTON_KEY, Material.JIGSAW, "§c« §7Página anterior", fillSet);
        setupButton(album, config, MENU_TESTE_KEY, Material.JIGSAW, "§7Voltar", fillSet);
    }

    private static void setupButton(Inventory album, FileConfiguration config, String key, Material material,
            String displayName, Set<Integer> fillSet) {
        Set<String> buttonKeys = config.getConfigurationSection(key).getKeys(false);
        for (String buttonKey : buttonKeys) {
            int slot = Integer.parseInt(buttonKey);
            ItemStack button = new ItemStack(material);
            ItemMeta meta = button.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(displayName);
                button.setItemMeta(meta);
            }
            album.setItem(slot, button);
            fillSet.add(slot);
        }
    }

    /*
     * Emblemas são colocados na GUI baseado na página atual que o jogador está. Vai
     * preenchendo todos e passando de slot. Recebe os slots através do argumento
     * "emblemas" e já recebe os emblemas filtrados pela categoria.
     */
    private static void fillEmblemasInAlbum(Inventory album, List<Emblema> emblemas, EmblemaManager emblemaManager,
            Set<Integer> fillSet, int raridadeFiltro, int pagina, UUID playerUUID) {
        int start = (pagina - 1) * MAX_EMBLEMAS_PER_PAGE;
        int end = Math.min(start + MAX_EMBLEMAS_PER_PAGE, emblemas.size());
        int slot = 0;

        for (int i = start; i < end; i++) {
            Emblema emblema = emblemas.get(i);

            while (fillSet.contains(slot)) {
                slot++;
            }

            album.setItem(slot, createEmblemaItem(emblema, playerUUID, emblemaManager));
            slot++;
        }
    }

    /*
     * Configurações do emblema. Usa a função abaixo createLore para adicionar as
     * lores e todas as informações do emblema
     */
    private static ItemStack createEmblemaItem(Emblema emblema, UUID playerUUID, EmblemaManager emblemaManager) {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a" + emblema.getNome());
            meta.setLore(createLore(emblema, playerUUID, emblemaManager));
            meta.setCustomModelData(
                    emblemaManager.possuiEmblema(playerUUID, emblema.getIdentificador()) ? emblema.getCustomModelData()
                            : emblema.getCustomBlack());
            item.setItemMeta(meta);
        }
        return item;
    }

    private static List<String> createLore(Emblema emblema, UUID playerUUID, EmblemaManager emblemaManager) {
        List<String> lore = new ArrayList<>();
        lore.add(LoreUtils.emblemaRaridade(emblema.getRaridade()));
        lore.add("");
        lore.add("§x§D§B§D§B§7§9" + emblema.getDescricaoRapida());
        lore.add("");
        lore.add("§a» §fLançamento: §7" + emblema.getDataLancamento());
        lore.add("§a» §fExclusividade: §7" + emblema.getLocalLancamento());
        lore.add("§a» §fConquistável: §7" + emblema.getModoConquista());
        lore.add("§a» §fPossuído por: §7" + emblemaManager.contarPossuidoresEmblema(emblema.getIdentificador()));

        if (!emblemaManager.possuiEmblema(playerUUID, emblema.getIdentificador())) {
            lore.add("");
            lore.add("§8" + emblema.getDescricaoCompleta());
        }
        return lore;
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
}
