package de.frozenbytes.kickermost.concurrent.exchange;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.frozenbytes.kickermost.dto.Ticker;
import de.frozenbytes.kickermost.dto.property.TickerUrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExchangeStorage {

    private static final Object LOCK = new Object();

    public static ExchangeStorage getInstance(){
        if(INSTANCE == null){
            synchronized (ExchangeStorage.class){
                if(INSTANCE == null){
                    INSTANCE = new ExchangeStorage();
                }
            }
        }
        return INSTANCE;
    }

    private static ExchangeStorage INSTANCE;

    final List<Ticker> tickerList;


    private ExchangeStorage(){
        this.tickerList = Collections.synchronizedList(new ArrayList<>());
    }

    public ImmutableList<Ticker> getTickerList(){
        synchronized (LOCK){
            return ImmutableList.copyOf(tickerList);
        }
    }

    public Ticker getTickerByUrl(final TickerUrl tickerUrl){
        final Ticker ticker = getTickerByUrlOrNull(tickerUrl);
        Preconditions.checkNotNull(ticker, String.format("No ticker with the url '%s' could be found in the storage list, but it should have been there!", tickerUrl));
        return ticker;
    }

    public boolean containsTickerWithUrl(final TickerUrl tickerUrl){
        return getTickerByUrlOrNull(tickerUrl) != null;
    }

    public void addTicker(final Ticker ticker){
        synchronized (LOCK){
            Preconditions.checkState(!tickerList.contains(ticker), String.format("A ticker with the url '%s' already have been added to the storage list!", ticker.getTickerUrl()));
            tickerList.add(ticker);
        }
    }

    private Ticker getTickerByUrlOrNull(final TickerUrl tickerUrl){
        synchronized (LOCK){
            for(Ticker ticker : tickerList){
                if(ticker.getTickerUrl().equals(tickerUrl)){
                    return ticker;
                }
            }
            return null;
        }
    }

}
