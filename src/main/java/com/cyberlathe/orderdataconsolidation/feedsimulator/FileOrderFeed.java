package com.cyberlathe.orderdataconsolidation.feedsimulator;

import com.cyberlathe.orderdataconsolidation.exchangefeed.Order;
import com.cyberlathe.orderdataconsolidation.exchangefeed.OrderProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseChar;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileOrderFeed implements OrderFeed {
    public static final Logger logger = LoggerFactory.getLogger(FileOrderFeed.class);

    private OrderProcessor orderProcessor;
    private String filePath = null;

    public FileOrderFeed(String filePath, OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
        this.filePath = filePath;
    }

    @Override
    public void startFeed(){
        CellProcessor[] processors = new CellProcessor[] {
            new NotNull(new ParseChar()), // orderType
            new NotNull(), // orderId
            new Optional(new ParseInt()), // quantity
            new Optional(new ParseChar()), // side
            new Optional(), // symbol
            new Optional(new ParseDouble()) // price
        };

        try (FileReader fileReader = new FileReader(filePath);
             ICsvBeanReader beanReader = new CsvBeanReader(fileReader,CsvPreference.STANDARD_PREFERENCE)) {

            String[] header = beanReader.getHeader(true);
            Order order = null;
            while ((order = beanReader.read(Order.class, header, processors)) != null) {
                orderProcessor.process(order);
            }
        } catch (FileNotFoundException ex) {
            logger.error("Could not file order feed file {}", filePath ,ex);
        } catch (IOException ex) {
            logger.error("Error reading order feed", ex);
        }
    }

}
