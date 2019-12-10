package tink.co.exchange.rates.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import tink.co.exchange.rates.App;
import tink.co.exchange.rates.model.Currencies;
import tink.co.exchange.rates.model.Currency;

import static tink.co.exchange.rates.Config.*;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBCallback dbCallback;

    private List<Currency> list = new ArrayList<>();

    public DBHelper(Context context) {
        super(context, DB_PATH + DB_NAME, null, DB_VERSION);
    }

    private static DBHelper instance = new DBHelper(App.getContext());

    public static DBHelper getInstance() {
        return instance;
    }

    public void setCallback(DBCallback callback) {
        dbCallback = callback;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + CURRENCIES + " ("
                + "_id integer primary key autoincrement,"
                + CURRENCY + " text,"
                + COST + " real" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void getCurrencies() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                list.clear();
                Cursor cursor = db.rawQuery("SELECT * FROM currencies", null);
                cursor.moveToFirst();
                do {
                    list.add(new Currency(
                            cursor.getString(cursor.getColumnIndexOrThrow(CURRENCY)),
                            cursor.getFloat(cursor.getColumnIndexOrThrow(COST))
                    ));
                } while (cursor.moveToNext());
                cursor.close();
                db.close();

                if (dbCallback != null) {
                    dbCallback.onCurrencyListReady(list);
                }
            }
        });

    }

    public void saveCurrencies(List<Currency> list) {

        ContentValues cv = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("delete from " + CURRENCIES);
        db.execSQL("vacuum");

        for (Currency currency : list) {
            cv.put("currency", currency.getCurrency());
            cv.put("cost", currency.getCost());

            db.insert(CURRENCIES, null, cv);
        }
        db.close();
    }
}
