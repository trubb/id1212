package id1212.hw4;

import id1212.hw4.integration.CurrencyRepository;
import id1212.hw4.integration.RateRepository;
import id1212.hw4.model.Currency;
import id1212.hw4.model.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;

    @Autowired
    public Application (CurrencyRepository currencyRepository, RateRepository rateRepository) {
        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Run the things!
     * @param strings   0 or more strings as superclass does it
     * @throws Exception
     */
    @Override
    public void run (String... strings) throws Exception {
        currenciesToDB();
        ratesToDB();
        System.out.println("\n\n\nI AM DONE NOW");
    }

    /**
     * Insert currencies into the database
     */
    private void currenciesToDB() {
        String[] codes = { "EUR", "USD", "GBP", "SEK" };

        for (String code : codes) {
            currencyRepository.save( new Currency( code ) );
        }
    }



    private void ratesToDB() {
        // hashmap for rates
        HashMap<String, Double> rates = new HashMap<>();
        rates.put("EUR", 1.0000);   // -> 1
        rates.put("USD", 1.137249); // -> 0.87931490816
        rates.put("GBP", 0.891648); // -> 1.12151880563
        rates.put("SEK", 10.252522);    // -> 0.09753697675

        Currency currency = currencyRepository.findByCode("EUR");
        for ( Map.Entry<String, Double> entry : rates.entrySet() ) {

            rateRepository.save(
                new Rate(
                    entry.getValue(),
                    currency,
                    currencyRepository.findByCode( entry.getKey() ) ) );

        }
    }

}
