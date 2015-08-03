package com.niesens.bungeecord.multiconnect;

import junit.framework.Assert;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.packet.LoginRequest;
import org.junit.Before;
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

    private enum ExpectedLoginNameType {
        MOJANG_USER_NAME, INET_ADDRESS_HASH, LAN_IP, LAN_USER_NAME
    }

    private int baseLogCount;

    @Before
    public void setUp() {
        baseLogCount = 4;
    }

    @Test
    public void testMultipleConnectionsUser() throws IOException, NoSuchAlgorithmException {
        testing("SampleUser2", ExpectedLoginNameType.INET_ADDRESS_HASH);
    }

    @Test
    public void testMultipleConnectionsUserLan() throws IOException, NoSuchAlgorithmException {
        setLan();
        testing("SampleUser2", ExpectedLoginNameType.LAN_IP);
    }

    @Test
    public void testNormalUser() throws IOException, NoSuchAlgorithmException {
        testing("NormalUser", ExpectedLoginNameType.MOJANG_USER_NAME);
    }

    @Test
    public void testNormalUserLan() throws IOException, NoSuchAlgorithmException {
        setLan();
        testing("NormalUser", ExpectedLoginNameType.MOJANG_USER_NAME);
    }

    @Test
    public void testMultipleConnectionsIp() throws IOException, NoSuchAlgorithmException {
        setMultiConnectIPs(Arrays.asList("10.10.10.10"));
        testing("SampleUser2", ExpectedLoginNameType.MOJANG_USER_NAME);
        setMultiConnectIPs(Arrays.asList("10.10.10.10", "10.20.56.123"));
        testing("SampleUser2", ExpectedLoginNameType.INET_ADDRESS_HASH);
        testing("NormalUser", ExpectedLoginNameType.MOJANG_USER_NAME);
    }

    @Test
    public void testMultipleConnectionsIpLan() throws IOException, NoSuchAlgorithmException {
        setLan();
        setMultiConnectIPs(Arrays.asList("10.10.10.10"));
        testing("SampleUser2", ExpectedLoginNameType.MOJANG_USER_NAME);
        setMultiConnectIPs(Arrays.asList("10.10.10.10", "10.20.56.123"));
        testing("SampleUser2", ExpectedLoginNameType.LAN_IP);
        testing("NormalUser", ExpectedLoginNameType.MOJANG_USER_NAME);
    }

    @Test
    public void testMultipleConnectionLanUserName() throws IOException, NoSuchAlgorithmException {
        setLan();
        testing("SampleUser2", ExpectedLoginNameType.LAN_USER_NAME);
    }

    @Test
    public void testAlwaysGenerateUsernames() throws IOException, NoSuchAlgorithmException {
        setAlwaysGenerateUsernames();
            testing("SampleUser2", ExpectedLoginNameType.INET_ADDRESS_HASH);
    }

    private void testing(String testUser, ExpectedLoginNameType expectedLoginNameType) throws UnknownHostException, NoSuchAlgorithmException {
        // Mock event
        PreLoginEvent preLoginEvent = PowerMockito.mock(PreLoginEvent.class);
        InitialHandler initialHandler = PowerMockito.mock(InitialHandler.class);
        Mockito.when(initialHandler.getName()).thenReturn(testUser);
        if (expectedLoginNameType.equals(ExpectedLoginNameType.LAN_USER_NAME)) {
            Mockito.when(initialHandler.getAddress()).thenReturn(getInetSocketAddress(true));
        } else {
            Mockito.when(initialHandler.getAddress()).thenReturn(getInetSocketAddress(false));
        }
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
        if (expectedLoginNameType.equals(ExpectedLoginNameType.MOJANG_USER_NAME)) {
            Mockito.verify(logger, Mockito.times(baseLogCount)).info(Matchers.anyString());
            Mockito.verify(initialHandler, Mockito.never()).setOnlineMode(Mockito.anyBoolean());
            Mockito.verify(loginRequest, Mockito.never()).setData(testUser);
        } else if (expectedLoginNameType.equals(ExpectedLoginNameType.INET_ADDRESS_HASH)) {
            Mockito.verify(logger, Mockito.times(baseLogCount + 1)).info(Matchers.anyString());
            Mockito.verify(initialHandler).setOnlineMode(false);
            Mockito.verify(loginRequest).setData("zVRYf6AvCi94REMB");
        } else if (expectedLoginNameType.equals(ExpectedLoginNameType.LAN_IP)) {
            Mockito.verify(logger, Mockito.times(baseLogCount + 1)).info(Matchers.anyString());
            Mockito.verify(initialHandler).setOnlineMode(false);
            Mockito.verify(loginRequest).setData("10.20.56.123");
        } else if (expectedLoginNameType.equals(ExpectedLoginNameType.LAN_USER_NAME)) {
            Mockito.verify(logger, Mockito.times(baseLogCount + 1)).info(Matchers.anyString());
            Mockito.verify(initialHandler).setOnlineMode(false);
            Mockito.verify(loginRequest).setData("LittleMiner");
        } else {
            Assert.fail("Unknown expectedLoginNameType: " + expectedLoginNameType);
        }
    }

    private void setAlwaysGenerateUsernames() throws IOException {
        Plugin plugin = mockPlugin();
        Configuration configuration = ConfigurationUtils.loadConfiguration(plugin);
        configuration.set("AlwaysGenerateUsernames", true);
        ConfigurationUtils.saveConfiguration(plugin, configuration);

        baseLogCount = 2;
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

