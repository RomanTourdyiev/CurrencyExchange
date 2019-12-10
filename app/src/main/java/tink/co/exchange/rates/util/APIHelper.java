package tink.co.exchange.rates.util;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Locale;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static tink.co.exchange.rates.Config.HISTORY;
import static tink.co.exchange.rates.Config.HOST;
import static tink.co.exchange.rates.Config.LATEST;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public class APIHelper {

    private static Callback okHTTPcallback;
    private static OkHttpClient client;

    private static APIHelper instance = new APIHelper();

    public static APIHelper getInstance() {
        client = new OkHttpClient();
        return instance;
    }

    public void setCallback(Callback callback) {
        okHTTPcallback = callback;
    }

    public void getCurrencies() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(HOST + LATEST)
                        .get()
                        .build();

                if (okHTTPcallback != null) {
                    client.newCall(request).enqueue(okHTTPcallback);
                }
            }
        });
    }

    public void getCurrencyHistory(final String from, final String to, final String currency) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(HOST + String.format(Locale.ENGLISH, HISTORY, from, to, currency))
                        .get()
                        .build();

                if (okHTTPcallback != null) {
                    client.newCall(request).enqueue(okHTTPcallback);
                }
            }
        });
    }
}
