package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.function.Function;

/**
 * 昼時間、夜時間を持つ料金体型の基底クラス。
 */
public abstract class TimePeriodFeeStrategy implements FeeStrategy {

    /**
     * 昼料金開始時刻
     */
    protected final LocalTime beginTimeOfDaytime;

    /**
     * 昼料金終了時刻
     */
    protected final LocalTime endTimeOfDaytime;

    /**
     * 昼基本料金
     */
    protected final BaseFee daytimeFee;

    /**
     * 夜基本料金
     */
    protected final BaseFee nightFee;

    protected TimePeriodFeeStrategy(LocalTime beginTimeOfDaytime, LocalTime endTimeOfDaytime, BaseFee daytimeFee, BaseFee nightFee) {
        this.daytimeFee = daytimeFee;
        this.nightFee = nightFee;
        this.beginTimeOfDaytime = beginTimeOfDaytime;
        this.endTimeOfDaytime = endTimeOfDaytime;
    }

    /**
     * 通常料金を計算
     *
     * @param in  入庫時刻
     * @param out 出庫時刻
     * @return 通常料金
     */
    protected final MonetaryAmount calcRegularFee(LocalDateTime in, LocalDateTime out) {
        // 最初の昼時間
        LocalDateTime beginOfDaytime = in.with(this.beginTimeOfDaytime);
        LocalDateTime endOfDaytime = in.with(this.endTimeOfDaytime);
        if (endOfDaytime.isBefore(beginOfDaytime)) {
            endOfDaytime = endOfDaytime.plusDays(1);
        }

        // 基本料金のイテレーター
        Iterator<BaseFee> baseFeeIterator;
        // 料金切り替え時刻のイテレーター
        Iterator<LocalDateTime> endOfPeriodIterator;

        if (in.isEqual(endOfDaytime) || in.isAfter(endOfDaytime) || in.isBefore(beginOfDaytime)) {
            // 入庫が夜時間の場合
            baseFeeIterator = new SwappingIterator<>(this.nightFee, this.daytimeFee);
            endOfPeriodIterator = new SwappingIterator<>(
                in.isAfter(beginOfDaytime) ? beginOfDaytime.plusDays(1) : beginOfDaytime,
                endOfDaytime.plusDays(1),
                x -> x.plusDays(1));
        } else {
            // 入庫が昼時間の場合
            baseFeeIterator = new SwappingIterator<>(this.daytimeFee, this.nightFee);
            endOfPeriodIterator = new SwappingIterator<>(endOfDaytime, beginOfDaytime.plusDays(1),
                x -> x.plusDays(1));
        }

        MonetaryAmount totalFee = Jpy.ZERO;
        MonetaryAmount feeOfCurrentPeriod = Jpy.ZERO;
        LocalDateTime current = in;
        BaseFee currentBaseFee = baseFeeIterator.next();
        LocalDateTime endOfPeriod = endOfPeriodIterator.next();
        for (int i = 0; i < 1000; i++ /* 無限ループ予防 */) {
            feeOfCurrentPeriod = feeOfCurrentPeriod.add(currentBaseFee.getAmount());
            current = current.plus(currentBaseFee.getUnitTime());
            if (current.isEqual(out) || current.isAfter(out)) {
                // 同一時間帯の合計料金を調整
                totalFee = totalFee.add(adjustFeeOfCurrentPeriod(feeOfCurrentPeriod, currentBaseFee));
                break;
            }
            if (current.isEqual(endOfPeriod) || current.isAfter(endOfPeriod)) {
                endOfPeriod = endOfPeriodIterator.next();
                // 同一時間帯の合計料金を調整
                totalFee = totalFee.add(adjustFeeOfCurrentPeriod(feeOfCurrentPeriod, currentBaseFee));
                feeOfCurrentPeriod = Jpy.ZERO;
                currentBaseFee = baseFeeIterator.next();
            }
        }
        return totalFee;
    }

    /**
     * 同一時間帯の合計料金を調整する
     *
     * @param amount  同一時間帯の合計料金
     * @param baseFee 対象の時間帯の基本料金
     * @return 調整後の料金
     */
    protected MonetaryAmount adjustFeeOfCurrentPeriod(MonetaryAmount amount, BaseFee baseFee) {
        return amount;
    }

    public interface Supplier {

        FeeStrategy supply(LocalTime daytimeBegin, LocalTime daytimeEnd, BaseFee daytimeFee, BaseFee nightFee);
    }

    /**
     * 入れ替えIterator
     */
    private static class SwappingIterator<T> implements Iterator<T> {

        private T a;

        private T b;

        private final Function<T, T> processOnSwap;

        SwappingIterator(T a, T b, Function<T, T> processOnSwap) {
            this.a = a;
            this.b = b;
            this.processOnSwap = processOnSwap;
        }

        SwappingIterator(T a, T b) {
            this.a = a;
            this.b = b;
            this.processOnSwap = Function.identity();
        }

        @Override
        public T next() {
            T swap = a;
            this.a = this.b;
            this.b = this.processOnSwap.apply(swap);
            return swap;
        }

        @Override
        public boolean hasNext() {
            return true;
        }
    }
}
