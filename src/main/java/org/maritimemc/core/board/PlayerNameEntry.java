package org.maritimemc.core.board;

public class PlayerNameEntry {

    private final String name;
    private final boolean iWantToBeRemoved;

    public PlayerNameEntry(String name) {
        this.name = name;
        this.iWantToBeRemoved = false;
    }

    public PlayerNameEntry(String name, boolean iWantToBeRemoved) {
        this.name = name;
        this.iWantToBeRemoved = iWantToBeRemoved;
    }

    public String getName() {
        return name;
    }

    public boolean isiWantToBeRemoved() {
        return iWantToBeRemoved;
    }

    @Override
    public String toString() {
        return "PlayerNameEntry{" +
                "name='" + name + '\'' +
                ", iWantToBeRemoved=" + iWantToBeRemoved +
                '}';
    }
}
