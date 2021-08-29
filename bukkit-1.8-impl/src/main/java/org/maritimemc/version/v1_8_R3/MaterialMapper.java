package org.maritimemc.version.v1_8_R3;

import org.bukkit.Material;
import org.maritimemc.abstraction.IMaterialMapper;

public class MaterialMapper implements IMaterialMapper {
    @Override
    public Material grassBlock() {
        return Material.GRASS;
    }

    @Override
    public Material sword(ToolMaterial type) {
        return Material.valueOf(type.name() + "_SWORD");
    }

    @Override
    public Material stainedClay() {
        return Material.STAINED_CLAY;
    }

    @Override
    public Material book(BookState bookState) {
        if (bookState == BookState.WRITABLE_BOOK) {
            return Material.BOOK_AND_QUILL;
        } else if (bookState == BookState.WRITTEN_BOOK) {
            return Material.WRITTEN_BOOK;
        } else {
            return Material.BOOK;
        }
    }

    @Override
    public Material clock() {
        return Material.WATCH;
    }

    @Override
    public Material cookedFish() {
        return Material.COOKED_FISH;
    }

    @Override
    public Material melon() {
        return Material.MELON;
    }

    @Override
    public Material sign() {
        return Material.SIGN;
    }
}
