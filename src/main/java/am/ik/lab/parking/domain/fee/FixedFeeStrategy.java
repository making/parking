package am.ik.lab.parking.domain.fee;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 基本料金固定型
 */
public class FixedFeeStrategy implements FeeStrategy {

    /**
     * 固定基本料金
     */
    private final BaseFee fixedFee;

    public FixedFeeStrategy(BaseFee fixedFee) {
        this.fixedFee = fixedFee;
    }

    @Override
    public MonetaryAmount calcFee(LocalDateTime in, LocalDateTime out) {
        return this.fixedFee.fee(Duration.between(in, out));
    }
}
