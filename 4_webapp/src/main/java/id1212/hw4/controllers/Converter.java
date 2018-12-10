package id1212.hw4.controllers;

import id1212.hw4.integration.CurrencyRepository;
import id1212.hw4.integration.RateRepository;
import id1212.hw4.model.Conversion;
import id1212.hw4.model.Currency;
import id1212.hw4.model.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Controller
public class Converter {

    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;

    private Iterable<Currency> currencies;
    private Conversion conversion;

    @Autowired
    public Converter(CurrencyRepository currencyRepository, RateRepository rateRepository) {
        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
        this.currencies = currencyRepository.findAll();
        this.conversion = new Conversion("EUR", "USD", 0, 0);
        for (Currency currency : currencies) {
            System.out.println( currency.getCode() );
        }
    }

    @GetMapping(value = "/")
    public String converter (Model model) {
        model.addAttribute("currencies", currencies);
        if ( !model.containsAttribute("conversion") ) {
            model.addAttribute("conversion", conversion);
        }
        return "converter";
    }

    @PostMapping(value = "/convert")
    public String convert(@ModelAttribute Conversion conversion) {
        double result;
        if ( conversion.getBaseCurrency().equals( conversion.getQuoteCurrency() ) ) {
            result = conversion.getAmount();
        } else {
            result = convertCurrencies( conversion );
        }
        this.conversion = new Conversion( conversion.getBaseCurrency(), conversion.getQuoteCurrency(), conversion.getAmount(), result );

        return "redirect:/";
    }

    private double convertCurrencies (Conversion conversion) {

        Currency base = currencyRepository.findByCode( conversion.getBaseCurrency() );
        Currency quote = currencyRepository.findByCode( conversion.getQuoteCurrency() );
        Rate rate = rateRepository.findByBaseCurrencyAndAndQuoteCurrency( base, quote );

        if ( base.getCode().equals("EUR") ) {
            return (double) Math.round( conversion.getAmount() * rate.getRate() );
        } else {
            // reverse the rate
            double reverserate = reverseRate( rate.getRate() );
            return (double) Math.round( conversion.getAmount() * reverserate );
        }
    }

    /**
     * TODO - MOVE THIS TO ANOTHER WEB IMPLEMENTATION
     * Calculates the inverse exchange rate (quote -> base) based on the rate base -> quote
     * In this case: OTHER -> EUR based on EUR -> OTHER
     * @param baseToQuote   the exchange rate when going from EUR to other currency
     * @return              the reverse rate
     */
    private double reverseRate (double baseToQuote) {
        return (double) 1 / baseToQuote;    // return the calculaton as a double
    }

}
