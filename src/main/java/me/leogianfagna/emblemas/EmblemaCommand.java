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
            int raridadeFiltro = 0; // padrão, exibe todos

            // Se o argumento de raridade foi fornecido, tenta interpretá-lo como número
            if (args.length > 0) {
                try {
                    raridadeFiltro = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Por favor, insira um número válido para a raridade.");
                    return true;
                }
            }

            // Passa a raridade como argumento para o método openAlbum
            EmblemaGUI.openAlbum(player, emblemaManager.getEmblemas(), plugin, emblemaManager, raridadeFiltro);
            return true;
        }
        return false;
    }

}
