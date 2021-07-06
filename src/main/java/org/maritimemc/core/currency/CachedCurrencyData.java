package org.maritimemc.core.currency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public class CachedCurrencyData {

    private final Map<Currency, Integer> currencyGlobalCountMap;
    private final Map<LocalCurrency, Integer> currencyLocalCountMap;

    public CachedCurrencyData() {
        this.currencyGlobalCountMap = new HashMap<>();
        this.currencyLocalCountMap = new HashMap<>();
    }

    public int getAmount(Currency currency) {
        return currencyGlobalCountMap.getOrDefault(currency, -1);
    }

    public int getAmount(Currency currency, int gameId) {
        for (LocalCurrency localCurrency : currencyLocalCountMap.keySet()) {
            if (localCurrency.getCurrency() == currency && localCurrency.getGameId() == gameId) {
                return currencyLocalCountMap.get(localCurrency);
            }
        }

        return -1;
    }

    public void setCurrency(Currency currency, int amount) {
        currencyGlobalCountMap.put(currency, amount);
    }

    public void setCurrency(Currency currency, int gameId, int amount) {
        LocalCurrency localCurrency = null;
        for (LocalCurrency loop : currencyLocalCountMap.keySet()) {
            if (loop.getCurrency() == currency && loop.getGameId() == gameId) {
                localCurrency = loop;
            }
        }

        if (localCurrency == null) {
            currencyLocalCountMap.put(new LocalCurrency(currency, gameId), amount);
        } else {
            currencyLocalCountMap.put(localCurrency, amount);
        }
    }

    public boolean hasRecordFor(Currency currency) {
        return getAmount(currency) != -1;
    }

    public boolean hasRecordFor(Currency currency, int gameId) {
        return getAmount(currency, gameId) != -1;
    }

    @RequiredArgsConstructor
    @Getter
    private static class LocalCurrency {
        private final Currency currency;
        private final int gameId;
    }
}
