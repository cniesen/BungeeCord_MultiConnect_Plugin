package com.niesens.bungeecord.multiconnect;

import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import net.md_5.bungee.connection.InitialHandler;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
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
        Set<String> multiConnectPlayers = ConfigurationUtils.getMultiConnectPlayers(plugin);
        plugin.getLogger().info("Allowed MultiConnectPlayers are: " + multiConnectPlayers);
        Set<String> multiConnectIPs = ConfigurationUtils.getMultiConnectIPs(plugin);
        plugin.getLogger().info("Allowed MultiConnectIPs are: " + (multiConnectIPs.isEmpty() ? "any" : multiConnectIPs));

        // check if player is allowed to connect multiple times simultaneously
        if (!multiConnectPlayers.contains(handler.getName())) {
            return;
        }

        // check if ip is restricted
        if (!multiConnectIPs.isEmpty() && !multiConnectIPs.contains(handler.getAddress().getAddress().getHostAddress())) {
            return;
        }

        String name;

        if (ConfigurationUtils.isLanMode(plugin)) {
            // Name is the ip address
            name = handler.getAddress().getAddress().getHostAddress();
        } else {
            // Name is a hash based on the ip address and port number of the connecting user
            name = ConfigurationUtils.hashInetSocketAddress(plugin, handler.getAddress(), new HashSet());
        }

        handler.setOnlineMode(false);
        handler.getLoginRequest().setData(name);
        plugin.getLogger().info("Allowing multiple connections for " + event.getConnection().getName()
                + " with the following user name " + name + ".");
    }

}
