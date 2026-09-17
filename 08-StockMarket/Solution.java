
import java.util.*;
import java.util.stream.Collectors;

import org.junit.Assert;



/* ===== Stock ===== */
class Stock {
	String symbol;
	String name;

	Stock(String symbol, String name) {
		this.symbol = symbol;
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Stock))
			return false;
		Stock s = (Stock) o;
		return symbol.equals(s.symbol) && name.equals(s.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(symbol, name);
	}
}

/* ===== PriceRecord ===== */
class PriceRecord {
	Stock stock;
	int price;
	String date; // YYYY-MM-DD

	PriceRecord(Stock stock, int price, String date) {
		this.stock = stock;
		this.price = price;
		this.date = date;
	}
	
//	String date() {
//		return date;
//	}
}

/* ===== StockCollection ===== */
class StockCollection {

	List<PriceRecord> priceRecords = new ArrayList<>();
	Stock stock;

	StockCollection(Stock stock) {
		this.stock = stock;
	}

	void addPriceRecord(PriceRecord record) {
		if (!record.stock.equals(stock)) {
			throw new IllegalArgumentException("Stock mismatch");
		}
		priceRecords.add(record);
	}

	int getNumPriceRecords() {
		return priceRecords.size();
	}

	int getMaxPrice() {
		return priceRecords.stream().mapToInt(r -> r.price).max().orElse(-1);
	}

	int getMinPrice() {
		return priceRecords.stream().mapToInt(r -> r.price).min().orElse(-1);
	}

	double getAvgPrice() {
	     /** Return the average price recorded in this StockCollection. */
        return priceRecords.stream().mapToInt(record -> record.price).average().orElse(-1.0);
	}
	
	Object[] getBiggestChange(){
		
	    if(priceRecords.size()<=0) {
	    	return null;
	    }
	    priceRecords.sort(Comparator.comparing(i->i.date));
		int Max=Integer.MIN_VALUE;
		String from ="",to="";
		for(int i=1;i<priceRecords.size();i++) {
			if(Math.abs((priceRecords.get(i).price)-(priceRecords.get(i-1).price))>Math.abs(Max)) {
				Max=((priceRecords.get(i).price)-(priceRecords.get(i-1).price));
				from=priceRecords.get(i-1).date;
				to=priceRecords.get(i).date;
				
			}
			
		}
		System.out.println(Max+" "+from+" "+to);
		
		return new Object[] {Max,from,to};
		
	}



}

/* ===== Transaction ===== */
class Transaction {
	Stock stock;
	String transactionType; // buy / sell
	String date;
	int quantity;

	Transaction(Stock stock, String transactionType, String date, int quantity) {
		this.stock = stock;
		this.transactionType = transactionType;
		this.date = date;
		this.quantity = quantity;
	}
}

/* ===== Tradebook ===== */
class Tradebook {

	List<Transaction> transactions = new ArrayList<>();

	void addTransaction(Transaction transaction) {
		transactions.add(transaction);
	}
	public int getTotal(List<StockCollection> stockCollections){
		int totals=0;
		for(StockCollection stockcollection:stockCollections) {
			stockcollection.priceRecords.sort(Comparator.comparing(i->i.date));
			PriceRecord LatestPriceRecord=stockcollection.priceRecords.getLast();
			int LatestPrice=stockcollection.priceRecords.getLast().price;
			System.out.println("LatestPrice:"+LatestPrice);
			List<Transaction>stockTransaction=transactions.stream().filter(i->i.stock.equals(LatestPriceRecord.stock)).toList();
			int totstock=0;
			for(Transaction stocktransaction:stockTransaction) {
				if(stocktransaction.transactionType.equals("sell")) {
					totstock-=stocktransaction.quantity;
				}
				else {
					totstock+=stocktransaction.quantity;
				}
				
			}
			System.out.println(totstock+"*"+LatestPrice);
			totals+=Math.abs(totstock)*LatestPrice;
	
			
		}
		System.out.println(Math.abs(totals));
		return totals;
	}
	

}

/* ===== Solution ===== */
public class Solution {
	public static void main(String[] args) {
		testPriceRecord();
		testStockCollection();
		testGetBiggestChange();
		testTradebook();
	}

	public static void testPriceRecord() { // Test basic PriceRecord functionality
		System.out.println("Running testPriceRecord");
		Stock testStock = new Stock("AAPL", "Apple Inc.");
		PriceRecord testPriceRecord = new PriceRecord(testStock, 100, "2023-07-01");
		assert testPriceRecord.stock.equals(testStock);
		assert testPriceRecord.price == 100;
		assert testPriceRecord.date.equals("2023-07-01");
	}

	private static StockCollection makeStockCollection(Stock stock, Object[][] priceData) {
		StockCollection stockCollection = new StockCollection(stock);
		for (Object[] priceRecordData : priceData) {
			PriceRecord priceRecord = new PriceRecord(stock, (int) priceRecordData[0], (String) priceRecordData[1]);
			stockCollection.addPriceRecord(priceRecord);
		}
		return stockCollection;
	}

	public static void testStockCollection() {
		System.out.println("Running testStockCollection "); // Test basic StockCollection functionality
		Stock testStock = new Stock("AAPL", "Apple Inc.");
		StockCollection stockCollection = new StockCollection(testStock);
		assert stockCollection.getNumPriceRecords() == 0;
		assert stockCollection.getMaxPrice() == -1;
		assert stockCollection.getMinPrice() == -1;
		assert Math.abs(stockCollection.getAvgPrice() + 1.0) < 0.001;

		Object[][] priceData = { { 110, "2023-06-29" }, { 112, "2023-07-01" }, { 90, "2023-06-28" },
				{ 105, "2023-07-06" } };
		testStock = new Stock("AAPL", "Apple Inc.");
		stockCollection = makeStockCollection(testStock, priceData);
		assert stockCollection.getNumPriceRecords() == priceData.length;
		assert stockCollection.getMaxPrice() == 112;
		assert stockCollection.getMinPrice() == 90;
		assert Math.abs(stockCollection.getAvgPrice() - 104.25) < 0.1;
	}


	public static void testGetBiggestChange() {
        // Test the getBiggestChange method
        System.out.println("Running testGetBiggestChange");
        Stock testStock = new Stock("AAPL", "Apple Inc.");
        StockCollection stockCollection = new StockCollection(testStock);

       Assert.assertNull(stockCollection.getBiggestChange());

        /*
         * Price Records: Price: 110 112 90 105 Date: 2023-06-29 2023-07-01 2023-06-25
         * 2023-07-06
         */
        Object[][] priceData = {{110, "2023-06-29"}, {112, "2023-07-01"}, {90, "2023-06-25"},
                {105, "2023-07-06"}};
        stockCollection = makeStockCollection(testStock, priceData);

        Assert.assertArrayEquals(new Object[]{20, "2023-06-25", "2023-06-29"}, stockCollection.getBiggestChange());

        /*
         * Price Records: Price: 200 210 190 180 Date: 2000-01-04 1999-12-30 2000-01-03
         * 2000-01-01
         */
        Object[][] priceData2 = {{200, "2000-01-04"}, {210, "1999-12-30"}, {190, "2000-01-03"},
                {180, "2000-01-01"}};
        stockCollection = makeStockCollection(testStock, priceData2);

        Assert.assertArrayEquals(new Object[]{-30, "1999-12-30", "2000-01-01"}, stockCollection.getBiggestChange());
    }
	
	  public static void testTradebook() {
	        // Test Tradebook functionality
	        System.out.println("Running testTradebook");
	        Tradebook tradebook = new Tradebook();
	        Stock testStock1 = new Stock("AAPL", "Apple Inc.");
	        Object[][] testPriceData1 = {{110, "2023-06-29"}, {112, "2023-07-01"}, {90, "2023-06-25"},
	                {105, "2023-07-06"}};
	        StockCollection testStockCollection1 = makeStockCollection(testStock1, testPriceData1);

	        ArrayList<StockCollection> testStockCollections = new ArrayList<>();
	        testStockCollections.add(testStockCollection1);

	        Transaction transaction1 = new Transaction(testStock1, "buy", "2023-06-25", 10);
	        tradebook.addTransaction(transaction1);
	        // Total price = 10 * 105 = 1050
	        Assert.assertEquals(1050, tradebook.getTotal(testStockCollections));

	        Transaction transaction2 = new Transaction(testStock1, "buy", "2023-06-29", 5);
	        Transaction transaction3 = new Transaction(testStock1, "sell", "2023-07-01", 3);
	        tradebook.addTransaction(transaction2);
	        tradebook.addTransaction(transaction3);
	        // Total stocks = 10 + 5 - 3 = 12
	        // Total price = 12 * 105 = 1260
	        Assert.assertEquals(1260, tradebook.getTotal(testStockCollections));

	        Stock testStock2 = new Stock("GOOG", "Alphabet Inc.");
	        Stock testStock3 = new Stock("MSFT", "Microsoft Corporation");
	        Stock testStock4 = new Stock("AMZN", "Amazon.com Inc.");
	        Object[][] testPriceData2 = {{1500, "2023-06-29"}, {1550, "2023-07-01"}, {1475, "2023-06-25"},
	                {1520, "2023-07-06"}};
	        Object[][] testPriceData3 = {{250, "2023-06-29"}, {255, "2023-07-01"}, {260, "2023-07-06"},
	                {245, "2023-06-25"}};
	        Object[][] testPriceData4 = {{3550, "2023-07-06"}, {3500, "2023-06-29"}, {3600, "2023-07-01"},
	                {3450, "2023-06-25"}};
	        StockCollection testStockCollection2 = makeStockCollection(testStock2, testPriceData2);
	        StockCollection testStockCollection3 = makeStockCollection(testStock3, testPriceData3);
	        StockCollection testStockCollection4 = makeStockCollection(testStock4, testPriceData4);
	        testStockCollections.add(testStockCollection2);
	        testStockCollections.add(testStockCollection3);
	        testStockCollections.add(testStockCollection4);

	        Transaction transaction4 = new Transaction(testStock2, "buy", "2023-06-25", 15);
	        Transaction transaction5 = new Transaction(testStock2, "sell", "2023-06-29", 10);
	        Transaction transaction6 = new Transaction(testStock2, "sell", "2023-07-01", 1);
	        tradebook.addTransaction(transaction4);
	        tradebook.addTransaction(transaction5);
	        tradebook.addTransaction(transaction6);

	        Transaction transaction7 = new Transaction(testStock3, "sell", "2023-07-01", 5);
	        Transaction transaction8 = new Transaction(testStock3, "buy", "2023-06-29", 20);
	        Transaction transaction9 = new Transaction(testStock3, "buy", "2023-06-25", 10);
	        tradebook.addTransaction(transaction7);
	        tradebook.addTransaction(transaction8);
	        tradebook.addTransaction(transaction9);

	        Transaction transaction10 = new Transaction(testStock4, "buy", "2023-07-01", 5);
	        Transaction transaction11 = new Transaction(testStock4, "buy", "2023-06-29", 5);
	        Transaction transaction12 = new Transaction(testStock4, "buy", "2023-06-25", 1);
	        tradebook.addTransaction(transaction10);
	        tradebook.addTransaction(transaction11);
	        tradebook.addTransaction(transaction12);
	        // Stocks: APPL GOOG MSFT AMZN
	        // Total stocks: 12 4 25 11
	        // Latest price: 105 1520 260 3550
	        // Total price: 1260 + 6080 + 6500 + 39050 = 52890
	        Assert.assertEquals(52890, tradebook.getTotal(testStockCollections));
	    }



}