package com.niesens.bungeecord.multiconnect;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {
    @Override
    public void onEnable() {
        this.getProxy().getPluginManager().registerListener(this, new EventListener(this));
    }
}
