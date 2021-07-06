package org.maritimemc.core.currency;

import org.maritimemc.data.versioning.MaritimeMaterial;

public class MaritimeCurrency {

    public static final Currency COINS = new Currency("COINS","Coin", "Coins", false, MaritimeMaterial.from("GOLD_INGOT"));
    public static final Currency PEARLS = new Currency("PEARLS", "Pearl", "Pearls", true, MaritimeMaterial.from("ENDER_PEARL"));

}
