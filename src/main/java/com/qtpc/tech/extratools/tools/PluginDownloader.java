package com.qtpc.tech.extratools.tools;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginDownloader extends Thread {
    private final JavaPlugin plugin;
    private final CommandSender sender;
    private final String pluginUrl;
    private final boolean shouldLoad;

    public PluginDownloader(JavaPlugin plugin, CommandSender sender, String pluginUrl, boolean shouldLoad) {
        this.plugin = plugin;
        this.sender = sender;
        this.pluginUrl = pluginUrl;
        this.shouldLoad = shouldLoad;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(pluginUrl);
            URLConnection connection = url.openConnection();
            connection.connect();
            int length = connection.getContentLength();
            BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
            ReadableByteChannel channel = Channels.newChannel(input);
            File outputDir = new File(plugin.getDataFolder(), "downloads");
            outputDir.mkdirs();
            String fileName = new File(url.getPath()).getName();
            File outputFile = new File(outputDir, fileName);
            FileOutputStream output = new FileOutputStream(outputFile);
            output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
            output.close();
            channel.close();
            input.close();

            // Check if it's a valid plugin jar
            try (JarFile jarFile = new JarFile(outputFile)) {
                JarEntry pluginYml = jarFile.getJarEntry("plugin.yml");
                if (pluginYml == null) {
                    sender.sendMessage(ChatColor.RED + "Error: downloaded file is not a valid plugin (missing plugin.yml)");
                    return;
                }

                PluginDescriptionFile description = plugin.getPluginLoader().getPluginDescription(outputFile);
                String mainClass = description.getMain();
                JarEntry mainClassEntry = jarFile.getJarEntry(mainClass.replace(".", "/") + ".class");
                if (mainClassEntry == null) {
                    sender.sendMessage(ChatColor.RED + "Error: downloaded file is not a valid plugin (missing main class)");
                    return;
                }
            }

            sender.sendMessage(ChatColor.GREEN + "Plugin downloaded successfully: " + fileName);

            if (shouldLoad) {
                File file = new File(outputDir, fileName);
                PluginManager pluginManager = plugin.getServer().getPluginManager();
                Plugin loadedPlugin = pluginManager.loadPlugin(file);
                if (loadedPlugin != null) {
                    sender.sendMessage(ChatColor.GREEN + "Plugin " + loadedPlugin.getName() + " loaded successfully!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Error: failed to load plugin");
                }
            }
        } catch (IOException | InvalidDescriptionException | InvalidPluginException | UnknownDependencyException e) {
            sender.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
        }
    }
}
