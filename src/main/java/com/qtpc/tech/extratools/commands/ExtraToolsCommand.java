package com.qtpc.tech.extratools.commands;

import com.qtpc.tech.extratools.tools.PluginDownloader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ExtraToolsCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public ExtraToolsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Error: missing argument");
            return false;
        }

        if (args[0].equalsIgnoreCase("download")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Error: missing plugin URL");
                return false;
            }

            boolean shouldLoad = false;
            if (args.length > 2 && args[2].equalsIgnoreCase("--load")) {
                shouldLoad = true;
            }

            String pluginUrl = args[1];
            PluginDownloader downloader = new PluginDownloader(plugin, sender, pluginUrl, shouldLoad);
            downloader.start();

            return true;
        }

        if (args[0].equalsIgnoreCase("load")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Error: missing plugin file name");
                return false;
            }

            String fileName = args[1];
            File file = new File(plugin.getDataFolder(), fileName);
            if (!file.exists()) {
                sender.sendMessage(ChatColor.RED + "Error: plugin file not found");
                return false;
            }

            try {
                plugin.getServer().getPluginManager().loadPlugin(file);
                sender.sendMessage(ChatColor.GREEN + "Plugin " + file.getName() + " loaded successfully!");
            } catch (InvalidPluginException | InvalidDescriptionException | UnknownDependencyException e) {
                sender.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
            }

            return true;
        }

        return false;
    }
}

