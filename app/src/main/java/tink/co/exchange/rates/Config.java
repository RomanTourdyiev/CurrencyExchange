package tink.co.exchange.rates;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public class Config {

    public final static String HOST = "https://api.exchangeratesapi.io/";
    public final static String LATEST = "latest?base=USD";
    public final static String HISTORY = "history?start_at=%s&end_at=%s&base=USD&symbols=%s";
    public final static String API_INTERACTION = "api_interaction";
    public final static String DB_PATH = Environment.getExternalStorageDirectory() + "/Currency_rates/";
    public final static String DB_NAME = "exchange.sqlite";
    public final static int DB_VERSION = 1;
    public final static String CURRENCIES = "currencies";
    public final static String CURRENCY = "currency";
    public final static String COST = "cost";
    public final static String RATES = "rates";
    public final static String DATE = "date";
    public final static String HISTORY_LIST = "history";
    public final static int REQUEST_PERMISSIONS_CODE = 4444;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
}
