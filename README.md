# Indispensable Commands

[![Modrinth Version](https://img.shields.io/modrinth/v/saIlazMs?logo=modrinth&color=008800)](https://modrinth.com/mod/fabric-custom-names)
[![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/saIlazMs?logo=modrinth&color=008800)](https://modrinth.com/mod/fabric-custom-names)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/saIlazMs?logo=modrinth&color=008800)](https://modrinth.com/mod/fabric-custom-names)
[![Github Badge](https://img.shields.io/badge/github-customname-white?logo=github)](https://github.com/eclipseisoffline/customname)
![GitHub License](https://img.shields.io/github/license/eclipseisoffline/customname)

Indispensable Commands est le mod parfait pour votre survie et/ou votre serveur entre amis. Le mod ajoute des commandes inspirées du plugin Essentials.
Il vous permet de profiter de la joie des mods sans sacrifier les commandes pourtant indispensable à toute aventure.

Pour aller au plus simple, le mod ne nécessite pas LuckPerms.
Do not hesitate to report any bugs, suggest new commands or translations ✌️

## Version support

| Minecraft Version | Status    |
|-------------------|-----------|
| 1.21.6            | 🛠️ W.I.P |
| 1.21.5            | ✅         |
| 1.21.4            | ❌         |
| 1.21.2            | ❌         |
| 1.21.3            | ❌         |
| 1.21.2            | ❌         |
| 1.21.1            | ❌         |

J'essaie de maintenir le mod à jour et de le rendre disponible sur le plus grand nombre de versions possible. 
Pour le moment, les versions en dessous de la 1.21.5 ne sont pas pris en charge, cependant celà peut changer si je reçois beaucoup de demandes pour des versions antiérieures.

## Commands
### Teleportation
- `/spawn` - Teleport to spawn.
- `/back` - Teleport to your last death position.
- `/sethome <home name>` - Creates a home.
- `/home <home name>` - Teleport to a home.
- `/homelist` - List your homes.
- `/delhome <home name>` - Deletes a home.
- `/tpa <player>` - Sends a TP request.
- `/tpyes or /tpaccept` - Accepts a TP request.
- `/tpno or /tpdeny` - Declines a TP request.
### Communication
- `/mail <player> <message>` - Sends a private message to the desired player.
- `/r or /replay <message>` - Reply to the last mail.

### Utilities
- `/trash` - Open a trash.
- `/hat` - Puts held item on your head.
- `/suicide` - Kills you. Plain and simple.

### Help
- `/help or /adie` - Shows all the commands available.


## OP Commands
- `/setspawn` - Sets the server spawn point.
- `/tpacooldown` - Changes the default TPA Cooldown.
- `/nick <nick>` - Changes the selected player's nickname .
- `/nicklength <1 to 64>` - Changes the max length for nicknames.
- `/freeze <player>` - Freezes a player.
- `/helpop or aideop` - Shows all the Admin Commands.

Minecraft's [formatting codes](https://minecraft.wiki/w/Formatting_codes) (`&`) can be used to format nickname as well as spaces, accents and hex codes `(&#<hex code)`

## Config file

The mod's configuration file is present in `minecraft directory/config/indispensable_commands/configs`.
By default, the configuration file looks like this:

```json
{
  "blacklistedNicks": [],
  "maxNickLength": 16,
  "homeLimit": 2
}
```
- `blacklistedNicks` can be used to blacklist unwanted nickname.
- `maxNickLength` controls how long a nickname can be.
- `homeLimit` controls the maximum number of homes for every players (Admins included).