package tink.co.exchange.rates.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import tink.co.exchange.rates.App;
import tink.co.exchange.rates.model.Currencies;
import tink.co.exchange.rates.model.Currency;

import static tink.co.exchange.rates.Config.API_INTERACTION;
import static tink.co.exchange.rates.Config.DATE;
import static tink.co.exchange.rates.Config.RATES;
import static tink.co.exchange.rates.Config.simpleDateFormat;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public class APIFactory implements Callback, DBCallback {
    private static SharedPreferences sharedPreferences;
    private static Gson gson;
    private static APIHelper apiHelper;
    private static DBHelper dbHelper;
    private APICallback apiCallback;
    private static APIFactory instance = new APIFactory();
    private List<Currency> list = new ArrayList<>();
    private List<Pair<String, Double>> currHistory = new ArrayList<>();
    private String currency;

    public static APIFactory getInstance() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        gson = new Gson();
        apiHelper = APIHelper.getInstance();
        dbHelper = DBHelper.getInstance();
        apiHelper.setCallback(instance);
        dbHelper.setCallback(instance);
        return instance;
    }

    public void setCallback(APICallback callback) {
        this.apiCallback = callback;
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        apiCallback.onFailure();
    }

    @Override
    public void onCurrencyListReady(List<Currency> currencies) {
        if (apiCallback != null) {
            apiCallback.onCurrencyListReady(currencies);
        }
    }

    @Override
    public void onFailure() {
        apiCallback.onFailure();
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

        try {
            String responce = response.body().string();
            JSONObject jsonObject = new JSONObject(responce);

            if (jsonObject.has(DATE)) {
                // this is main list
                Map<String, Float> currenyMap = gson.fromJson(jsonObject.toString(), Currencies.class).getCurrencyMap();
                list.clear();
                for (String key : currenyMap.keySet()) {
                    list.add(new Currency(key, currenyMap.get(key)));
                }

                dbHelper.saveCurrencies(list);
                sharedPreferences.edit().putLong(API_INTERACTION, System.currentTimeMillis()).apply();
                if (apiCallback != null) {
                    apiCallback.onCurrencyListReady(list);
                }
            } else {
                // this is a one currency history
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> history = gson.fromJson(jsonObject.getJSONObject(RATES).toString(), type);
                if (apiCallback != null) {
                    currHistory.clear();
                    for (String key : history.keySet()) {
                        for (String keyS : ((LinkedTreeMap<String, Double>) history.get(key)).keySet()) {
                            currency = keyS;
                            currHistory.add(new Pair<>(key, ((LinkedTreeMap<String, Double>) history.get(key)).get(keyS)));
                        }
                    }
                    apiCallback.onCurrencyHistoryReady(currHistory, currency);
                }
            }

        } catch (JSONException je) {
            je.printStackTrace();
        }

    }

    public void getCurrencies() {
        if (System.currentTimeMillis() - sharedPreferences.getLong(API_INTERACTION, 0L) > TimeUnit.MINUTES.toMillis(10)) {
            // more than 10 minutes - requesting from server
            Log.d("apiFactory", "more than 10 minutes - requesting from server");
            apiHelper.getCurrencies();
        } else {
            // less than 10 min - showing local cache
            Log.d("apiFactory", "less than 10 min - showing local cache");
            dbHelper.getCurrencies();
        }
    }

    public void getCurrencyHistory(final String currency) {
        apiHelper.getCurrencyHistory(
                simpleDateFormat.format(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(8)),
                simpleDateFormat.format(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)),
                currency);
    }
}
