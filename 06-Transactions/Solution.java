import java.util.*;

import org.junit.*;

import java.util.stream.Collectors;

enum TransactionType {
    CREDIT,
    DEBIT
}

class Account {
    int accountId;
    String ownerName;

    Account(int accountId, String ownerName) {
        this.accountId = accountId;
        this.ownerName = ownerName;
    }
}

class Transaction {
    int transactionId;
    int accountId;
    TransactionType type;
    double amount;     // Always positive in inputs
    long timestampSec; // Unix-style seconds (monotonic for tests)

    Transaction(int transactionId, int accountId, TransactionType type, double amount, long timestampSec) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestampSec = timestampSec;
    }
    // i added
    public long gettimestampSec() {
    	return timestampSec;
    }

}

class AccountManager {
    Map<Integer, Account> accounts = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();

    void addAccount(Account account) {
        accounts.put(account.accountId, account);
    }

    void addTransaction(Transaction tx) {
        // Assume input transactions always refer to valid accounts for this question.
        transactions.add(tx);
    }

    // Returns the current balance for the given accountId.
    double getBalance(int accountId) {
        double balance = 0.0;
        for (Transaction tx : transactions) {
            if (tx.accountId == accountId) {
                if (tx.type == TransactionType.CREDIT) {
                    balance += tx.amount;
                } 
                else if (tx.type == TransactionType.DEBIT) {
                    balance -= tx.amount;
                }
            }
            
               
            
        }
        return balance;
    }


    public Map<Integer, Double> getAverageTransactionAmountByAccount() {
        Map<Integer, Double> result = new HashMap<>();
        
        //Method 1 : Stream
       result=transactions.stream().collect(Collectors.groupingBy(i->i.accountId,Collectors.averagingDouble(i->i.amount)));
        System.out.println(result);
       
       // Method 2 : BruteForce
        Map<Integer,Double> tot=new HashMap<>();
        Map<Integer,Integer> count=new HashMap<>();
        for(Transaction transaction:transactions) {
        	tot.put(transaction.accountId, tot.getOrDefault(transaction.accountId, 0.0)+transaction.amount);
        	count.put(transaction.accountId,count.getOrDefault(transaction.accountId,0)+1);
        }
        for(Map.Entry<Integer, Double> hmap:tot.entrySet()) {
        	double total=hmap.getValue();
        	int n=count.get(hmap.getKey());
        	result.put(hmap.getKey(), total/n);
        	
        }
        System.out.println(result);
        return result;
    }

    public Map<Integer, Double> getTransactionFees() {
        Map<Integer, Double> fees = new HashMap<>();
       
        Map<Integer,List<Transaction>>list=transactions.stream().collect(Collectors.groupingBy(t->t.accountId));
   
        for(Map.Entry<Integer,List<Transaction>> hmap:list.entrySet()) {
        	hmap.getValue().sort(Comparator.comparingLong(i->i.timestampSec));
        	
        	for(int i=3;i<hmap.getValue().size();i++) {
        	int key=hmap.getKey();
        
        	if(hmap.getValue().get(i).type==TransactionType.CREDIT) {
        		fees.put(key, fees.getOrDefault(key, 0.0)+1);
        	}
        	else {
        		fees.put(key, fees.getOrDefault(key, 0.0)+2);
        	}
        	
        }
        
        
        	
        }
        System.out.println(fees);
   
        return fees;
    }

    public List<Integer> getSuspiciousAccounts() {
        List<Integer> suspicious = new ArrayList<>();
        
        Map<Integer, List<Long>> map = new HashMap<>();
 
        for(Transaction transaction:transactions) {
        	if(transaction.type==TransactionType.DEBIT&&transaction.amount>=50) {
        		
        		map.computeIfAbsent(transaction.accountId, k -> new ArrayList<>()).add(transaction.timestampSec);
        	}
        	
        }
        for(Map.Entry<Integer, List<Long>> hmap:map.entrySet()) {
        	List<Long> times=hmap.getValue();
        	Collections.sort(times);
        	for(int i=2;i<times.size();i++) {
        		if(times.get(i)-times.get(i-2)<=60) {
        			suspicious.add(hmap.getKey());
        		}
        	}
        }
        System.out.println(suspicious);
        return suspicious;
    }
    
    //  TopSpendingAccount
    
    public int getTopSpendingAccount() {

        Map<Integer, Double> spending = new HashMap<>();

        for (Transaction tx : transactions) {
            if (tx.type == TransactionType.DEBIT) {
                spending.put(
                    tx.accountId,
                    spending.getOrDefault(tx.accountId, 0.0) + tx.amount
                );
            }
        }

        int result = -1;
        double max = 0;

        for (Map.Entry<Integer, Double> entry : spending.entrySet()) {

            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            } else if (entry.getValue() == max &&
                       entry.getKey() < result) {
                result = entry.getKey();
            }
        }

        return result;
    }
    
    //TransactionCountByAccount
    
    public Map<Integer, Integer> getTransactionCountByAccount() {

        Map<Integer, Integer> counts = new HashMap<>();

        for (Transaction tx : transactions) {
            counts.put(
                tx.accountId,
                counts.getOrDefault(tx.accountId, 0) + 1
            );
        }

        return counts;
    }
}

public class Solution {

    public static void main(String[] args) {
        testGetBalance_basic();
        testGetBalance_multipleAccounts();
        testGetAverageTransactionAmountByAccount();
        testGetTransactionFees();
        testGetSuspiciousAccounts();
        System.out.println("All tests passed.");
    }

    private static void assertAlmost(double expected, double actual, double eps) {
        Assert.assertTrue("Expected " + expected + " but got " + actual, Math.abs(expected - actual) <= eps);
    }

    public static void testGetBalance_basic() {
        System.out.println("Running testGetBalance_basic");
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));

        mgr.addTransaction(new Transaction(101, 1, TransactionType.CREDIT, 100.0, 1000));
        mgr.addTransaction(new Transaction(102, 1, TransactionType.DEBIT, 30.0, 1010));
        mgr.addTransaction(new Transaction(103, 1, TransactionType.DEBIT, 20.0, 1020));
        mgr.addTransaction(new Transaction(104, 1, TransactionType.CREDIT, 10.0, 1030));

        // Expected balance: 100 - 30 - 20 + 10 = 60
        assertAlmost(60.0, mgr.getBalance(1), 0.0001);
    }

    public static void testGetBalance_multipleAccounts() {
        System.out.println("Running testGetBalance_multipleAccounts");
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));

        mgr.addTransaction(new Transaction(201, 1, TransactionType.CREDIT, 50.0, 2000));
        mgr.addTransaction(new Transaction(202, 2, TransactionType.CREDIT, 80.0, 2005));
        mgr.addTransaction(new Transaction(203, 1, TransactionType.DEBIT, 10.0, 2010));
        mgr.addTransaction(new Transaction(204, 2, TransactionType.DEBIT, 5.5, 2015));
        mgr.addTransaction(new Transaction(205, 2, TransactionType.DEBIT, 14.5, 2020));

        // Account 1: 50 - 10 = 40
        assertAlmost(40.0, mgr.getBalance(1), 0.0001);
        // Account 2: 80 - 5.5 - 14.5 = 60
        assertAlmost(60.0, mgr.getBalance(2), 0.0001);
    }

    public static void testGetAverageTransactionAmountByAccount() {
        System.out.println("Running testGetAverageTransactionAmountByAccount");
        AccountManager mgr = new AccountManager();

        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));
        mgr.addAccount(new Account(3, "Charlie")); // no transactions

        // Account 1: 100, 30, 20, 10 => avg = 160/4 = 40
        mgr.addTransaction(new Transaction(101, 1, TransactionType.CREDIT, 100.0, 1000));
        mgr.addTransaction(new Transaction(102, 1, TransactionType.DEBIT, 30.0, 1010));
        mgr.addTransaction(new Transaction(103, 1, TransactionType.DEBIT, 20.0, 1020));
        mgr.addTransaction(new Transaction(104, 1, TransactionType.CREDIT, 10.0, 1030));

        // Account 2: 80, 5.5, 14.5 => avg = 100/3 = 33.333...
        mgr.addTransaction(new Transaction(201, 2, TransactionType.CREDIT, 80.0, 2005));
        mgr.addTransaction(new Transaction(202, 2, TransactionType.DEBIT, 5.5, 2015));
        mgr.addTransaction(new Transaction(203, 2, TransactionType.DEBIT, 14.5, 2020));

        Map<Integer, Double> avg = mgr.getAverageTransactionAmountByAccount();

        assertAlmost(40.0, avg.get(1), 0.0001);
        assertAlmost(33.3333, avg.get(2), 0.0001);

        // Account 3 has no transactions -> should not be present
        Assert.assertFalse(avg.containsKey(3));
    }

    public static void testGetTransactionFees() {
        System.out.println("Running testGetTransactionFees");
        AccountManager mgr = new AccountManager();

        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));
        mgr.addAccount(new Account(3, "Jane"));

        // Account 1: 5 transactions
        mgr.addTransaction(new Transaction(1, 1, TransactionType.CREDIT, 100.0, 1000));
        mgr.addTransaction(new Transaction(2, 1, TransactionType.DEBIT, 20.0, 1010));
        mgr.addTransaction(new Transaction(3, 1, TransactionType.CREDIT, 10.0, 1020));
        mgr.addTransaction(new Transaction(4, 1, TransactionType.DEBIT, 5.0, 1030));  // fee: $2
        mgr.addTransaction(new Transaction(5, 1, TransactionType.CREDIT, 7.0, 1040)); // fee: $1

        // Account 2: 4 transactions
        mgr.addTransaction(new Transaction(6, 2, TransactionType.DEBIT, 50.0, 2000));
        mgr.addTransaction(new Transaction(7, 2, TransactionType.DEBIT, 10.0, 2010));
        mgr.addTransaction(new Transaction(8, 2, TransactionType.CREDIT, 20.0, 2020));
        mgr.addTransaction(new Transaction(9, 2, TransactionType.DEBIT, 5.0, 2030)); // fee: $2

        // Account 3: 4 transactions
        mgr.addTransaction(new Transaction(26, 3, TransactionType.DEBIT, 50.0, 2000));
        mgr.addTransaction(new Transaction(27, 3, TransactionType.DEBIT, 10.0, 2010));
        mgr.addTransaction(new Transaction(28, 3, TransactionType.CREDIT, 20.0, 2020)); // should be 4th → $1
        mgr.addTransaction(new Transaction(29, 3, TransactionType.DEBIT, 5.0, 2005));


        Map<Integer, Double> fees = mgr.getTransactionFees();

        // Account 1: $2 + $1 = $3
        assertAlmost(3.0, fees.get(1), 0.0001);

        // Account 2: $2
        assertAlmost(2.0, fees.get(2), 0.0001);

        // Account 3: 4th transaction (chronologically) is CREDIT → $1
        assertAlmost(1.0, fees.get(3), 0.0001);
    }

    public static void testGetSuspiciousAccounts() {
        System.out.println("Running testGetSuspiciousAccounts");

        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));
        mgr.addAccount(new Account(3, "Charlie"));

        // Account 1: three large debits within 60 seconds -> suspicious
        mgr.addTransaction(new Transaction(1, 1, TransactionType.DEBIT, 50.0, 1000));
        mgr.addTransaction(new Transaction(2, 1, TransactionType.DEBIT, 70.0, 1030));
        mgr.addTransaction(new Transaction(3, 1, TransactionType.DEBIT, 90.0, 1060)); // 1000..1060 inclusive => suspicious

        // Account 2: three large debits but spread out > 60 seconds -> NOT suspicious
        mgr.addTransaction(new Transaction(4, 2, TransactionType.DEBIT, 60.0, 2000));
        mgr.addTransaction(new Transaction(5, 2, TransactionType.DEBIT, 80.0, 2070));
        mgr.addTransaction(new Transaction(6, 2, TransactionType.DEBIT, 55.0, 2141)); // no 60-sec window contains all 3

        // Account 3: has credits and small debits; should not be suspicious
        mgr.addTransaction(new Transaction(7, 3, TransactionType.CREDIT, 1000.0, 3000));
        mgr.addTransaction(new Transaction(8, 3, TransactionType.DEBIT, 49.99, 3010));
        mgr.addTransaction(new Transaction(9, 3, TransactionType.DEBIT, 50.0, 3020));
        mgr.addTransaction(new Transaction(10, 3, TransactionType.DEBIT, 50.0, 3100)); // only 2 large debits within any window

        List<Integer> suspicious = mgr.getSuspiciousAccounts();
        Assert.assertEquals(Arrays.asList(1), suspicious);

        // Second test case: input order is shuffled; should still detect
        mgr = new AccountManager();
        mgr.addAccount(new Account(10, "Daisy"));

        mgr.addTransaction(new Transaction(100, 10, TransactionType.DEBIT, 50.0, 500));
        mgr.addTransaction(new Transaction(101, 10, TransactionType.DEBIT, 50.0, 560));
        mgr.addTransaction(new Transaction(102, 10, TransactionType.DEBIT, 50.0, 530)); // out of order
        // 500, 530, 560 => within 60 inclusive => suspicious

        suspicious = mgr.getSuspiciousAccounts();
        Assert.assertEquals(Arrays.asList(10), suspicious);
    }
}