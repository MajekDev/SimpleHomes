package dev.majek.homes.command;

import dev.majek.homes.Homes;
import dev.majek.homes.data.struct.Home;
import dev.majek.homes.data.struct.HomesPlayer;
import dev.majek.homes.util.TabCompleterBase;
import dev.majek.homes.util.TabExecutor;
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
        HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(player.getUniqueId());

        // Player is viewing their own homes
        if ((args.length == 1 && isInt(args[0])) || args.length == 0) {

            // Check if the player has permission
            if (!player.hasPermission("majekhomes.homes")) {
                sendMessage(player, "command.noPermission");
                return true;
            }

            // Check if the player has no homes
            if (homesPlayer.getTotalHomes() == 0) {
                sendMessage(player, "command.homes.noHomes");
                return true;
            }

            // Build pagination for homes
            List<String> lines = new ArrayList<>();
            for (Home home : homesPlayer.getHomes()) {
                String line = "&b" + home.getName() + " &f- ${hover-command,&7" + home.getLocation().getBlockX() + " "
                        + home.getLocation().getBlockY() + " " + home.getLocation().getBlockZ() + ","
                        + Homes.getCore().getLang().getString("command.homes.clickToTravel") + ",/home "
                        + home.getName() + "}";
                lines.add(line);
            }

            Paginate paginate = new Paginate(lines, Homes.getCore().getLang().getString("command.homes.headerYours"),
                    8, "homes ");
            String toSend = args.length == 0 ? paginate.getPage(1) : paginate.getPage(Integer.parseInt(args[0]));

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
            if (!player.hasPermission("majekhomes.homes.other")) {
                sendMessage(player, "command.noPermission");
                return true;
            }

            HomesPlayer target = Homes.getCore().getHomesPlayer(args[0]);

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
            List<String> lines = new ArrayList<>();
            for (Home home : target.getHomes()) {
                String line;
                if (player.hasPermission("majekhomes.home.other"))
                    line = "&b" + home.getName() + " &f- ${hover-command,&7" + home.getLocation().getBlockX() + " "
                            + home.getLocation().getBlockY() + " " + home.getLocation().getBlockZ() + ","
                            + Homes.getCore().getLang().getString("command.homes.clickToTravel") + ",/home "
                            + target.getLastSeenName() + " " + home.getName() + "}";
                else
                    line = "&b" + home.getName() + " &f- &7" + home.getLocation().getBlockX() + " "
                            + home.getLocation().getBlockY() + " " + home.getLocation().getBlockZ();
                lines.add(line);
            }

            Paginate paginate = new Paginate(lines, Homes.getCore().getLang().getString("command.homes.headerOthers", "null")
                    .replace("%player%", target.getLastSeenName()), 8, "homes " + target.getLastSeenName() + " ");

            // Avoid exceptions with Integer#parseInt
            if (args.length == 2 && !isInt(args[1])) {
                sendMessageWithReplacement(player, "command.homes.invalidPage", "%max%", String.valueOf(paginate.getMaxPage()));
                return true;
            }

            String toSend = args.length == 1 ? paginate.getPage(1) : paginate.getPage(Integer.parseInt(args[1]));

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
            if (args.length == 1 && player.hasPermission("majekhomes.homes.others")) {
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
        private final String header;
        private final Map<Integer, String> pageMap;

        // Config values
        public String PAGE = getConfigString("command.homes.page");
        public String PREV = getConfigString("command.homes.prev");
        public String PREVIOUS_PAGE = getConfigString("command.homes.previousPage");
        public String NO_PREVIOUS_PAGE = getConfigString("command.homes.noPreviousPage");
        public String NEXT = getConfigString("command.homes.next");
        public String NEXT_PAGE = getConfigString("command.homes.nextPage");
        public String NO_NEXT_PAGE = getConfigString("command.homes.noNextPage");

        /**
         * Generate pagination for when there is a long list of values to display in chat.
         *
         * @param lines        The list of values to display across pages.
         * @param header       The header to display at the top of every page.
         * @param linesPerPage How many lines to display on each page.
         * @param command      The command, not including page number, to use for moving between pages.
         */
        public Paginate(List<String> lines, String header, int linesPerPage, String command) {
            this.header = header;
            this.pages = lines.size() % linesPerPage == 0 ? lines.size() / linesPerPage : (lines.size() / linesPerPage) + 1;
            this.currentPage = 1;
            this.pageMap = new HashMap<>();

            for (int i = 0; i <= lines.size(); i++) {
                List<String> linesForPage = new ArrayList<>();
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

        public String getPage() {
            return pageMap.get(currentPage);
        }

        public String getPage(int page) {
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
        public String createPage(List<String> lines, int pageNumber, String command) {
            // TODO: switch this from string format with ChatParser to Component chaining
            String prevPage = currentPage > 1 ? "${hover-command,&6[&b" + PREV + "&6],&6" + PREVIOUS_PAGE + ",/"
                    + command + " " + (pageNumber - 1) + "} " : "${hover,&6[&7" + PREV + "&6],&6" + NO_PREVIOUS_PAGE + "} ";
            String title = this.header + " - " + PAGE + " " + pageNumber + "/" + pages;
            String nextPage = this.currentPage == pages ? " ${hover,&6[&7" + NEXT + "&6],&6" + NO_NEXT_PAGE + "}"
                    : " ${hover-command,&6[&b" + NEXT + "&6],&6" + NEXT_PAGE + ",/"
                    + command + " " + (pageNumber + 1) + "}";
            String page = prevPage + title + nextPage;
            StringBuilder buildLines = new StringBuilder();
            for (String line : lines)
                buildLines.append("\n").append(line);
            page = page + buildLines;
            return page;
        }

        public String getConfigString(String path) {
            return Homes.getCore().getLang().getString(path);
        }
    }
}
