package id1212.hw4;

import id1212.hw4.integration.CurrencyRepository;
import id1212.hw4.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final CurrencyRepository currencyRepository;

    @Autowired
    public Application (CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Put the things into the db
     * @param strings   0 or more strings as superclass does it
     * @throws Exception
     */
    @Override
    public void run (String... strings) throws Exception {
        currenciesToDB();
        System.out.println("\n\n\nI AM DONE NOW");
        Iterable<Currency> currencies = currencyRepository.findAll();
        for (Currency currency : currencies) {
            System.out.println( currency.getCode() + " " + currency.getRate() );
        }
    }

    /**
     * Insert currencies into the database
     */
    private void currenciesToDB() {
        HashMap<String, Double> currencies = new HashMap<>();
        currencies.put("EUR", 1.0000);   // -> 1 EUR per EUR
        currencies.put("USD", 1.137249); // -> 0.87931490816 EUR per USD
        currencies.put("GBP", 0.891648); // -> 1.12151880563 EUR per GBP
        currencies.put("SEK", 10.252522);    // -> 0.09753697675 EUR per SEK

        for (Map.Entry<String, Double> entry : currencies.entrySet()) {
            currencyRepository.save( new Currency( entry.getKey(), entry.getValue() ) );
            System.out.println( "key " + entry.getKey() + " value: " + entry.getValue() );
        }
    }

}
