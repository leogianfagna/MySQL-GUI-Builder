package me.leogianfagna.emblemas;

import org.bukkit.Bukkit;
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
            int raridadeFiltro = 0;
            int pagina = 1;

            // Processar argumentos
            if (args.length > 0) {
                try {
                    raridadeFiltro = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Por favor, insira um número válido para a raridade.");
                    return true;
                }
            }
            if (args.length > 1) {
                try {
                    pagina = Integer.parseInt(args[1]);
                    if (pagina < 1) {
                        player.sendMessage("O número da página deve ser maior ou igual a 1.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("Por favor, insira um número válido para a página.");
                    return true;
                }
            }
            
            final int raridadeLambda = raridadeFiltro;
            final int paginaLambda = pagina;
            
            // Executa a consulta de forma assíncrona
            emblemaManager.getEmblemasAsync(raridadeFiltro).thenAccept(emblemas -> {

                // Volta para a thread principal para abrir o inventário
                Bukkit.getScheduler().runTask(plugin, () -> {
                    EmblemaGUI.openAlbum(player, emblemas, plugin, emblemaManager, raridadeLambda, paginaLambda);
                });
            }).exceptionally(ex -> {
                ex.printStackTrace();
                player.sendMessage("Ocorreu um erro ao buscar os emblemas.");
                return null;
            });
            return true;
        }
        return false;
    }

}
