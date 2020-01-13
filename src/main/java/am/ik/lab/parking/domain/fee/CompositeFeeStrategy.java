package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 日付によって変わる料金計算ルール<br>
 * ルールの詳細は個別クラスに委譲する。
 */
public class CompositeFeeStrategy implements FeeStrategy {

    private final SortedMap<LocalDateTime, FeeStrategy> feeStrategies;

    public CompositeFeeStrategy(SortedMap<LocalDateTime, FeeStrategy> feeStrategies) {
        final LocalDateTime lastKey = feeStrategies.lastKey();
        if (!LocalDateTime.MAX.isEqual(lastKey)) {
            SortedMap<LocalDateTime, FeeStrategy> copy = new TreeMap<>(feeStrategies);
            final FeeStrategy lastStrategy = copy.remove(lastKey);
            copy.put(LocalDateTime.MAX, lastStrategy);
            this.feeStrategies = Collections.unmodifiableSortedMap(copy);
        } else {
            this.feeStrategies = Collections.unmodifiableSortedMap(feeStrategies);
        }
    }

    @Override
    public MonetaryAmount calcFee(LocalDateTime in, LocalDateTime out) {
        MonetaryAmount totalFee = Jpy.ZERO;
        LocalDateTime beginOfPeriod = in;
        for (Map.Entry<LocalDateTime, FeeStrategy> entry : this.feeStrategies.entrySet()) {
            if (in.isEqual(entry.getKey()) || in.isAfter(entry.getKey())) {
                // 入庫時刻が対象の料金計算ルールの期限以降の場合は次の料金計算ルールを使用する
                continue;
            }
            final FeeStrategy feeStrategy = entry.getValue();
            final LocalDateTime endOfPeriod = entry.getKey().isBefore(out) ? entry.getKey() : out;
            final MonetaryAmount feeOfPeriod = feeStrategy.calcFee(beginOfPeriod, endOfPeriod);
            totalFee = totalFee.add(feeOfPeriod);
            // TODO 入庫から単位時間経過前に料金計算ルールが変わった場合の調整
            beginOfPeriod = endOfPeriod;
            if (out.isEqual(entry.getKey()) || out.isBefore(entry.getKey())) {
                break;
            }
        }
        return totalFee;
    }
}
