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
            album.addItem(item);
        }
        player.openInventory(album);
    }
}
