package com.xeiam.xchange.bitstamp.service.polling;

import static com.xeiam.xchange.dto.Order.OrderType.BID;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.bitstamp.BitstampAdapters;
import com.xeiam.xchange.bitstamp.dto.BitstampException;
import com.xeiam.xchange.bitstamp.dto.trade.BitstampOrder;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.dto.trade.UserTrades;
import com.xeiam.xchange.exceptions.ExchangeException;
import com.xeiam.xchange.exceptions.NotAvailableFromExchangeException;
import com.xeiam.xchange.exceptions.NotYetImplementedForExchangeException;
import com.xeiam.xchange.service.polling.trade.PollingTradeService;
import com.xeiam.xchange.service.polling.trade.params.DefaultTradeHistoryParamPaging;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParamPaging;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParams;
import com.xeiam.xchange.utils.Assert;

/**
 * @author Matija Mazi
 */
// TODO Convert BitstampExceptions to ExchangeException at the Raw level. Do not leak them out of this class.
public class BitstampTradeService extends BitstampTradeServiceRaw implements PollingTradeService {

    /**
     * Constructor
     *
     * @param exchange
     */
    public BitstampTradeService(Exchange exchange) {

        super(exchange);
    }

    @Override
    public OpenOrders getOpenOrders() throws IOException, BitstampException {

        BitstampOrder[] openOrders = getBitstampOpenOrders();

        List<LimitOrder> limitOrders = new ArrayList<LimitOrder>();
        for (BitstampOrder bitstampOrder : openOrders) {
            OrderType orderType = bitstampOrder.getType() == 0 ? OrderType.BID : OrderType.ASK;
            String id = Integer.toString(bitstampOrder.getId());
            BigDecimal price = bitstampOrder.getPrice();
            limitOrders.add(new LimitOrder(orderType, bitstampOrder.getAmount(), CurrencyPair.BTC_USD, id, bitstampOrder.getTime(), price));
        }
        return new OpenOrders(limitOrders);
    }

    @Override
    public String placeMarketOrder(MarketOrder marketOrder) throws IOException, BitstampException {

        throw new NotAvailableFromExchangeException();
    }

    @Override
    public String placeLimitOrder(LimitOrder limitOrder) throws IOException, BitstampException {

        Assert.isTrue(limitOrder.getCurrencyPair().equals(CurrencyPair.BTC_USD), "Currency Pair must be USD/BTC!!!");

        BitstampOrder bitstampOrder;
        if (limitOrder.getType() == BID) {
            bitstampOrder = buyBitStampOrder(limitOrder.getTradableAmount(), limitOrder.getLimitPrice());
        } else {
            bitstampOrder = sellBitstampOrder(limitOrder.getTradableAmount(), limitOrder.getLimitPrice());
        }
        if (bitstampOrder.getErrorMessage() != null) {
            throw new ExchangeException(bitstampOrder.getErrorMessage());
        }

        return Integer.toString(bitstampOrder.getId());
    }

    @Override
    public boolean cancelOrder(String orderId) throws IOException, BitstampException {

        return cancelBitstampOrder(Integer.parseInt(orderId));
    }

    /**
     * Required parameter types: {@link TradeHistoryParamPaging#getPageLength()}
     */
    @Override
    public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {

        return BitstampAdapters.adaptTradeHistory(getBitstampUserTransactions(Long.valueOf(((TradeHistoryParamPaging) params).getPageLength())));
    }

    @Override
    public TradeHistoryParams createTradeHistoryParams() {

        return new DefaultTradeHistoryParamPaging(1000);
    }

    @Override
    public Collection<Order> getOrder(String... orderIds) throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException,
            IOException {
        throw new NotYetImplementedForExchangeException();
    }

}
