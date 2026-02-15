# Project Requirements
Product: Central Limit common.Order Book (CLOB) 
* Price-Time priority
* Partial fills
* Trade Reporting
* Final Book Snapshot
* Optional Iceberg trade orders

# System Architect
* Design
  * common.Order Gateway Service receives from network/common.Order source (multiple threads)
  * ↓
  * Thread safe unbound linked list queue. Store orders in serialize.
  * ↓
  * Single Matching Engine thread
  * ↓
  * Update common.Order book

* InputParser:
  * File reader service for parsing text file to get the trades.
  * extensive feature to Detects iceberg 5th data column.
  * extendable feature to accept different trade data source.
  
* com.BixMex.orderBook.OrderBookImpl (Data Structure Design)
  * 2 order books : Bids(Buy) and Ask (Sell) books
  * Each map uses structure TreeMap<Integer, Dequeue<OrderEvent>>;
  * The com.BixMex.orderBook.OrderBookImpl should be multi-thread supported /atomic reference.
  * TreeMap gives a sortedmap. Dequeue has FIFO queue implementation with time priority.
  * It includes methods in interface:
  
  | Interface Method | Comments                                                           |
  |------------------|--------------------------------------------------------------------|
  | match()          | match the order, remove successful trade position from order book. |
  | add()            | add position into the order book. (unmatched position)             |
  | remove()         | remove position from the order book. (matched position)            |

* Matching Rules
  * For any incoming order:
  * Matches best price first.
  * Matches the oldest order in the best price first.
  * Allows partial filled.

| common.Order In   | Match against   | Price condition              |
|------------|-----------------|------------------------------|
| Buy (bid)  | SELL (ask) book | sell book price <= buy price |
| Sell (ask) | BUY (bid) book  | buy book price >= sell price |

* Book snapshot
  * book print format:
    Buyers               Sellers
    000,000,000 000000 | 000000 000,000,000

* Iceberg
  * An iceberg order will increase the originator’s execution capabilities by maximising volume executed in a single order book execution at the same price (tranching in an automated input facility will only result in the ‘peak’ size being executed).
  * Customers looking to execute aggressively on the order book, eg using an At Best order, are more likely to achieve better prices when iceberg orders are available for execution as these will exhaust total iceberg volume before executing against orders further down the price queue).