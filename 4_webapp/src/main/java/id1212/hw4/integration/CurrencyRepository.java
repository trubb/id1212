package id1212.hw4.integration;

import id1212.hw4.model.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CurrencyRepository extends CrudRepository<Currency, Long> {
    Currency findByCode(@Param("code") String code);
}
