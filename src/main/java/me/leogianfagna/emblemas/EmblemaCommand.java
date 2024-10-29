package me.leogianfagna.emblemas;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EmblemaCommand implements CommandExecutor {

    private final EmblemaManager emblemaManager;
    private final JavaPlugin plugin;

    public EmblemaCommand(EmblemaManager emblemaManager, JavaPlugin plugin) {
        this.emblemaManager = emblemaManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            EmblemaGUI.openAlbum(player, emblemaManager.getEmblemas(), plugin);
            return true;
        }
        return false;
    }
}

