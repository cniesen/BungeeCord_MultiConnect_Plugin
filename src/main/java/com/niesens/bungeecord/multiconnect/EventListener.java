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
        Set<String> multiConnectUsers = getMultiConnectUsers();
        plugin.getLogger().info("Allowed MultiConnectUsers are: " + multiConnectUsers);

        if (multiConnectUsers.contains(handler.getName())) {
            String name;

            if(oneSubnet()) {
                // Name is "N" + last six digits of ip
                name = "N " + Byte.toString(handler.getAddress().getAddress().getAddress()[2]) + "-"
                        + Byte.toString(handler.getAddress().getAddress().getAddress()[3]);
            } else {
                // Name is "N" + last three digits of ip + port number
                name = "N " + Byte.toString(handler.getAddress().getAddress().getAddress()[3]) + "-"
                        + Integer.toString(handler.getAddress().getPort());
            }

            plugin.getLogger().info("Allowing multiple connections for " + event.getConnection().getName()
                    + " with the following user name " + name + ".");
            handler.setOnlineMode(false);
            handler.getLoginRequest().setData(name);
        }

    }

    private Set<String> getMultiConnectUsers() {
        try {
            return new HashSet(ConfigurationUtils.getConfiguration(plugin).getStringList("MultiConnectUsers"));
        } catch (IOException e) {
            return new HashSet();
        }
    }

    private boolean oneSubnet() {
        try {
            return ConfigurationUtils.getConfiguration(plugin).getBoolean("OneSubnet");
        } catch (IOException e) {
            return false;
        }
    }

}
