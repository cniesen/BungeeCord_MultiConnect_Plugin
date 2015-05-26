package com.niesens.bungeecord.multiconnect;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class TestBase {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    protected Plugin mockPlugin() {
        Plugin plugin = PowerMockito.mock(Plugin.class);
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(plugin.getLogger()).thenReturn(logger);
        BungeeCord bungeeCord = PowerMockito.mock(BungeeCord.class);
        Mockito.when(bungeeCord.getPluginsFolder()).thenReturn(testFolder.getRoot());
        Mockito.when(plugin.getProxy()).thenReturn(bungeeCord);
        Mockito.when(plugin.getDescription()).thenReturn(new PluginDescription("", null, null, null, null, null, null, null));
        return plugin;
    }

    protected InetSocketAddress getInetSocketAddress() throws UnknownHostException {
        byte[] ipAddress = new byte[]{10, 20, 56, 123};
        InetAddress inetAddress = InetAddress.getByAddress("example.com", ipAddress);
        return new InetSocketAddress(inetAddress, 3333);
    }

}
