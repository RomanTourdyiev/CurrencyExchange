package tink.co.exchange.rates.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tink.co.exchange.rates.App;
import tink.co.exchange.rates.R;
import tink.co.exchange.rates.model.Currencies;
import tink.co.exchange.rates.model.Currency;
import tink.co.exchange.rates.ui.adapter.CurrencyAdapter;
import tink.co.exchange.rates.ui.fragment.HistoryFragment;
import tink.co.exchange.rates.util.APICallback;
import tink.co.exchange.rates.util.APIFactory;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static androidx.recyclerview.widget.RecyclerView.VERTICAL;
import static tink.co.exchange.rates.Config.CURRENCY;
import static tink.co.exchange.rates.Config.HISTORY;
import static tink.co.exchange.rates.Config.HISTORY_LIST;
import static tink.co.exchange.rates.Config.REQUEST_PERMISSIONS_CODE;

public class MainActivity extends AppCompatActivity implements APICallback {

    private CurrencyAdapter currencyAdapter;
    private APIFactory apiFactory;
    private static FragmentManager fragmentManager;

    private RecyclerView recyclerView;
    private ProgressBar progressbar;
    private TextView error;

    private List<Currency> list = new ArrayList<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            initApp();
        }
    }

    @Override
    public void onCurrencyListReady(List<Currency> currencies) {
        list.clear();
        list.addAll(currencies);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currencyAdapter.notifyDataSetChanged();
                progressbar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCurrencyHistoryReady(List<Pair<String, Double>> history, String currency) {

        final HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        Type listType = new TypeToken<List<Pair<String, Double>>>() {
        }.getType();
        bundle.putString(HISTORY_LIST, new Gson().toJson(history, listType));
        bundle.putString(CURRENCY, currency);
        fragment.setArguments(bundle);

        fragmentManager
                .beginTransaction()
                .replace(R.id.frame_container, fragment, HISTORY_LIST)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(HISTORY_LIST)
                .commit();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressbar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onFailure() {
        progressbar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        error.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setActivity(this);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();

        if (ActivityCompat.checkSelfPermission(App.getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initApp();
        } else {
            requestPermission();
        }
    }

    @Override
    protected void onDestroy() {
        apiFactory.setCallback(null);
        super.onDestroy();
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recycler_view);
        progressbar = findViewById(R.id.progressbar);
        error = findViewById(R.id.error);
    }

    private void initViews() {

        error.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);

        list.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        currencyAdapter = new CurrencyAdapter(list);
        recyclerView.setAdapter(currencyAdapter);
    }

    private void initApp() {
        fragmentManager = getSupportFragmentManager();
        apiFactory = APIFactory.getInstance();
        apiFactory.setCallback(this);
        apiFactory.getCurrencies();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSIONS_CODE);
    }

    public void showLoading(){
        progressbar.setVisibility(View.VISIBLE);
    }
}
