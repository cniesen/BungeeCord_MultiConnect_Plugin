package com.niesens.bungeecord.multiconnect;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.packet.LoginRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class EventListenerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testMultipleConnectionsUser() throws IOException {
        testing("SampleUser2");
    }

    @Test
    public void testNormalUser() throws IOException {
        testing("NormalUser");
    }

    private void testing(String testUser) throws UnknownHostException {
        // Mock event
        PreLoginEvent preLoginEvent = PowerMockito.mock(PreLoginEvent.class);
        InitialHandler initialHandler = PowerMockito.mock(InitialHandler.class);
        Mockito.when(initialHandler.getName()).thenReturn(testUser);
        Mockito.when(initialHandler.getAddress()).thenReturn(getInetSocketAddress());
        LoginRequest loginRequest = PowerMockito.mock(LoginRequest.class);
        Mockito.when(initialHandler.getLoginRequest()).thenReturn(loginRequest);
        Mockito.when(preLoginEvent.getConnection()).thenReturn(initialHandler);

        // Mock plugin
        Plugin plugin = PowerMockito.mock(Plugin.class);
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(plugin.getLogger()).thenReturn(logger);
        BungeeCord bungeeCord = PowerMockito.mock(BungeeCord.class);
        Mockito.when(bungeeCord.getPluginsFolder()).thenReturn(testFolder.getRoot());
        Mockito.when(plugin.getProxy()).thenReturn(bungeeCord);
        Mockito.when(plugin.getDescription()).thenReturn(new PluginDescription("",null,null,null,null,null,null,null));

        // Trigger event to be tested
        EventListener eventListener = new EventListener(plugin);
        eventListener.onPreLogin(preLoginEvent);

        // Verify results
        if (testUser.equals("SampleUser2")) {
            Mockito.verify(logger, Mockito.times(3)).info(Matchers.anyString());
            Mockito.verify(initialHandler).setOnlineMode(false);
            Mockito.verify(loginRequest).setData("N 123-3333");
        } else {
            Mockito.verify(logger, Mockito.times(2)).info(Matchers.anyString());
            Mockito.verify(initialHandler, Mockito.never()).setOnlineMode(Mockito.anyBoolean());
            Mockito.verify(loginRequest, Mockito.never()).setData(Mockito.anyString());
        }
    }

    private InetSocketAddress getInetSocketAddress() throws UnknownHostException {
        byte[] ipAddress = new byte[] {10,20,56,123};
        InetAddress inetAddress = InetAddress.getByAddress("example.com", ipAddress);
        return new InetSocketAddress(inetAddress, 3333);
    }

}