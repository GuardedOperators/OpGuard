<img src="http://i.imgur.com/le8Nqlv.png"></img>

---

OpGuard is a Spigot plugin that protects against op exploits &amp; malicious plugins.

## Config

### Global Section

| Option                           | Description                      |
|----------------------------------|----------------------------------|
| inspection-interval | Amount of time *in ticks* OpGuard should verify the op list. |
| | **Default Value:** `4` *(5 times per second)* |
| save-interval | Amount of time *in ticks* OpGuard should save the config. |
| | **Default Value:** `1200` *(once per minute)* |
| only-op-if-online | If true, OpGuard will only allow online players to gain op. |
| | **Default Value:** `true` |
| shutdown-on-disable | If true, OpGuard will stop the server if another plugin attempts to disable it. |
| | **Default Value:** `false` |

### Log Section

| Option                           | Description                      |
|----------------------------------|----------------------------------|
| enabled | Whether or not logging should occur. |
| | **Default Value:** `true` |
| status | Whether or not OpGuard should log status messages. |
| | **Default Value:** `true` |
| plugin-attempt | Whether or not OpGuard should log op attempts from other plugins. |
| | **Default Value:** `true` |
| console-attempt | Whether or not OpGuard should log op attempts from the console. |
| | **Default Value:** `true` |
| player-attempt | Whether or not OpGuard should log op attempts from online players. |
| | **Default Value:** `true` |

### Warn Section

| Option                           | Description                      |
|----------------------------------|----------------------------------|
| status | Recieve notifications about OpGuard's status. |
| | **Default Value:** `true` |
| plugin-attempt | Recieve notifications about op attempts from other plugins. |
| | **Default Value:** `true` |
| console-attempt | Recieve notifications about op attempts from the console. |
| | **Default Value:** `true` |
| player-attempt | Recieve notifications about op attempts from online players. |
| | **Default Value:** `true` |
| security-risk | Recieve security warnings if OpGuard's password isn't set. |
| | **Default Value:** `true` |
