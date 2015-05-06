BungeeCord_MultiConnect_Plugin
==============================

This plugin allows multiple logins from a single Minecraft account.

( It's a much nicer implementation of the CraftBukkitLanLogin plugin https://github.com/cniesen/CraftBukkitLanLogin )

Where to find the official version?
-----------------------------------

End users can download GitHub Backup at https://github.com/cniesen/BungeeCord_MultiConnect_Plugin/releases

Developers can contribute to GitHub Backup at https://github.com/cniesen/BungeeCord_MultiConnect_Plugin

Where to file bugs and feature requests?
----------------------------------------

Please use the issue tracking at https://github.com/cniesen/BungeeCord_MultiConnect_Plugin/issues

Requirements
------------

* Spigot http://www.spigotmc.org/wiki/spigot/
* Bungeecord http://www.spigotmc.org/wiki/bungeecord/
* A server/computer to run all this
* This plugin
* And a bit of configuration

Plugin Configuration
--------------------

```
OneSubnet: false
MultiConnectUsers: 
 - SampleUser1
 - SampleUser2
```
On the first run the plugin will create the config.yml file if one doesn't exist already.  This makes configuration easy.  Start, shutdown, config, and have fun.  By default noone is configured to benefit from the MultiConnect plugin. The config file is located at `bungeecord/plugins/BungeeCord MultiConnect Plugin`.

MultiConnectUsers is the list of Minecraft users that are allowed to connect multiple times. Each user has to be on its own line and must be preceeded by a space, dash, space. 

OneSubnet toggles how the username for MultiConnectUsers are created.  By default OneSubnet is false and the player name will be created by the last few digits of the IP and the port number of the client.  The problem with this setting is that port numbers change when reconnecting and a different player name will be assigned. However, the use of port numbers enables multiple connections from the same IP address (i.e. when you're behind your home (nat) router.  

If everybody will have a unique IP then then the OneSubnet can be set to true. The player name will only be based of the IP and remain the same when reconnecting.  Anyway, if in doubt leave the OneSubnet false.