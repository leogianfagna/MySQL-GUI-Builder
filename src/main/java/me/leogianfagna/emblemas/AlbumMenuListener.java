package me.leogianfagna.emblemas;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AlbumMenuListener implements Listener {

    private final Inventory album;
    private final FileConfiguration config;
    private static final String NEXT_BUTTON_KEY = "next-page";
    private static final String PREVIOUS_BUTTON_KEY = "previous-page";
    private static final String MENU_TESTE_KEY = "back";

    public AlbumMenuListener(Inventory album, FileConfiguration config) {
        this.album = album;
        this.config = config;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;

        if (event.getClickedInventory().equals(album)) {
            event.setCancelled(true); // Impede movimentação de itens

            Player player = (Player) event.getWhoClicked();
            int slotClicado = event.getSlot();
            String comando = null;

            // Verifica o comando no slot clicado em ambas as seções de configuração
            if (config.contains(NEXT_BUTTON_KEY + "." + slotClicado)) {
                comando = config.getString(NEXT_BUTTON_KEY + "." + slotClicado);
            } else if (config.contains(PREVIOUS_BUTTON_KEY + "." + slotClicado)) {
                comando = config.getString(PREVIOUS_BUTTON_KEY + "." + slotClicado);
            } else if (config.contains(MENU_TESTE_KEY + "." + slotClicado)) {
                comando = config.getString(MENU_TESTE_KEY + "." + slotClicado);
            }

            // Executa o comando se encontrado
            if (comando != null && !comando.isEmpty()) {
                player.closeInventory();
                player.performCommand(comando.replaceFirst("/", ""));
                player.sendMessage("Executando comando: " + comando); // Feedback para o jogador
            } else {
                System.out.println("Nenhum comando configurado para o slot: " + slotClicado);
            }
        }
    }
}
