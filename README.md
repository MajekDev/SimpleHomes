<img align="right" src="https://github.com/MajekDev/SimpleHomes/blob/main/img/logo.png?raw=true" height="200" width="220">
<h1 align="center">SimpleHomes by Majekdor</h1>

SimpleHomes is a stable base homes plugin. It offers all the essential features necessary in a homes plugin and some additional useful permissions. If you're looking for a more featured homes plugin with many more commands/permissions, then you'll want to take a look at UltimateHomes.

## Commands

There are only 5 commands in SimpleHomes.
- **/sethome [name]** - Set a new home at your current location.
- **/home [player|name] [name]** - Teleport to a certain home.
- **/homes [player]** - View a paginated list of homes.
- **/delhome [player|name] [name]** - Delete a certain home.
- **/simplehomes [help|reload]** - Get help or reload the plugin.

## Permissions

There are a number of permissions available to modify what certain players can do.

These are given to all players automatically, but can be negated with a permission manager such as [LuckPerms](https://github.com/lucko/LuckPerms):
- `simplehomes.sethome` - Allows players to set a home.
- `simplehomes.home` - Allows players to go to one of their homes.
- `simplehomes.homes` - Allows players to view a list of their homes.
- `simplehomes.delhome` - Allows players to delete one of their homes.
- `simplehomes.nether` - Allows players to set homes in the nether dimension.
- `simplehomes.end` - Allows players to set homes in the end dimension.

These are only given to opped players automatically:
- `simplehomes.delhome.other` - Allows players to delete another player's home.
- `simplehomes.home.other` - Allows players to travel to another player's home.
- `simplehomes.homes.other` - Allows players to view other player's homes.
- `simplehomes.reload` - Allows players to reload the plugin.
- `simplehomes.sethome.unlimited` - Allows players to set unlimited homes.
- `simplehomes.delay.bypass` - Allows players to bypass the teleport delay.

To set a player's max homes limit with permissions use `simplehomes.sethome.max.<integer>`, replacing `<integer>` with the number of max homes.

> Note: If you set a lower home limit than what has been set before, the lower limit will not overwrite the higher limit. Meaning if a player has, for example, a home limit of 5 and you set it to 3 via permissions, it will remain 5. To lower it you must manually edit the player's JSON file. 

## Configuration

The default `config.yml` is available [here](https://github.com/MajekDev/SimpleHomes/blob/main/src/main/resources/config.yml) where a handful of configuration options can be changed. To make these changes take effect run `/simplehomes reload`. If you have questions about the configuration or want to suggest a new configuration option you can discuss that in [Discord](https://discord.majek.dev).

All messages sent by SimpleHomes are stored in the `lang.yml` file [here](https://github.com/MajekDev/SimpleHomes/blob/main/src/main/resources/lang.yml) and can be changed/translated. Color codes and other formatting are done using [MiniMessage](https://docs.adventure.kyori.net/minimessage.html#the-components). Legacy color codes (`&c`, `&l`, etc.) will not work. Those are being removed from Minecraft entirely in the near future.

## For the nerds... I meean devs :P

SimpleHomes stores homes using JSON. When the plugin is installed the `playerdata` folder is created and every player has a file named with their unique id. When homes are set/deleted the file updates in real time. The plugin also depends heavily on [adventure](https://github.com/KyoriPowered/adventure) by Kyori for components in chat. SimpleHomes has an API and documentation for that is coming.

## Support

If you need help with the plugin and can't find the answer here or on Spigot, then the best way to get help is to join my [Discord](https://discord.gg/CGgvDUz). Make sure you read the frequently-asked channel before posting in the bug-reports channel (if it's a bug) or in the simple-homes channel (for general help).

If you have discovered a bug you can either join my [Discord](https://discord.gg/CGgvDUz) and report it there or open an issue here on GitHub. Please do not message me on Spigot in regard to a bug, there are easier ways to communicate.


## Contributing

SimpleHomes is open-source and licensed under the [MIT License](https://github.com/MajekDev/SimpleHomes/blob/main/LICENSE), so if you want to use any code contained in the plugin or clone the repository and make some changes, go ahead!

If you've found a bug within the plugin and would like to just make the changes to fix it yourself, you're free to do so and make a pull request here on GitHub. If you make significant contributions to the project, and by significant I mean one little PR to fix a tiny bug doesn't count as significant, you can earn the Contributor role in my [Discord](https://discord.gg/CGgvDUz).


## Donate

I'm a full time college student who makes and supports these plugins in my free time (when I have any). As a long time supporter of open source, most of my plugins are free. If you enjoy my plugins and would like to support me, you can buy me coffee over on  [PayPal](https://paypal.com/paypalme/majekdor). Donations of any amount are appreciated and a donation of $10 or more will get you the Supporter role in my [Discord](https://discord.gg/CGgvDUz)!
