package dev.majek.simplehomes.util;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Allows for some static utility methods to be available to tab completer
 */
public abstract class TabCompleterBase implements TabCompleter {
    /**
     * Returns a list of the currently online players whose name starts with the given partial name.
     *
     * @param partialName the partial name.
     * @return a list of the currently online players whose name starts with the given partial name.
     */
    public static List<String> getOnlinePlayers(String partialName) {
        return filterStartingWith(partialName, Bukkit.getOnlinePlayers().stream().map(Player::getName));
    }

    /**
     * Joins all the arguments after the argument at the given index with the given delimiter.
     *
     * @param index the index.
     * @param delim the delimiter.
     * @param args  the arguments.
     * @return the result of joining the argument after the given index with the given delimiter.
     */
    public static String joinArgsBeyond(int index, String delim, String[] args) {
        ++index;
        String[] data = new String[args.length - index];
        System.arraycopy(args, index, data, 0, data.length);
        return String.join(delim, data);
    }

    /**
     * Filters the given stream by removing null or empty strings, or strings who do not start with the given prefix
     * (ignoring case).
     *
     * @param prefix the prefix to match.
     * @param stream the stream to filter.
     * @return the list of values left after the stream has been filtered.
     */
    public static List<String> filterStartingWith(String prefix, Stream<String> stream) {
        return stream.filter(s -> s != null && !s.isEmpty() && s.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Filters the given string list by removing null or empty strings, or strings who do not start with the given
     * prefix (ignoring case). This method is equivalent to calling filterStartingWith(prefix, strings.stream()).
     *
     * @param prefix  the prefix to match.
     * @param strings the strings to filter.
     * @return the list of values left after the strings have been filtered.
     */
    public static List<String> filterStartingWith(String prefix, Collection<String> strings) {
        return filterStartingWith(prefix, strings.stream());
    }
}

