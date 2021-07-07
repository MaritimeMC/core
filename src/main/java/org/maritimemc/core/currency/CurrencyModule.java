package org.maritimemc.core.currency;

import org.maritimemc.core.Module;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CurrencyModule implements Module {

    private final Map<UUID, CachedCurrencyData> localCurrencyCache;

    private final CurrencyDataManager currencyDataManager;

    public CurrencyModule() {
        this.currencyDataManager = new CurrencyDataManager();
        this.localCurrencyCache = new HashMap<>();

        Module.registerEvents(this);
    }

    public int get(UUID uuid, Currency currency) {
        return get(uuid, currency, -1);
    }

    public int get(UUID uuid, Currency currency, int gameId) {

        if (localCurrencyCache.containsKey(uuid)) {
            CachedCurrencyData cached = localCurrencyCache.get(uuid);

            if (gameId == -1) {
                // GLOBAL CURRENCY
                if (!cached.hasRecordFor(currency)) {
                    // Not exists, fetch from DB
                    int amount = currencyDataManager.getGlobalCurrency(uuid, currency);

                    if (amount == -1) {
                        // Nothing in DB either!
                        amount = 0;
                    }

                    // Update cache
                    cached.setCurrency(currency, amount);
                    return amount;
                } else {
                    return cached.getAmount(currency);
                }
            } else {
                // LOCAL CURRENCY
                if (!cached.hasRecordFor(currency, gameId)) {
                    // Not exists, fetch from DB
                    int amount = currencyDataManager.getLocalCurrency(uuid, currency, gameId);

                    if (amount == -1) {
                        // Nothing in DB either!
                        amount = 0;
                    }

                    // Update cache
                    cached.setCurrency(currency, gameId, amount);
                    return amount;
                } else {
                    return cached.getAmount(currency, gameId);
                }
            }
        } else {
            if (gameId == -1) {
                int globalCurrency = currencyDataManager.getGlobalCurrency(uuid, currency);

                if (globalCurrency == -1) {
                    globalCurrency = 0;
                }

                CachedCurrencyData cachedCurrencyData = new CachedCurrencyData();
                cachedCurrencyData.setCurrency(currency, globalCurrency);

                localCurrencyCache.put(uuid, cachedCurrencyData);
                return globalCurrency;
            } else {
                int localCurrency = currencyDataManager.getLocalCurrency(uuid, currency, gameId);

                if (localCurrency == -1) {
                    localCurrency = 0;
                }

                CachedCurrencyData cachedCurrencyData = new CachedCurrencyData();
                cachedCurrencyData.setCurrency(currency, gameId, localCurrency);

                localCurrencyCache.put(uuid, cachedCurrencyData);
                return localCurrency;
            }
        }
    }

    public void set(UUID uuid, Currency currency, int amount) {
        set(uuid, currency, -1, amount);
    }

    public void set(UUID uuid, Currency currency, int gameId, int amount) {
        if (gameId == -1) {
            currencyDataManager.setGlobalCurrency(uuid, currency, amount);
        } else {
            currencyDataManager.setLocalCurrency(uuid, currency, gameId, amount);
        }

        CachedCurrencyData cachedCurrencyData;
        if (localCurrencyCache.containsKey(uuid)) {
            cachedCurrencyData = localCurrencyCache.get(uuid);
        } else {
            cachedCurrencyData = new CachedCurrencyData();
            localCurrencyCache.put(uuid, cachedCurrencyData);
        }

        if (gameId == -1) {
            cachedCurrencyData.setCurrency(currency, amount);
        } else {
            cachedCurrencyData.setCurrency(currency, gameId, amount);
        }
    }

    public void add(UUID uuid, Currency currency, int amount) {
        add(uuid, currency, -1, amount);
    }

    public void add(UUID uuid, Currency currency, int gameId, int amount) {
        if (gameId == -1) {
            int before = get(uuid, currency);
            amount += before;

            set(uuid, currency, amount);
        } else {
            int before = get(uuid, currency, gameId);

            amount += before;
            set(uuid, currency, gameId, amount);
        }
    }

    public void remove(UUID uuid, Currency currency, int amount) {
        remove(uuid, currency, -1, amount);
    }

    public void remove(UUID uuid, Currency currency, int gameId, int amount) {
        if (gameId == -1) {
            int before = get(uuid, currency);
            amount -= before;
            assert amount >= 0;

            set(uuid, currency, amount);
        } else {
            int before = get(uuid, currency, gameId);
            amount -= before;
            assert amount >= 0;

            set(uuid, currency, gameId, amount);
        }
    }

    public boolean has(UUID uuid, Currency currency, int amount) {
        return has(uuid, currency, -1, amount);
    }

    public boolean has(UUID uuid, Currency currency, int gameId, int amount) {
        if (gameId == -1) {
            return get(uuid, currency) >= amount;
        } else {
            return get(uuid, currency, gameId) >= amount;
        }
    }


}
