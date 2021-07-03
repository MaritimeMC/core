package org.maritimemc.core.board;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class ScoreboardLine {

    private final char colour;
    private final int line;
    private final Team team;
    private String content;

    private boolean iWantToBeRemoved = false;

    public ScoreboardLine(char colour, int line, Team team, String content) {
        this.colour = colour;
        this.line = line;
        this.team = team;
        this.content = content;
    }

    public char getColour() {
        return colour;
    }

    public int getLine() {
        return line;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void applyContentToTeam() {
        if (content.length() > 16) {
            String before = content.substring(0, 16);
            String after = ChatColor.getLastColors(before) + content.substring(16);

            if (after.length() > 16) {
                throw new UnsupportedOperationException("Content is > 32 characters long: " + content);
            }

            team.setPrefix(before);
            team.setSuffix(after);
        } else {
            team.setPrefix(content);
        }
    }

    @Override
    public ScoreboardLine clone() {
        return new ScoreboardLine(colour, line, team, content);
    }

    public boolean isiWantToBeRemoved() {
        return iWantToBeRemoved;
    }

    public void setiWantToBeRemoved(boolean iWantToBeRemoved) {
        this.iWantToBeRemoved = iWantToBeRemoved;
    }

}
