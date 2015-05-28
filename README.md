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
MultiConnectUsers:
  - SampleUser1
  - SampleUser2
MultiConnectIPs:
  - 127.0.0.1
LanMode: false
LanUsersNames:
  127.0.0.1: PapaMiner
  12.118.1.15: MamaMiner
  12.118.10.114: LittleMiner
 SecretSalt: i3XACXrpc96HSbCtI8Szh8p1IHM=
```
On the first run the plugin will create the config.yml file if one doesn't exist already.  This makes configuration easy.  Start, log on once, shutdown, make configuration changes, and have fun.  By default no one is configured to benefit from the MultiConnect plugin. The config file is located at `bungeecord/plugins/BungeeCord MultiConnect Plugin`.

**MultiConnectUsers** is the list of Mojang usernamess that are allowed to connect multiple times. Each user has to be on its own line and must be preceded by a space, dash, space. Users not in this list will sign in as a regular Mojang user.  By default this list is empty.

**MultiConnectIPs** is a list of IPs from which clients are allowed to use the MultiConnect feature by signing in with a username in the MultiConnectUsers list.  Users connecting from other IPs will sign in as a regular Mojang user.  This list is empty by default which allows everybody to use the MultiConnect feature by signing in with a username in the MultiConnectUsers list.

**LanMode** toggles how the username for MultiConnectUsers are created.  By default LanMode is false and the player name will be a hash of the internet address of the player.  The hash provides some obscurity and makes it difficult to determine the players IP address and thus providing some privacy toward other players.  
When LanMode is true the IP address of the player is used which makes it a nicer username but can't be used across a home (NAT) router.

If everybody will have a unique IP then then the LanMode can be set to true. The player name will be the IP address of the player.  This also allows the usage of the nicer looking LanUserNames.  Anyway, if in doubt leave the LanMode false.

**LanUserNames** are IP and username pairs.  The associated username for the specified IP number will be used in the game.  This allows nice usernames for MultiConnectUsers.  However this feature requires that the LanMode is enabled and thus is not suggested to be used for Minecraft servers that are accessible from the Internet.

**SecretSalt** is a random hash used to further obscure the internet address that's used as the user name when LanMode is disabled.  It is best to let this unique salt to be created automatically.

![Flowchart](https://cloud.githubusercontent.com/assets/3842973/7850820/b4291a34-04ac-11e5-9ccb-131974ef5c84.png)