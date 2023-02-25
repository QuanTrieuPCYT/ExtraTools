package com.qtpc.tech.extratools;

import com.qtpc.tech.extratools.commands.ExtraToolsCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class ExtraTools extends JavaPlugin {
    public static Plugin plugin;
    public final Logger logger = this.getLogger();


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger.info("ExtraTools has been enabled!");
        // register the command to the server for it to handle
        ExtraToolsCommand commandExecutor = new ExtraToolsCommand(this);
        getCommand("extratools").setExecutor(commandExecutor);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Shutting down ExtraTools...");
    }
}
