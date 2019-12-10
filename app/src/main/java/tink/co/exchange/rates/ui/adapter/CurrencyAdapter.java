package tink.co.exchange.rates.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import tink.co.exchange.rates.App;
import tink.co.exchange.rates.R;
import tink.co.exchange.rates.model.Currency;
import tink.co.exchange.rates.util.APIFactory;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private List<Currency> list;

    public CurrencyAdapter(List<Currency> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_currency,
                        parent,
                        false);
        return new CurrencyAdapter.CurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int i) {

        View itemView = holder.itemView;
        final int position = holder.getAdapterPosition();

        holder.currency.setText(list.get(position).getCurrency());
        holder.cost.setText(String.format(Locale.ENGLISH, "$%f", list.get(position).getCost()));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIFactory.getInstance().getCurrencyHistory(list.get(position).getCurrency());
                App.getActivity().showLoading();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CurrencyViewHolder extends RecyclerView.ViewHolder {

        protected TextView currency;
        protected TextView cost;

        public CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);
            currency = itemView.findViewById(R.id.currency);
            cost = itemView.findViewById(R.id.cost);
        }
    }

}
