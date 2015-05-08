package com.niesens.bungeecord.multiconnect;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigurationUtils {

    public static Configuration getConfiguration(Plugin plugin) throws IOException {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            Files.copy(plugin.getResourceAsStream("config.yml"), file.toPath());
        }

        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    }
}
