package de.frozenbytes.kickermost.concurrent.exchange;

import com.google.common.collect.ImmutableList;
import de.frozenbytes.kickermost.dto.Ticker;

import java.util.ArrayList;
import java.util.List;

public final class ExchangeStorage {

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
        this.tickerList = new ArrayList<>();
    }

    public ImmutableList<Ticker> getTickerList(){
        return ImmutableList.copyOf(tickerList);
    }

}
