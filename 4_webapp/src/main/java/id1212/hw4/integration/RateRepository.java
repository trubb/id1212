package id1212.hw4.integration;

import id1212.hw4.model.Currency;
import id1212.hw4.model.Rate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RateRepository extends CrudRepository<Rate, Long> {
    Rate findByBaseCurrencyAndAndQuoteCurrency(@Param("baseCurrency") Currency baseCurrency, @Param("quoteCurrency") Currency quoteCurrency);
}
