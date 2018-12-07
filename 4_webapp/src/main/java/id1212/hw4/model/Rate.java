package id1212.hw4.model;

import javax.persistence.*;

@Entity
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double rate;

    @ManyToOne
    private Currency baseCurrency;

    @ManyToOne
    private Currency quoteCurrency;

    public Rate() {
    }

    public Rate(double rate, Currency baseCurrency, Currency quoteCurrency) {
        this.rate = rate;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(Currency quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }
}
