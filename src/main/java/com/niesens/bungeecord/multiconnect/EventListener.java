package com.niesens.bungeecord.multiconnect;

import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import net.md_5.bungee.connection.InitialHandler;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class EventListener implements Listener {
    private Plugin plugin;

    public EventListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) throws NoSuchAlgorithmException {
        InitialHandler handler = (InitialHandler) event.getConnection();
        plugin.getLogger().info(handler.getName() + " from " + handler.getAddress() + " connected.");

        if (ConfigurationUtils.isAlwaysGenerateUsernames(plugin)) {
            plugin.getLogger().info("AlwaysGenerateUsername: true");

        } else {
            plugin.getLogger().info("AlwaysGenerateUsername: false");
            Set<String> MultiConnectUsers = ConfigurationUtils.getMultiConnectUsers(plugin);
            plugin.getLogger().info("Allowed MultiConnectUsers are: " + MultiConnectUsers);
            Set<String> multiConnectIPs = ConfigurationUtils.getMultiConnectIPs(plugin);
            plugin.getLogger().info("Allowed MultiConnectIPs are: " + (multiConnectIPs.isEmpty() ? "any" : multiConnectIPs));

            // check if player is allowed to connect multiple times simultaneously
            if (!MultiConnectUsers.contains(handler.getName())) {
                return;
            }

            // check if ip is restricted
            if (!multiConnectIPs.isEmpty() && !multiConnectIPs.contains(handler.getAddress().getAddress().getHostAddress())) {
                return;
            }
        }

        String name;

        if (ConfigurationUtils.isLanMode(plugin)) {
            // Name is the ip address
            name = handler.getAddress().getAddress().getHostAddress();

            // Use configured LAN name if one exists
            Map lanLoginUsers = ConfigurationUtils.getLanUsersNames(plugin);
            if (lanLoginUsers.containsKey(name) && lanLoginUsers.get(name) instanceof String) {
                name = (String) lanLoginUsers.get(name);
            }
        } else {
            // Name is a hash based on the ip address and port number of the connecting user
            name = ConfigurationUtils.hashInetSocketAddress(plugin, handler.getAddress(), new HashSet());
        }

        plugin.getLogger().info("Allowing multiple connections for " + handler.getName()
                + " with the following username " + name + ".");
        handler.setOnlineMode(false);
        handler.getLoginRequest().setData(name);
    }

}
