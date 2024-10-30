package me.leogianfagna.emblemas;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class AlbumMenuListener implements Listener {

    private final Inventory album;
    private final FileConfiguration config;
    private final int raridadeFiltro;
    private final int pagina;
    private static final String NEXT_BUTTON_KEY = "next-page";
    private static final String PREVIOUS_BUTTON_KEY = "previous-page";
    private static final String MENU_TESTE_KEY = "back";

    public AlbumMenuListener(Inventory album, FileConfiguration config, int raridadeFiltro, int pagina) {
        this.album = album;
        this.config = config;
        this.raridadeFiltro = raridadeFiltro;
        this.pagina = pagina;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;

        if (event.getClickedInventory().equals(album)) {
            event.setCancelled(true); // Impede movimentação de itens

            Player player = (Player) event.getWhoClicked();
            int slotClicado = event.getSlot();
            String comando = null;

            if (config.contains(NEXT_BUTTON_KEY + "." + slotClicado)) {
                comando = "/album " + raridadeFiltro + " " + (pagina + 1);
            } else if (config.contains(PREVIOUS_BUTTON_KEY + "." + slotClicado)) {
                
                if ((pagina - 1) < 1) {
                    comando = null;
                } else {
                    comando = "/album " + raridadeFiltro + " " + (pagina - 1);
                }

            } else if (config.contains(MENU_TESTE_KEY + "." + slotClicado)) {
                comando = config.getString(MENU_TESTE_KEY + "." + slotClicado);
            }

            if (comando != null && !comando.isEmpty()) {
                player.performCommand(comando.replaceFirst("/", ""));
            }
        }
    }
}
