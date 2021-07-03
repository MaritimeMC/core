package org.maritimemc.core.board;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UtilLog;
import org.maritimemc.data.perm.PermissionGroup;

import java.util.*;

public class MaritimeScoreboard {

    // We only need 15 as there will only ever be 15 lines on the scoreboard.
    private static final char[] COLOUR_CHARS = "1234567890abcde".toCharArray();

    private final List<IndexedLine> registeredLines = new ArrayList<>();

    private final List<ScoreboardLine> lines = new ArrayList<>();
    private final List<ScoreboardLine> linesBuffered = new ArrayList<>();

    private final Map<ChatColor, Set<PlayerNameEntry>> colourNames = new HashMap<>();
    private final Map<ChatColor, Set<PlayerNameEntry>> namesBuffered;

    private final Scoreboard scoreboard;
    private final Objective objective;

    private final Player player;

    public MaritimeScoreboard(Player player) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("minedroid", "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < COLOUR_CHARS.length; i++) {
            char c = COLOUR_CHARS[i];

            Team team = scoreboard.registerNewTeam("text-" + i);
            team.addEntry(generateEntry(c));

            // Setup default empties inside lines.
            lines.add(new ScoreboardLine(c, 15 - i, team, ""));
        }

        for (char colourChar : COLOUR_CHARS) {
            ChatColor color = ChatColor.getByChar(colourChar);

            colourNames.put(color, new HashSet<>());

            Team t = scoreboard.registerNewTeam(generateColourTeamName(color));
            t.setPrefix(color + "");
        }

        namesBuffered = new HashMap<>(colourNames);
    }

    public Player getPlayer() {
        return player;
    }

    public void setDisplayName(String displayName) {
        objective.setDisplayName(Formatter.format(displayName));
    }

    private String generateEntry(char c) {
        return ChatColor.getByChar(c).toString() + ChatColor.RESET;
    }

    private String generateColourTeamName(ChatColor color) {
        return color.name();
    }

    public void sendUpdates() {
        for (ScoreboardLine scoreboardLine : linesBuffered) {

            ScoreboardLine old = getByLine(scoreboardLine.getLine());
            assert old != null;

            if (old.getContent().equals(scoreboardLine.getContent()) && !scoreboardLine.isiWantToBeRemoved()) {
                // Content has not been changed and it's not being removed, but it's still buffered? Save packets!
                continue;
            }

            if (scoreboardLine.isiWantToBeRemoved()) {
                scoreboard.resetScores(generateEntry(scoreboardLine.getColour()));
                old.setContent("");

                continue;
            }

            objective.getScore(generateEntry(scoreboardLine.getColour())).setScore(scoreboardLine.getLine());
            scoreboardLine.applyContentToTeam();

            // Update the lines list to the currently sent line.
            old.setContent(scoreboardLine.getContent());
        }

        linesBuffered.clear();

        for (Map.Entry<ChatColor, Set<PlayerNameEntry>> chatColorSetEntry : namesBuffered.entrySet()) {

            ChatColor color = chatColorSetEntry.getKey();
            Set<PlayerNameEntry> set = chatColorSetEntry.getValue();

            if (!set.isEmpty()) {

                for (PlayerNameEntry e : set) {
                    if (e.isiWantToBeRemoved()) {
                        removeFromNamesWhereNameEquals(color, e.getName());
                        scoreboard.getTeam(generateColourTeamName(color)).removeEntry(e.getName());
                    } else {
                        colourNames.get(color).add(e);
                        scoreboard.getTeam(generateColourTeamName(color)).addEntry(e.getName());
                    }
                }

                set.clear();
            }
        }

    }

    public void register(IndexedLine... indexedLines) {
        Collections.addAll(registeredLines, indexedLines);
    }

    public void set() {
        player.setScoreboard(scoreboard);
    }

    public void addPlayerToColour(ChatColor color, Player player) {
        removeFromBuffer(player);

        namesBuffered.get(color).add(new PlayerNameEntry(player.getName()));
    }

    private void removeFromBuffer(Player player) {
        for (Map.Entry<ChatColor, Set<PlayerNameEntry>> chatColorSetEntry : namesBuffered.entrySet()) {

            PlayerNameEntry entry = null;
            for (PlayerNameEntry playerNameEntry : chatColorSetEntry.getValue()) {
                if (player.getName().equals(player.getName())) {
                    entry = playerNameEntry;
                }
            }

            chatColorSetEntry.getValue().remove(entry);
        }
    }

    public void addPlayersToColour(ChatColor color, Player... players) {
        for (Player player : players) {
            addPlayerToColour(color, player);
        }
    }

    public void removePlayer(Player player) {
        TeamedPlayerEntry entry = getEntryByName(player.getName());
        if (entry != null) {
            removePlayer(entry.getColor(), entry.getPlayerNameEntry());
        }
    }

    private void removePlayer(ChatColor color, PlayerNameEntry playerNameEntry) {
        namesBuffered.get(color).add(
                new PlayerNameEntry(playerNameEntry.getName(), true)
        );
    }

    private TeamedPlayerEntry getEntryByName(String name) {
        for (Map.Entry<ChatColor, Set<PlayerNameEntry>> chatColorSetEntry : colourNames.entrySet()) {
            for (PlayerNameEntry playerNameEntry : chatColorSetEntry.getValue()) {
                if (playerNameEntry.getName().equals(name)) {
                    return new TeamedPlayerEntry(chatColorSetEntry.getKey(), playerNameEntry);
                }
            }
        }

        return null;
    }

    private void removeFromNamesWhereNameEquals(ChatColor color, String s) {
        PlayerNameEntry entry = null;

        for (PlayerNameEntry playerNameEntry : colourNames.get(color)) {
            if (playerNameEntry.getName().equals(s)) {
                entry = playerNameEntry;
            }
        }

        if (entry != null) {
            colourNames.get(color).remove(entry);
        }
    }

    private ScoreboardLine getByLine(int line) {
        for (ScoreboardLine scoreboardLine : lines) {
            if (scoreboardLine.getLine() == line) {
                return scoreboardLine;
            }
        }

        return null;
    }

    private int getIndexByRegisteredLine(IndexedLine line) {
        int index = registeredLines.indexOf(line);

        if (index == -1) {
            return -1;
        }

        return registeredLines.size() - registeredLines.indexOf(line);
    }

    public void setValue(IndexedLine line, String value) {
        int index = getIndexByRegisteredLine(line);

        if (index == -1) {
            UtilLog.log("Tried to set " + value + " but line wasn't registered.");
            return;
        }

        setValue(index, value);
    }

    public void setValue(int line, String value) {
        value = Formatter.format(value);

        if (value.equals("")) {
            value = ChatColor.RESET.toString();
        }

        ScoreboardLine byLine = getByLine(line);
        if (byLine == null) {
            throw new NullPointerException("Line provided was not between 15 and 1.");
        }

        ScoreboardLine clone = byLine.clone();
        clone.setContent(value);

        removeLineFromBuffer(line);
        linesBuffered.add(clone);
    }

    public void applyRankTags(Player... players) {
        for (Player player : players) {
            PermissionGroup highest = Locator.locate(ProfileManager.class).getCached(player).getHighestPrimaryGroup();

            addPlayerToColour(Formatter.toChatColor(highest.getColour()), player);
        }
    }

    private void removeLineFromBuffer(int line) {
        ScoreboardLine buffer = null;
        for (ScoreboardLine scoreboardLine : linesBuffered) {
            if (scoreboardLine.getLine() == line) {
                buffer = scoreboardLine;
            }
        }

        if (buffer != null) {
            linesBuffered.remove(buffer);
        }
    }

    public void removeLine(IndexedLine line) {
        int index = getIndexByRegisteredLine(line);

        if (index == -1) {
            return;
        }

        removeLine(index);
    }

    public void removeLine(int line) {
        ScoreboardLine byLine = getByLine(line);
        if (byLine == null) {
            throw new NullPointerException("Line provided was null.");
        }

        ScoreboardLine clone = byLine.clone();
        clone.setContent("");
        clone.setiWantToBeRemoved(true);

        removeLineFromBuffer(line);
        linesBuffered.add(clone);
    }

    public void clearRegister() {
        registeredLines.clear();
    }

    public void clearDisplayBoard() {
        for (int i = 1; i <= 15; i++) {
            removeLine(i);
        }
    }


}