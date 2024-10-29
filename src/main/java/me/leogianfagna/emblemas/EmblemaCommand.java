package me.leogianfagna.emblemas;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmblemaCommand implements CommandExecutor {

    private final EmblemaManager emblemaManager;

    public EmblemaCommand(EmblemaManager emblemaManager) {
        this.emblemaManager = emblemaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            EmblemaGUI.openAlbum(player, emblemaManager.getEmblemas());
            return true;
        }
        return false;
    }
}

