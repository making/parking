package am.ik.lab.parking.domain.fee;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

/**
 * 時間貸駐車場の料金計算ルール
 *
 * @see <a href="https://times-info.net/info/max_fee/">時間貸駐車場の最大料金について</a>
 */
public interface FeeStrategy {

    /**
     * 駐車料金を計算する
     *
     * @param in  入庫時刻
     * @param out 出庫時刻
     * @return 駐車料金
     */
    MonetaryAmount calcFee(LocalDateTime in, LocalDateTime out);


}
