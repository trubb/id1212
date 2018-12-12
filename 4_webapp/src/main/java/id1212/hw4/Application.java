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
    private HashMap<String, Double> currencies = new HashMap<>();

    @Autowired
    public Application (CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Put the currencies into the db
     * @param strings   0 or more strings as superclass does this
     * @throws Exception
     */
    @Override
    public void run (String... strings) throws Exception {
        fillHashMap();
        currenciesToDB();
        // print to show what hits the db, just a check TODO - REMOVE THIS
        Iterable<Currency> currencies = currencyRepository.findAll();
        for (Currency currency : currencies) {
            System.out.println( currency.getCode() + " " + currency.getRate() );
        }
        System.out.println("\n\nI AM DONE NOW");
    }

    /**
     * Put currencies into the hashmap
     * We're using EUR as base, all other values
     * reflect how many CURRENCY you get per EUR
     * All data retrieved via fixer.io API
     */
    private void fillHashMap() {
        this.currencies.put("AED", 4.172396);
        this.currencies.put("AFN", 86.103724);
        this.currencies.put("ALL", 123.641885);
        this.currencies.put("AMD", 551.04086);
        this.currencies.put("ANG", 2.01631);
        this.currencies.put("AOA", 350.470457);
        this.currencies.put("ARS", 42.803628);
        this.currencies.put("AUD", 1.572845);
        this.currencies.put("AWG", 2.045164);
        this.currencies.put("AZN", 1.9367);
        this.currencies.put("BAM", 1.960029);
        this.currencies.put("BBD", 2.273818);
        this.currencies.put("BDT", 95.296356);
        this.currencies.put("BGN", 1.955599);
        this.currencies.put("BHD", 0.428287);
        this.currencies.put("BIF", 2063.906109);
        this.currencies.put("BMD", 1.135887);
        this.currencies.put("BND", 1.789794);
        this.currencies.put("BOB", 7.849716);
        this.currencies.put("BRL", 4.362602);
        this.currencies.put("BSD", 1.136);
        this.currencies.put("BTC", 0.000332);
        this.currencies.put("BTN", 81.607384);
        this.currencies.put("BWP", 12.201688);
        this.currencies.put("BYN", 2.413414);
        this.currencies.put("BYR", 22263.379054);
        this.currencies.put("BZD", 2.289718);
        this.currencies.put("CAD", 1.515835);
        this.currencies.put("CDF", 1835.592711);
        this.currencies.put("CHF", 1.128146);
        this.currencies.put("CLF", 0.028451);
        this.currencies.put("CLP", 770.92614);
        this.currencies.put("CNY", 7.81774);
        this.currencies.put("COP", 3605.985875);
        this.currencies.put("CRC", 678.809171);
        this.currencies.put("CUC", 1.135887);
        this.currencies.put("CUP", 30.100997);
        this.currencies.put("CVE", 111.042581);
        this.currencies.put("CZK", 25.852709);
        this.currencies.put("DJF", 201.870381);
        this.currencies.put("DKK", 7.464093);
        this.currencies.put("DOP", 57.174872);
        this.currencies.put("DZD", 134.812744);
        this.currencies.put("EGP", 20.341434);
        this.currencies.put("ERN", 17.037994);
        this.currencies.put("ETB", 31.99818);
        this.currencies.put("EUR", 1.00000);
        this.currencies.put("FJD", 2.400981);
        this.currencies.put("FKP", 0.897333);
        this.currencies.put("GBP", 0.900305);
        this.currencies.put("GEL", 3.015775);
        this.currencies.put("GGP", 0.900356);
        this.currencies.put("GHS", 5.650923);
        this.currencies.put("GIP", 0.897333);
        this.currencies.put("GMD", 56.322959);
        this.currencies.put("GNF", 10438.799072);
        this.currencies.put("GTQ", 8.786764);
        this.currencies.put("GYD", 237.496614);
        this.currencies.put("HKD", 8.878942);
        this.currencies.put("HNL", 27.738119);
        this.currencies.put("HRK", 7.388715);
        this.currencies.put("HTG", 86.613062);
        this.currencies.put("HUF", 323.258577);
        this.currencies.put("IDR", 16510.567342);
        this.currencies.put("ILS", 4.259745);
        this.currencies.put("IMP", 0.900356);
        this.currencies.put("INR", 81.604361);
        this.currencies.put("IQD", 1352.841044);
        this.currencies.put("IRR", 47826.508961);
        this.currencies.put("ISK", 140.202409);
        this.currencies.put("JEP", 0.900356);
        this.currencies.put("JMD", 145.484501);
        this.currencies.put("JOD", 0.806363);
        this.currencies.put("JPY", 128.723229);
        this.currencies.put("KES", 116.652964);
        this.currencies.put("KGS", 79.318918);
        this.currencies.put("KHR", 4577.623411);
        this.currencies.put("KMF", 493.798365);
        this.currencies.put("KPW", 1022.328689);
        this.currencies.put("KRW", 1279.212793);
        this.currencies.put("KWD", 0.345653);
        this.currencies.put("KYD", 0.946688);
        this.currencies.put("KZT", 420.516484);
        this.currencies.put("LAK", 9706.151405);
        this.currencies.put("LBP", 1709.452316);
        this.currencies.put("LKR", 203.823722);
        this.currencies.put("LRD", 179.55527);
        this.currencies.put("LSL", 16.277446);
        this.currencies.put("LTL", 3.353978);
        this.currencies.put("LVL", 0.687086);
        this.currencies.put("LYD", 1.579151);
        this.currencies.put("MAD", 10.793879);
        this.currencies.put("MDL", 19.613921);
        this.currencies.put("MGA", 3993.777873);
        this.currencies.put("MKD", 61.491222);
        this.currencies.put("MMK", 1805.094177);
        this.currencies.put("MNT", 2998.359932);
        this.currencies.put("MOP", 9.14633);
        this.currencies.put("MRO", 405.511271);
        this.currencies.put("MUR", 38.901278);
        this.currencies.put("MVR", 17.560977);
        this.currencies.put("MWK", 844.327765);
        this.currencies.put("MXN", 22.782605);
        this.currencies.put("MYR", 4.758686);
        this.currencies.put("MZN", 69.930872);
        this.currencies.put("NAD", 16.277107);
        this.currencies.put("NGN", 414.036333);
        this.currencies.put("NIO", 36.791467);
        this.currencies.put("NOK", 9.739581);
        this.currencies.put("NPR", 130.853378);
        this.currencies.put("NZD", 1.65748);
        this.currencies.put("OMR", 0.437339);
        this.currencies.put("PAB", 1.135943);
        this.currencies.put("PEN", 3.813796);
        this.currencies.put("PGK", 3.827371);
        this.currencies.put("PHP", 59.761835);
        this.currencies.put("PKR", 158.030271);
        this.currencies.put("PLN", 4.300705);
        this.currencies.put("PYG", 6748.981714);
        this.currencies.put("QAR", 4.135878);
        this.currencies.put("RON", 4.658533);
        this.currencies.put("RSD", 118.382154);
        this.currencies.put("RUB", 75.31046);
        this.currencies.put("RWF", 988.221417);
        this.currencies.put("SAR", 4.260825);
        this.currencies.put("SBD", 9.167685);
        this.currencies.put("SCR", 15.494602);
        this.currencies.put("SDG", 54.094359);
        this.currencies.put("SEK", 10.346326);
        this.currencies.put("SGD", 1.558779);
        this.currencies.put("SHP", 1.50039);
        this.currencies.put("SLL", 9768.625491);
        this.currencies.put("SOS", 658.814401);
        this.currencies.put("SRD", 8.471445);
        this.currencies.put("STD", 23911.095838);
        this.currencies.put("SVC", 9.939974);
        this.currencies.put("SYP", 584.981703);
        this.currencies.put("SZL", 16.288673);
        this.currencies.put("THB", 37.245874);
        this.currencies.put("TJS", 10.700563);
        this.currencies.put("TMT", 3.986962);
        this.currencies.put("TND", 3.332579);
        this.currencies.put("TOP", 2.559777);
        this.currencies.put("TRY", 6.086422);
        this.currencies.put("TTD", 7.656614);
        this.currencies.put("TWD", 34.978521);
        this.currencies.put("TZS", 2612.308167);
        this.currencies.put("UAH", 31.639555);
        this.currencies.put("UGX", 4217.654218);
        this.currencies.put("USD", 1.135887);
        this.currencies.put("UYU", 36.587496);
        this.currencies.put("UZS", 9441.49048);
        this.currencies.put("VEF", 282344.797762);
        this.currencies.put("VND", 26469.681042);
        this.currencies.put("VUV", 128.818916);
        this.currencies.put("WST", 2.937142);
        this.currencies.put("XAF", 657.405616);
        this.currencies.put("XAG", 0.077054);
        this.currencies.put("XAU", 0.000912);
        this.currencies.put("XCD", 3.069791);
        this.currencies.put("XDR", 0.820981);
        this.currencies.put("XOF", 662.221654);
        this.currencies.put("XPF", 120.176504);
        this.currencies.put("YER", 284.340809);
        this.currencies.put("ZAR", 16.074785);
        this.currencies.put("ZMK", 10224.367066);
        this.currencies.put("ZMW", 13.592055);
        this.currencies.put("ZWL", 366.158765);
    }

    /**
     * Insert currencies into the database
     */
    private void currenciesToDB() {
        // empty the DB so we dont have to do it manually each time we run the program
        currencyRepository.deleteAll();

        // put each entry in the hashmap into the DB
        for (Map.Entry<String, Double> entry : currencies.entrySet()) {
            currencyRepository.save( new Currency( entry.getKey(), entry.getValue() ) );
        }
    }

}
