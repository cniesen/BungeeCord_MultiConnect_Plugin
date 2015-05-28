package com.niesens.bungeecord.multiconnect;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigurationUtils {

    public static Configuration loadConfiguration(Plugin plugin) throws IOException {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(getConfigurationFile(plugin));
    }

    public static void saveConfiguration(Plugin plugin, Configuration configuration) throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, getConfigurationFile(plugin));
    }

    public static Set<String> getMultiConnectUsers(Plugin plugin) {
        try {
            return new HashSet<>(ConfigurationUtils.loadConfiguration(plugin).getStringList("MultiConnectUsers"));
        } catch (IOException e) {
            return new HashSet<>();
        }
    }

    public static Set<String> getMultiConnectIPs(Plugin plugin) {
        try {
            return new HashSet<>(ConfigurationUtils.loadConfiguration(plugin).getStringList("MultiConnectIPs"));
        } catch (IOException e) {
            return new HashSet<>();
        }
    }

    public static boolean isLanMode(Plugin plugin) {
        try {
            return ConfigurationUtils.loadConfiguration(plugin).getBoolean("LanMode");
        } catch (IOException e) {
            return false;
        }
    }

    public static Map getLanUsersNames(Plugin plugin) {
        try {
            return (Map) ConfigurationUtils.loadConfiguration(plugin).get("LanUsersNames");
        } catch (IOException | ClassCastException e) {
            return new HashMap();
        }
    }

    public static String hashInetSocketAddress(Plugin plugin, InetSocketAddress address, Set userList) throws NoSuchAlgorithmException {
        String saltIpPort = getSecretSalt(plugin) + address.toString();
        byte[] saltIpPortBytes = saltIpPort.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("MD5");
        String mdBase64 = Base64.encodeBase64String(md.digest(saltIpPortBytes));
        mdBase64 = mdBase64.replace("+","_").replace("/", "_");
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            userList.add(player.getDisplayName());
        }
        while (mdBase64.length() > 16 && userList.contains(mdBase64.substring(0,16))) {
            mdBase64 = mdBase64.substring(1);
        }
        return mdBase64.substring(0, 16);
    }

    private static String getSecretSalt(Plugin plugin) throws NoSuchAlgorithmException {
        Configuration configuration;

        try {
            configuration = ConfigurationUtils.loadConfiguration(plugin);
        } catch (IOException e) {
            configuration = new Configuration();
        }

        String secretSalt = configuration.getString("SecretSalt");

        if (secretSalt.isEmpty()) {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            byte[] bytes = new byte[20];
            secureRandom.nextBytes(bytes);
            secretSalt = Base64.encodeBase64String(bytes);

            try {
                configuration.set("SecretSalt", secretSalt);
                ConfigurationUtils.saveConfiguration(plugin, configuration);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed saving configuration file");
            }
        }

        return secretSalt;
    }

    private static File getConfigurationFile(Plugin plugin) throws IOException {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            Files.copy(plugin.getResourceAsStream("config.yml"), file.toPath());
        }

        return file;
    }
}
