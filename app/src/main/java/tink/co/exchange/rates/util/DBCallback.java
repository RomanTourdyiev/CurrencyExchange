package tink.co.exchange.rates.util;

import java.util.List;

import tink.co.exchange.rates.model.Currency;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public interface DBCallback {
    void onCurrencyListReady(List<Currency> currencies);
    void onFailure();
}
