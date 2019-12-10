package tink.co.exchange.rates.model;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public class Currency {

    private String currency;
    private float cost;

    private Currency() {
    }

    public Currency(String currency, float cost) {
        this.currency = currency;
        this.cost = cost;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currency='" + currency + '\'' +
                ", cost='" + cost + '\'' +
                '}';
    }
}
