package me.leogianfagna.emblemas;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class EmblemaGUI {

    public static void openAlbum(Player player, List<Emblema> emblemas) {
        Inventory album = Bukkit.createInventory(null, 54, "Álbum de Emblemas");
        for (Emblema emblema : emblemas) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(emblema.getNome());
                meta.setLore(Arrays.asList(
                        "Categoria: " + emblema.getCategoria(),
                        "Raridade: " + emblema.getRaridade(),
                        "Data de Lançamento: " + emblema.getDataLancamento(),
                        "Modo de Conquista: " + emblema.getModoConquista()));
                item.setItemMeta(meta);
            }
            album.addItem(item);
        }
        player.openInventory(album);
    }
}
