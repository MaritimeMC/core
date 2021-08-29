package org.maritimemc.abstraction;

import org.bukkit.Material;

public interface IMaterialMapper {

    enum ToolMaterial {
        WOOD,
        STONE,
        IRON,
        GOLD,
        DIAMOND,
        NETHERITE;
    }

    enum BookState {
        EMPTY_BOOK,
        WRITTEN_BOOK,
        WRITABLE_BOOK;
    }

    Material grassBlock();
    Material sword(ToolMaterial type);
    Material stainedClay();
    Material book(BookState bookState);
    Material clock();
    Material cookedFish();
    Material melon();
    Material sign();

}
