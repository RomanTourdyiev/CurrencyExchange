package tink.co.exchange.rates.util;

import android.util.Pair;

import java.util.List;
import java.util.Map;

import tink.co.exchange.rates.model.Currency;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public interface APICallback {
    void onCurrencyListReady(List<Currency> currencies);
    void onCurrencyHistoryReady(List<Pair<String, Double>> history, String currency);
    void onFailure();
}
