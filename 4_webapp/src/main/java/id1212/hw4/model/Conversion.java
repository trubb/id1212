package id1212.hw4.model;

public class Conversion {

    private String baseCurrency;
    private String quoteCurrency;
    private double amount;
    private double result;

    public Conversion() {
    }

    public Conversion(String baseCurrency, String quoteCurrency, double amount, double result) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.amount = amount;
        this.result = result;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
