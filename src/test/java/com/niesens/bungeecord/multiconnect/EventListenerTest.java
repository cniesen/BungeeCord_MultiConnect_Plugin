package com.niesens.bungeecord.multiconnect;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.packet.LoginRequest;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class EventListenerTest extends TestBase {

    @Test
    public void testMultipleConnectionsUser() throws IOException, NoSuchAlgorithmException {
        testing("SampleUser2", "login-multiconnect", false);
    }

    @Test
    public void testMultipleConnectionsUserLan() throws IOException, NoSuchAlgorithmException {
        setLan();
        testing("SampleUser2", "login-multiconnect", true);
    }

    @Test
    public void testNormalUser() throws IOException, NoSuchAlgorithmException {
        testing("NormalUser", "login-normal", false);
    }

    @Test
    public void testNormalUserLan() throws IOException, NoSuchAlgorithmException {
        setLan();
        testing("NormalUser", "login-normal", false);
    }

    @Test
    public void testMultipleConnectionsIp() throws IOException, NoSuchAlgorithmException {
        setMultiConnectIPs(Arrays.asList("10.10.10.10"));
        testing("SampleUser2", "login-normal", false);
        setMultiConnectIPs(Arrays.asList("10.10.10.10", "10.20.56.123"));
        testing("SampleUser2", "login-multiconnect", false);
        testing("NormalUser", "login-normal", false);
    }

    @Test
    public void testMultipleConnectionsIpLan() throws IOException, NoSuchAlgorithmException {
        setLan();
        setMultiConnectIPs(Arrays.asList("10.10.10.10"));
        testing("SampleUser2", "login-normal", true);
        setMultiConnectIPs(Arrays.asList("10.10.10.10", "10.20.56.123"));
        testing("SampleUser2", "login-multiconnect", true);
        testing("NormalUser", "login-normal", true);
    }

    private void testing(String testUser, String expectedLoginType, boolean expectLanMode) throws UnknownHostException, NoSuchAlgorithmException {
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
        Mockito.when(plugin.getDescription()).thenReturn(new PluginDescription("", null, null, null, null, null, null, null));

        // Trigger event to be tested
        EventListener eventListener = new EventListener(plugin);
        eventListener.onPreLogin(preLoginEvent);

        // Verify results
        if (expectedLoginType.equals("login-multiconnect")) {
            Mockito.verify(logger, Mockito.times(4)).info(Matchers.anyString());
            Mockito.verify(initialHandler).setOnlineMode(false);
            if (expectLanMode) {
                Mockito.verify(loginRequest).setData("10.20.56.123");
            } else {
                Mockito.verify(loginRequest).setData("zVRYf6AvCi94REMB");
            }
        } else {
            Mockito.verify(logger, Mockito.times(3)).info(Matchers.anyString());
            Mockito.verify(initialHandler, Mockito.never()).setOnlineMode(Mockito.anyBoolean());
            Mockito.verify(loginRequest, Mockito.never()).setData(testUser);
        }
    }

    private void setMultiConnectIPs(List multiConnectIPs) throws IOException {
        Plugin plugin = mockPlugin();
        Configuration configuration = ConfigurationUtils.loadConfiguration(plugin);
        configuration.set("MultiConnectIPs", multiConnectIPs);
        ConfigurationUtils.saveConfiguration(plugin, configuration);
    }

    private void setLan() throws IOException {
        Plugin plugin = mockPlugin();
        Configuration configuration = ConfigurationUtils.loadConfiguration(plugin);
        configuration.set("LanMode", true);
        ConfigurationUtils.saveConfiguration(plugin, configuration);

    }

}

