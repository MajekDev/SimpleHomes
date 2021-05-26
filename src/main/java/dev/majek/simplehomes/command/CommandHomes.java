package dev.majek.simplehomes.command;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.data.PAPI;
import dev.majek.simplehomes.data.struct.Home;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import dev.majek.simplehomes.util.TabCompleterBase;
import dev.majek.simplehomes.util.TabExecutor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Handles listing homes
 */
public class CommandHomes implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Console cannot go to homes
        if (!(sender instanceof Player)) {
            sendMessage(sender, "command.invalidSender");
            return true;
        }

        Player player = (Player) sender;
        HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(player.getUniqueId());

        // Player is viewing their own homes
        if ((args.length == 1 && isInt(args[0])) || args.length == 0) {

            // Check if the player has permission
            if (!player.hasPermission("simplehomes.homes")) {
                sendMessage(player, "command.noPermission");
                return true;
            }

            // Check if the player has no homes
            if (homesPlayer.getTotalHomes() == 0) {
                sendMessage(player, "command.homes.noHomes");
                return true;
            }

            // Build pagination for homes
            List<Component> lines = new ArrayList<>();
            for (Home home : homesPlayer.getHomes()) {
                Component line = MiniMessage.get().parse("<aqua>" + home.name() + " <white>· <click:run_command:/home "
                         + home.name() + "><hover:show_text:'" + SimpleHomes.core().getLang()
                        .getString("command.homes.clickToTravel") + "'>" + "<gray>" + + home.location().getBlockX()
                        + " " + home.location().getBlockY() + " " + home.location().getBlockZ());
                lines.add(line);
            }

            String header;
            if (SimpleHomes.core().hasPapi)
                header = PAPI.applyPlaceholders(player, SimpleHomes.core().getLang().getString("command.homes" +
                        ".headerYours", "null"));
            else
                header = SimpleHomes.core().getLang().getString("command.homes.headerYours", "null");

            Paginate paginate = new Paginate(lines, MiniMessage.get().parse(header), 8, "homes ");
            Component toSend = args.length == 0 ? paginate.getPage(1) : paginate.getPage(Integer.parseInt(args[0]));

            // If the player specified a page that doesn't exist
            if (toSend == null) {
                sendMessageWithReplacement(player, "command.homes.invalidPage", "%max%", String.valueOf(paginate.getMaxPage()));
                return true;
            }

            // Send the paginated message
            sendFormattedMessage(player, toSend);
        }

        // Player is viewing someone else's homes
        else {

            // Check if the player has permission
            if (!player.hasPermission("simplehomes.homes.other")) {
                sendMessage(player, "command.noPermission");
                return true;
            }

            HomesPlayer target = SimpleHomes.core().getHomesPlayer(args[0]);

            // Make sure the player is found
            if (target == null) {
                sendMessageWithReplacement(player, "command.unknownPlayer", "%player%", args[0]);
                return true;
            }

            // Check if the player has no homes
            if (target.getTotalHomes() == 0) {
                sendMessageWithReplacement(player, "command.homes.noHomesOther", "%player%", target.getLastSeenName());
                return true;
            }

            // Build pagination for homes
            List<Component> lines = new ArrayList<>();
            for (Home home : target.getHomes()) {
                Component line;

                if (player.hasPermission("simplehomes.home.other"))
                    line = MiniMessage.get().parse("<aqua>" + home.name() + " <white>· <click:run_command:/home "
                            + target.getLastSeenName() + " " + home.name() + "><hover:show_text:'" +
                            SimpleHomes.core().getLang().getString("command.homes.clickToTravel") + "'>" +
                            "<gray>" + + home.location().getBlockX() + " " + home.location().getBlockY() + " "
                            + home.location().getBlockZ());
                else
                    line = MiniMessage.get().parse("<aqua>" + home.name() + " <white>· <gray>"
                            + home.location().getBlockX() + " " + home.location().getBlockY() + " "
                            + home.location().getBlockZ());
                lines.add(line);
            }

            String header;
            if (SimpleHomes.core().hasPapi)
                header = PAPI.applyPlaceholders(player, SimpleHomes.core().getLang().getString("command.homes" +
                        ".headerOthers", "null").replace("%player%", target.getLastSeenName()));
            else
                header = SimpleHomes.core().getLang().getString("command.homes" +
                        ".headerOthers", "null").replace("%player%", target.getLastSeenName());

            Paginate paginate = new Paginate(lines, MiniMessage.get().parse(header), 8, "homes "
                    + target.getLastSeenName() + " ");

            // Avoid exceptions with Integer#parseInt
            if (args.length == 2 && !isInt(args[1])) {
                sendMessageWithReplacement(player, "command.homes.invalidPage", "%max%", String.valueOf(paginate.getMaxPage()));
                return true;
            }

            Component toSend = args.length == 1 ? paginate.getPage(1) : paginate.getPage(Integer.parseInt(args[1]));

            // If the player specified a page that doesn't exist
            if (toSend == null) {
                sendMessageWithReplacement(player, "command.homes.invalidPage", "%max%", String.valueOf(paginate.getMaxPage()));
                return true;
            }

            // Send the paginated message
            sendFormattedMessage(player, toSend);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1 && player.hasPermission("simplehomes.homes.other")) {
                return TabCompleterBase.getOnlinePlayers(args[0]);
            } else
                return Collections.emptyList();
        }
        return null;
    }

    public boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    /**
     * Used to paginate long lists of text, such as help, in chat.
     */
    public static class Paginate {

        private final int pages;
        private int currentPage;
        private final Component header;
        private final Map<Integer, Component> pageMap;

        // Config values
        public final String PAGE = getConfigString("command.homes.page");
        public final String PREV = getConfigString("command.homes.prev");
        public final String PREVIOUS_PAGE = getConfigString("command.homes.previousPage");
        public final String NO_PREVIOUS_PAGE = getConfigString("command.homes.noPreviousPage");
        public final String NEXT = getConfigString("command.homes.next");
        public final String NEXT_PAGE = getConfigString("command.homes.nextPage");
        public final String NO_NEXT_PAGE = getConfigString("command.homes.noNextPage");

        /**
         * Generate pagination for when there is a long list of values to display in chat.
         *
         * @param lines        The list of values to display across pages.
         * @param header       The header to display at the top of every page.
         * @param linesPerPage How many lines to display on each page.
         * @param command      The command, not including page number, to use for moving between pages.
         */
        public Paginate(List<Component> lines, Component header, int linesPerPage, String command) {
            this.header = header;
            this.pages = lines.size() % linesPerPage == 0 ? lines.size() / linesPerPage : (lines.size() / linesPerPage) + 1;
            this.currentPage = 1;
            this.pageMap = new HashMap<>();

            for (int i = 0; i <= lines.size(); i++) {
                List<Component> linesForPage = new ArrayList<>();
                for (int j = i; j <= (i + (linesPerPage - 1)); j++) {
                    try {
                        linesForPage.add(lines.get(j));
                    } catch (Exception ex) {
                        break;
                    }
                }
                pageMap.put(currentPage, createPage(linesForPage, currentPage, command));
                currentPage++;
                i += (linesPerPage - 1);
            }
        }

        public Component getPage() {
            return pageMap.get(currentPage);
        }

        public Component getPage(int page) {
            currentPage = page;
            return getPage();
        }

        public int getMaxPage() {
            return pages;
        }

        /**
         * Create a new page of values. The page formatting is the same as the /help pages.
         *
         * @param lines      The list of values for this page.
         * @param pageNumber The page number assigned to this page.
         * @param command    The command, not including page number, to use for moving between pages.
         * @return The created page ready to send formatted.
         */
        public Component createPage(List<Component> lines, int pageNumber, String command) {
            Component prev = currentPage > 1 ? MiniMessage.get().parse("<click:run_command:/" + command + " "
                    + (pageNumber - 1) + "><hover:show_text:'<aqua>" + PREVIOUS_PAGE + "'><gold>[<aqua>" + PREV + "<gold>] ") :
                    MiniMessage.get().parse("<hover:show_text:'<gray>" + NO_PREVIOUS_PAGE + "'><gold>[<gray>" + PREV + "<gold>] ");
            Component title = header.append(MiniMessage.get().parse("<aqua> - " + PAGE + " " + pageNumber + "/" + pages));
            Component next = currentPage < pages ? MiniMessage.get().parse(" <click:run_command:/" + command + " "
                    + (pageNumber + 1) + "><hover:show_text:'<aqua>" + NEXT_PAGE + "'><gold>[<aqua>" + NEXT + "<gold>]") :
                    MiniMessage.get().parse(" <hover:show_text:'<gray>" + NO_NEXT_PAGE + "'><gold>[<gray>" + NEXT + "<gold>]");
            TextComponent.Builder page = Component.text().append(prev).append(title).append(next);
            for (Component line : lines)
                page.append(Component.newline()).append(line);
            return page.asComponent();
        }

        public String getConfigString(String path) {
            return SimpleHomes.core().getLang().getString(path);
        }
    }
}
