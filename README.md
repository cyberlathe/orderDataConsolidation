Order Book Consolidation - Example
==================================

Sample order book consolidation program that computes basic order book to a fixed depth from sample market data and performs consolidation across multiple exchanges

### Components implemented
* Exchange Feed Simulator : Program which reads data from a flat file and computes a basic order book
* Pubsub implementation : Sample pubsub implementation
* Socket connection : Sample Network socket connection implementation
* Order data consolidation : Program that performs consolidation across multiple exchanges

<img width="1032" alt="flow-image" src="https://github.com/sunil9631/orderDataConsolidation/blob/master/images/flow.png" style="max-width:100%;">

### Run Program
Run file TestMarketDataClient.java, which generates consolidated order book, formatted as the following comma separated output:
depth, bid price, bid volume, ask price, ask volume



