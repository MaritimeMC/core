package org.maritimemc.version.v1_17_R2;

import org.bukkit.Material;
import org.maritimemc.abstraction.IMaterialMapper;

public class MaterialMapper implements IMaterialMapper {
    @Override
    public Material grassBlock() {
        return Material.GRASS_BLOCK;
    }

    @Override
    public Material sword(ToolMaterial type) {
        switch (type) {
            case GOLD:
                return Material.GOLDEN_SWORD;
            case IRON:
                return Material.IRON_SWORD;
            case WOOD:
                return Material.WOODEN_SWORD;
            case STONE:
                return Material.STONE_SWORD;
            case DIAMOND:
                return Material.DIAMOND_SWORD;
            case NETHERITE:
                return Material.NETHERITE_SWORD;
            default:
                return null;
        }
    }

    @Override
    public Material stainedClay() {
        return Material.LEGACY_STAINED_CLAY;
    }

    @Override
    public Material book(BookState bookState) {
        switch (bookState) {
            case EMPTY_BOOK:
                return Material.BOOK;
            case WRITTEN_BOOK:
                return Material.WRITTEN_BOOK;
            case WRITABLE_BOOK:
                return Material.WRITABLE_BOOK;
            default:
                return null;
        }
    }

    @Override
    public Material clock() {
        return Material.CLOCK;
    }

    @Override
    public Material cookedFish() {
        return Material.COOKED_SALMON;
    }

    @Override
    public Material melon() {
        return Material.MELON_SLICE;
    }

    @Override
    public Material sign() {
        return Material.OAK_SIGN;
    }
}
