<center><img src="info/img/logo.png"></img></center>

---

OpGuard is a Spigot plugin that protects against op exploits &amp; malicious plugins. It achieves this by maintaining a list of verified operators and overriding the Bukkit implementation of /op and /deop with its own command: `/opguard`.

#### Releases are compiled with *Java 8*

---

## Features

* **Prevent other plugins from giving op**
  * Malicious plugins won't be able to set op

* **Require a password to op or deop players**
  * Useful against social-engineering attacks (e.g. json signs/books) and breached accounts

* **Punish players that are maliciously given op**

* **Hidden from /help, /?, and tab-completion**
  * If you disable access to /plugins, players won't even know this plugin is installed

* **Disables /op and /deop**
  * All op management must be done through OpGuard
  * Returns an "unknown command" message when someone attempts /op or /deop

* **Log and notify staff of op attempts**
 
## Command Usage

**Command:** `/opguard` 

**Alias:** `/og`

| Command             | Description         |
|---------------------|---------------------|
| `/opguard op <player> <password (if set)>` | Gives op to a player. <br> If OpGuard's password is set, it **must** be included. |
| `/opguard deop <player> <password (if set)>` | Removes op from a player. <br> If OpGuard's password is set, it **must** be included. |
| `/opguard list` | List all players with op. |
| `/opguard password <new password>` | Set OpGuard's password. <br> This will only work if OpGuard has no password set. |
| `/opguard reset <current password>` | Removes OpGuard's password. <br> The current password must be correct to remove. |
| `/opguard reload` | Reload OpGuard's config. |

## Permissions

| Permission Node | Grants Access To |
|-----------------|------------------|
| opguard.manage | Use `/opguard` |
| opguard.warn | Recieves notifications from OpGuard |

Players with op have both permissions by default.

## Configuration

For an in-depth configuration guide, [visit the wiki page here](https://github.com/RezzedUp/OpGuard/wiki/Configuration-Guide).

OpGuard's config is pretty straight forward despite lacking comments.
