# EntityLabelLib

*A library to simplify working with client-side entity label modifications.*

EntityLabelLib (ELL) allows plugins to easily manipulate client-side nametags/CustomName values of
entities with support for both sending and intercepting packets. Inside, it contains 

> **Note:** ELS is currently in *alpha* stage. Please check the issue tracker to view what tasks
> are yet to be completed.

> **Note:** The term 'label' is used instead of
> 'name-tag' or 'CustomName' to avoid them
> being confused for the server-side CustomName value of a mob.
> The entire purpose of ELL is to allow plugins
> to easily modify these values strictly client-side (using packets).

## Requirements

- **Platforms required:** ELL is available for Bukkit (and therefore SpigotMC/PaperMC servers).
  - *SpongePowered and Minestom implementations are planned for the distant future.*
  
- **Adventure is required:** ELL requires you to use Adventure components,
which are available on all platforms.
  - If you are compiling your plugin purely for platforms which natively provide Adventure, then
  no worries!
  - Otherwise, for plugins which are compatible with SpigotMC and other non-Adventure
  platforms, you'll need a platform implementation shaded in, such as the Bukkit one.

- **LabelHandler Impl Compatibility:** Users must fulfil any of these requirements for ELL to work:
  - MC 1.18.2 (or newer) servers will use the `NmsLabelHandler`
  (unless a new version has not been implemented yet).
  - Servers with ProtocolLib installed will use the `ProtocolLibLabelHandler`.
  - Servers which do not fulfil any of the above requirements will not be able to use ELL, as there
  is no other label handler implementation available to provide compatibility with their server.

## Contributors

Contributions are warmly welcome! As with all of our other contributors, you will receive credit
for your kind work.

To learn more about contributing, see [CONTRIBUTING.md](CONTRIBUTING.md).

Thank you to all of the contributors for their work!

> **Labels:** `🔴 maintainer` `🔵 code` `🟢 support` `🟡 docs` `🟣 concepts` `⚪️ other` `💥 big contributor`

> **Ordering:** Date of Contribution

- [@lokka30](https://github.com/lokka30) `🔴 maintainer` `💥 big contributor` `🟢 support` `🔵 code` `🟡 docs` `🟣 concepts`
- [@DavidTs93](https://github.com/DavidTs93) (aka *DMan16*) `💥 big contributor` `🔵 code`
  > David helped a lot in getting the packet code working in [this](https://www.spigotmc.org/threads/applying-per-player-name-tags-to-entities-using-nms-1-19-3.582896/) thread.
- [@MrIvanPlays](https://github.com/MrIvanPlays) `🔵 code`
  > Ivan has assisted with implementing NMS packet label methods.

## Projects Using ELL

<table>
    <tr>
        <td>
          <a href="https://github.com/ArcanePlugins/LevelledMobs">
            <img src="https://github-readme-stats.vercel.app/api/pin/?username=ArcanePlugins&repo=LevelledMobs&show_owner=true&theme=react" alt="LevelledMobs Repository Card">
          </a>
        </td>
        <td>
          <p>
            EntityLabelLib was born out of LM4's necessities for packet label management.
            <br /><br />
            LM4 utilises EntityLabelLib to display per-player fake labels to display level and health information.
            <br />
            LM4 also actively displays or hides nametags per-player depending on user settings and player context.
          </p>
        </td>
    </tr>
</table>

> **Note:** Your project not listed above? Please create an issue on the issue tracker. :)

## Copyright Notice

        Copyright © 2022-2023 lokka30 and contributors

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU Affero General Public License as
        published by the Free Software Foundation, either version 3 of the
        License, or (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU Affero General Public License for more details.

        You should have received a copy of the GNU Affero General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.

