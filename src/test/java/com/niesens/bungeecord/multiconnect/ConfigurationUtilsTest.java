package com.niesens.bungeecord.multiconnect;

import junit.framework.Assert;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;

public class ConfigurationUtilsTest extends TestBase {

    @Test
    public void testSaveLoadConfiguration() throws IOException {
        Plugin plugin = mockPlugin();
        Configuration configuration = new Configuration();
        configuration.set("TestProperty", "TestValue");
        ConfigurationUtils.saveConfiguration(plugin, configuration);
        configuration = ConfigurationUtils.loadConfiguration(plugin);
        Assert.assertEquals("TestValue", configuration.getString("TestProperty"));
    }

    @Test
    public void testHashInetSocketAddress() throws UnknownHostException, NoSuchAlgorithmException {
        Assert.assertEquals("zVRYf6AvCi94REMB", ConfigurationUtils.hashInetSocketAddress(mockPlugin(), getInetSocketAddress(false), new HashSet()));
        Assert.assertEquals("RYf6AvCi94REMB53", ConfigurationUtils.hashInetSocketAddress(mockPlugin(), getInetSocketAddress(false), new HashSet(Arrays.asList("zVRYf6AvCi94REMB", "VRYf6AvCi94REMB5"))));
    }
}