package top.didasoft.ibmmq.cli.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TradeEntryDO implements Cloneable{
    private Long id;

    private String companyId;

    private String tradeEntryId;

    private Integer version;

    private LocalDate businessFromDate;

    private LocalDate businessThruDate;

    private LocalDate processInDate;

    private LocalDate processOutDate;

    private String source;

    private String eodId;

    private String externalRefId;

    private String status;

    private String currentFlag;

    private String initiatorType;

    private String initiatorId;

    private String lastUpdateType;

    private String lastUpdateBy;

    private LocalDate tradeDate;

    private LocalDateTime postSystemTs;

    private String dealType;

    private String direction;

    private String openOrClose;

    private String orderType;

    private String orderNumber;

    private LocalDateTime executionTime;

    private String executionBroker;

    private Integer exchangeId;

    private String blotterCode;

    private String primaryAccountNo;

    private String primaryAccountId;

    private String primaryPartyId;

    private String primaryAccountSub;

    private String contraPartyId;

    private String contraAccountId;

    private String contraAccountNo;

    private String contraAccountSub;

    private String servicePartyId;

    private String serviceAccountId;

    private String serviceAccountNo;

    private String serviceAccountSub;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal price;

    private BigDecimal amount;

    private String priceCurrency;

    private String settlementCurrency;

    private String productSymbolType;

    private String productAssetType;

    private String productSecurityType;

    private String productId;

    private String productDescription;

    private String productSymbol;

    private String dateInfo;

    private String additionalInfo;

    private String remark;

    private String executionId;

    private String orderChannel;

    private String action;

    private String tradeType;

    private String tradeCode;

    private String orderId;

    private String member;

    private String traderId;

    private String transactionList;

    private LocalDate settlementDate;

    private LocalDateTime firstCreateTime;

    private Long preEntryId;

    private String verify;

    private Integer fourEyeCheckStatus;

    private LocalDateTime updateTime;

    private String balanceType;

    private String businessType;

    private String exchangeOrderId;

    private String giveInOutIndicator;

    private String giveOutRelate;

    private String tally;

    @Override
    public TradeEntryDO clone() {
        TradeEntryDO o = null;
        try {
            o = (TradeEntryDO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
