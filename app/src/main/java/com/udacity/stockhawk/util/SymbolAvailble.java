package com.udacity.stockhawk.util;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by Mahertag on 3/13/2017.
 */

public class SymbolAvailble {

    public static boolean checkSymbol(Stock stock){

        try {
            StockQuote quote = stock.getQuote();

            quote.getPrice().floatValue();
            quote.getChange().floatValue();
            quote.getChangeInPercent().floatValue();

            stock.getHistory();

        }catch (Exception e){
            return false;
        }
        return true;
    }
}
