package am.ik.lab.parking.currency;

import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public final class Jpy {

    public static final CurrencyUnit CURRENCY_UNIT = Monetary.getCurrency("JPY");

    public static final MonetaryAmount ZERO = Jpy.of(0);

    public static final MonetaryAmount INFINITE = Jpy.of(Long.MAX_VALUE);

    public static MonetaryAmount of(Number amount) {
        return Money.of(amount, CURRENCY_UNIT);
    }

    public static MonetaryAmount of(BigDecimal amount) {
        return Money.of(amount, CURRENCY_UNIT);
    }
}
