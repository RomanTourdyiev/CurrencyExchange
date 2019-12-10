package tink.co.exchange.rates.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public class Currencies {
    @SerializedName("rates")
    private Map<String, Float> currencyMap;

    public Map<String, Float> getCurrencyMap() {
        return currencyMap;
    }

    public void setCurrencyMap(Map<String, Float> currencyMap) {
        this.currencyMap = currencyMap;
    }
}
