package com.niesens.bungeecord.multiconnect;

import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import net.md_5.bungee.connection.InitialHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class EventListener implements Listener {
    private Plugin plugin;

    public EventListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        InitialHandler handler = (InitialHandler) event.getConnection();
        plugin.getLogger().info(handler.getName() + " from " + handler.getAddress() + " connected.");
        Set<String> multiConnectPlayers = getMultiConnectPlayers();
        plugin.getLogger().info("Allowed MultiConnectPlayers are: " + multiConnectPlayers);
        Set<String> multiConnectIPs = getMultiConnectIPs();
        plugin.getLogger().info("Allowed MultiConnectIPs are: " + (multiConnectIPs.isEmpty() ? "any" : multiConnectIPs));

        // check if player is allowed to connect multiple times simultaneously
        if (!multiConnectPlayers.contains(handler.getName())) {
            return;
        }

        // check if ip is restricted
        if (!getMultiConnectIPs().isEmpty() && !getMultiConnectIPs().contains(handler.getAddress().getAddress().getHostAddress())) {
            return;
        }

        String name;

        if (lanMode()) {
            // Name is the ip address
            name = handler.getAddress().getAddress().getHostAddress();
        } else {
            // Name is "N" + last three digits of ip + port number
            name = "N " + Byte.toString(handler.getAddress().getAddress().getAddress()[3]) + "-"
                    + Integer.toString(handler.getAddress().getPort());
        }

        handler.setOnlineMode(false);
        handler.getLoginRequest().setData(name);
        plugin.getLogger().info("Allowing multiple connections for " + event.getConnection().getName()
                + " with the following user name " + name + ".");
    }

    private Set<String> getMultiConnectPlayers() {
        try {
            return new HashSet<>(ConfigurationUtils.getConfiguration(plugin).getStringList("MultiConnectPlayers"));
        } catch (IOException e) {
            return new HashSet<>();
        }
    }

    private Set<String> getMultiConnectIPs() {
        try {
            return new HashSet<>(ConfigurationUtils.getConfiguration(plugin).getStringList("MultiConnectIPs"));
        } catch (IOException e) {
            return new HashSet<>();
        }
    }

    private boolean lanMode() {
        try {
            return ConfigurationUtils.getConfiguration(plugin).getBoolean("LanMode");
        } catch (IOException e) {
            return false;
        }
    }

}
