package id1212.hw4.controllers;

import id1212.hw4.integration.CurrencyRepository;
import id1212.hw4.model.Conversion;
import id1212.hw4.model.Currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Converter {

    private final CurrencyRepository currencyRepository;

    private Iterable<Currency> currencies;
    private Conversion conversion;

    /**
     * Initialize the Converter
     * @param currencyRepository    the database into which we have put all currencies and their rates
     */
    @Autowired
    public Converter(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
        this.currencies = currencyRepository.findAll();
        this.conversion = new Conversion("EUR", "USD", 0, 0);
    }

    /**
     * When a user connects to "url/" we set a couple of attributes and return our html page
     * @param model The model to which we want to add attributes
     * @return      The html file that we want to present to the client
     */
    @GetMapping(value = "/")
    public String converter (Model model) {
        model.addAttribute("currencies", currencies);   // add currencies as attribute in model
        if ( !model.containsAttribute("conversion") ) {
            model.addAttribute("conversion", conversion);   // add a conversion as attribute
        }
        return "converter"; // serve converter.html
    }

    /**
     * The method that is called when the user presses the "Convert"-button
     * @param conversion    The conversion object that we will act upon
     * @return              Redirect to the "landing page" but this time with a finished conversion
     */
    @PostMapping(value = "/convert")
    public String convert(@ModelAttribute Conversion conversion) {
        double result;
        if ( conversion.getBaseCurrency().equals( conversion.getQuoteCurrency() ) ) {
            result = conversion.getAmount();
        } else {
            result = convertCurrencies( conversion );
        }
        this.conversion = new Conversion(
                conversion.getBaseCurrency(),
                conversion.getQuoteCurrency(),
                conversion.getAmount(),
                result
        );
        return "redirect:/";
    }

    /**
     * Reset the current conversion
     * @return  A blank conversion
     */
    @GetMapping(value = "/reset")
    public String reset() {
        this.conversion = new Conversion("EUR", "USD", 0, 0);
        return "redirect:/";
    }

    /**
     * Calculates the result of converting currency A to currency B
     * @param conversion    a conversion object with currencies A & B
     * @return              the result of the conversion
     */
    private double convertCurrencies (Conversion conversion) {
        Currency base = currencyRepository.findByCode( conversion.getBaseCurrency() );
        Currency quote = currencyRepository.findByCode( conversion.getQuoteCurrency() );

        return conversion.getAmount() / base.getRate() * quote.getRate();
    }

}
