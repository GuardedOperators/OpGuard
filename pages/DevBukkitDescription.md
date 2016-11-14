![](http://i.imgur.com/5kJ1YvJ.png)

[![](http://i.imgur.com/pTd7ouB.png)](https://github.com/RezzedUp/OpGuard) [![](http://i.imgur.com/985CDzO.png)](https://discord.gg/E4dzuQ7) [![](http://i.imgur.com/p7Gjbdt.png)](https://github.com/RezzedUp/OpGuard/issues)

![](http://i.imgur.com/Vaecppj.png)

# About

OpGuard is a plugin that protects servers against op exploits & malicious plugins by maintaining a list of **verified operators** and overriding the default implementation of /op and /deop with its own command: `/opguard`.

It not only blocks access to /op as a command, it also prevents **other plugins** from setting op with Bukkit API -- directly denying access in some cases.

OpGuard offers the ability to restrict access to /op (`/opguard op`) with a **password**, useful for keeping untrustworthy staff from setting op. It's also helpful in the event a malicious user somehow gains op or when a staff member has their account broken into; users who don't know the password won't be able to op other users when OpGuard is installed.

# Features

### Require a password to op and deop players.

> Passwords limit who can set op.
>
> ![](http://i.imgur.com/c43QgQS.png)
>
> **Remember:** don't ever tell untrustworthy people the password!

### Deny access to /op and /deop.

> OpGuard denies all attempts at using /op and /deop, whether from players or console.
>
> ![](http://i.imgur.com/S6Ludvs.png)
> 
> ![](http://i.imgur.com/VtmpdDI.png)
>
> All op-related commands must be done through /opguard.
>
> Players who don't have permission to use /op and /deop will see an "unknown command" error when they attempt to use either command.​

### Prevent other plugins from setting op.

> If a malicious plugin is caught setting op, OpGuard will disable the plugin and punish the player.
>
> ![](http://i.imgur.com/3mQUXCN.png)
>
> If a plugin isn't caught, the player is still punished.
>
> ![](http://i.imgur.com/xhh6eWT.png)
>
> **Remember:** only install plugins from trusted sources!​

### Punish players that attempt to gain op.

> Configure multiple punishment commands.
>
> ![](http://i.imgur.com/aYSt507.png)

### Hidden from /help, /?, and tab-completion.

> If `/plugins` is disabled, players won't ever know this plugin is installed.
>
> **/opguard** will display an "unknown command" message for players who don't have permission.​

### Extensive logging.

> OpGuard keeps a log file for everything that happens with the plugin.
>
> Logged items can be modified in the config.​

# Commands

**Command:** /opguard

**Alias:** /og

**Usage:**

* /opguard ***op*** `<`**player**`>` `<`**password** `(if set)>`
    * Set op for a player.
    * If OpGuard's password is set, it **must** be included.

* /opguard ***deop*** `<`**player**`>` `<`**password** `(if set)>`
    * Remove op from a player.
    * If OpGuard's password is set, it **must** be included.

* /opguard ***list***
    * List all verified operators.

* /opguard ***password*** `<`**new password**`>`
    * Set OpGuard's password.
    * This will only work if OpGuard has no password set.

* /opguard ***reset*** `<`**current password**`>`
    * Removes OpGuard's password. 
    * The current password must be correct to remove.

* /opguard ***reload*** `<`**password** `(if set)>`
    * Reload OpGuard's config.
    * If OpGuard's password is set, it **must** be included.

![](http://i.imgur.com/bM6gdtj.png)


# Permissions

* **opguard.manage**
    * Grants access to `/opguard`
* **opguard.warn**
    * Recieves notifications from OpGuard

Players with op have both permissions by default.

# Notice

OpGuard submits **anonymous** metrics data to http://mcstats.org 

This can be **disabled** via the config (set **metrics** to false).

Collected data is nonintrusive and includes the following:
* Server version
* Number of players online
* Java version
* Geographic location (country) of the server
* Plugin version
* Online mode status of the server

OpGuard utilizes mcstats' standard unmodified MetricsLite class. For more information, [visit this page](https://github.com/Hidendra/Plugin-Metrics/wiki).
