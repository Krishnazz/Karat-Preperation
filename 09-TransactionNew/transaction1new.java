import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Objects;
import java.util.stream.Collectors;



import static org.junit.Assert.*;



public class transaction1new {



    /**

     * TransactionType represents the category of a financial event.

     * DEPOSIT and INTEREST are credits (they increase account balance).

     * WITHDRAWAL, FEE, and TRANSFER are debits (they decrease account balance).

     */

    enum TransactionType {

        DEPOSIT,

        WITHDRAWAL,

        TRANSFER,

        FEE,

        INTEREST,

    }



    /** Data about a single financial transaction. */

    static class Transaction {

        final String transactionId;

        final int accountId;

        final double amount;

        final TransactionType transactionType;

        final int timestamp;

        final String description;

        final String source;



        Transaction(String transactionId, int accountId, double amount,

                    TransactionType transactionType, int timestamp,

                    String description, String source) {

            this.transactionId = transactionId;

            this.accountId = accountId;

            this.amount = amount;

            this.transactionType = transactionType;

            this.timestamp = timestamp;

            this.description = description;

            this.source = source;

        }



        @Override

        public boolean equals(Object other) {

            if (!(other instanceof Transaction t)) return false;

            return transactionId.equals(t.transactionId)

                    && accountId == t.accountId

                    && Double.compare(amount, t.amount) == 0

                    && transactionType == t.transactionType

                    && timestamp == t.timestamp

                    && description.equals(t.description)

                    && source.equals(t.source);

        }



        @Override

        public int hashCode() {

            return Objects.hash(transactionId, accountId, amount, transactionType,

                    timestamp, description, source);

        }



        @Override

        public String toString() {

            return "Transaction ID: " + transactionId

                    + ", Account: " + accountId

                    + ", Amount: " + amount

                    + ", Type: " + transactionType

                    + ", Timestamp: " + timestamp

                    + ", Description: " + description

                    + ", Source: " + source;

        }

    }



    /**

     * Data for managing imported bank transactions, and methods which staff

     * can use to perform any queries or updates.

     */

    static class ImportSystem {

        final ArrayList<Transaction> transactions = new ArrayList<>();

        public Map<Integer,Double> balances=new HashMap<>();



        /** Adds a transaction to the system */

        void addTransaction(Transaction transaction) {

            transactions.add(transaction);

        }







        /** Calculates and returns summary statistics for a given account */

        Map<String, Object> getAccountSummary(int accountId) {

            double totalCredits = 0;

            double totalDebits = 0;

            int transactionCount = 0;

            for (Transaction t : transactions) {

                if (t.accountId != accountId) continue;

                transactionCount++;

                if (t.transactionType == TransactionType.DEPOSIT

                        || t.transactionType == TransactionType.INTEREST) {

                    totalCredits += t.amount;

                } else if (t.transactionType == TransactionType.WITHDRAWAL

                        || t.transactionType == TransactionType.FEE

                        || t.transactionType == TransactionType.TRANSFER) {

                    totalDebits += t.amount;

                }

            }



            double netChange = totalCredits-totalDebits;



            Map<String, Object> result = new HashMap<>();

            result.put("total_credits", totalCredits);

            result.put("total_debits", totalDebits);

            result.put("net_change", netChange);

            result.put("transaction_count", transactionCount);

            return result;

        }



//   2-2) recordTransaction(transactionId, accountId, amount, transactionType,

//   timestamp, description, source): create a new Transaction, append it to

//   the system, and update the account's balance based on the transaction's

//   direction. Credits (DEPOSIT, INTEREST) add the amount; debits (WITHDRAWAL,

//   FEE, TRANSFER) subtract it. If the account is new (not yet in balances),

//   initialize it to $0 before applying the change.



        public void recordTransaction(String transid, int acountId, double amount, TransactionType type, int time, String desc, String src) {
        	if(type==TransactionType.DEPOSIT||type==TransactionType.INTEREST) {
        		balances.put(acountId, balances.getOrDefault(acountId, 0.0)+amount);
        	}
        	else {
        		balances.put(acountId, balances.getOrDefault(acountId, 0.0)-amount);
        	}
        	transactions.add(new Transaction( transid,  acountId,  amount,  type,  time,  desc, src));

        }


        
//        getTypeSummary(): return a mapping from transaction type name (a string,
//
//        		   uppercase, matching the enum name exactly — for example "DEPOSIT") to an
//
//        		   inner mapping with keys "count" (integer) and "total_amount" (double or
//
//        		   equivalent). Aggregate across all accounts. Only include types that have
//
//        		   at least one transaction; omit types that have none.



        public Map<String, Map<String, Object>> getTypeSummary() {

            Map<String, Map<String, Object>> map=new HashMap<>();
            
            Map<TransactionType,List<Transaction>> transactionMap=transactions.stream().collect(Collectors.groupingBy(i->i.transactionType));
            for(Map.Entry<TransactionType,List<Transaction>>hmap:transactionMap.entrySet()) {
            	int n=(int)hmap.getValue().stream().count();
            	double tot=hmap.getValue().stream().mapToDouble(i->i.amount).sum();
            	Map<String, Object> innerMap=new HashMap<>();
            	innerMap.put("total_amount", tot);
            	innerMap.put("count", n);
            	map.put(String.valueOf(hmap.getKey()),innerMap);
            }
          System.out.println(map);
            return map;

        }

    }




    public static void main(String[] args) {
        testTransaction();
        testAccountSummary();
        testRecordTransaction();
        testGetTypeSummary();
        System.out.println("All testcases passed");
    }



        public static void testTransaction() {

            Transaction t = new Transaction(

                    "t001", 101, 500.00, TransactionType.DEPOSIT, 1, "Salary", "SRC_A");

            assertEquals("t001", t.transactionId);

            assertEquals(101, t.accountId);

            assertEquals(500.00, t.amount, 0.001);

            assertEquals(TransactionType.DEPOSIT, t.transactionType);

            assertEquals(1, t.timestamp);

            assertEquals("Salary", t.description);

            assertEquals("SRC_A", t.source);

        }



        public static void testAccountSummary() {

            ImportSystem system = new ImportSystem();



            // Account 101: 6 transactions covering all 5 types

            system.addTransaction(new Transaction("t001", 101, 500.00, TransactionType.DEPOSIT,    1, "Salary",        "SRC_A"));

            system.addTransaction(new Transaction("t002", 101, 300.00, TransactionType.DEPOSIT,    2, "Refund",        "SRC_A"));

            system.addTransaction(new Transaction("t003", 101, 150.00, TransactionType.WITHDRAWAL, 3, "ATM",           "SRC_A"));

            system.addTransaction(new Transaction("t004", 101,  50.00, TransactionType.FEE,        4, "Service fee",   "SRC_A"));

            system.addTransaction(new Transaction("t005", 101, 200.00, TransactionType.TRANSFER,   5, "Wire out",      "SRC_A"));

            system.addTransaction(new Transaction("t006", 101,  20.00, TransactionType.INTEREST,   6, "Interest paid", "SRC_A"));



            // Account 202: 2 transactions to verify per-account filtering

            system.addTransaction(new Transaction("t007", 202, 100.00, TransactionType.DEPOSIT,    7, "Deposit",       "SRC_A"));

            system.addTransaction(new Transaction("t008", 202,  30.00, TransactionType.WITHDRAWAL, 8, "Withdrawal",    "SRC_A"));



            // Expected: credits 820, debits 400, net 420

            Map<String, Object> summary101 = system.getAccountSummary(101);

            assertEquals(820.00, (double) summary101.get("total_credits"), 0.001);

            assertEquals(400.00, (double) summary101.get("total_debits"), 0.001);

            assertEquals(420.00, (double) summary101.get("net_change"), 0.001);

            assertEquals(6, (int) summary101.get("transaction_count"));



            // Expected: credits 100, debits 30, net 70



            Map<String, Object> summary202 = system.getAccountSummary(202);

            assertEquals(100.00, (double) summary202.get("total_credits"), 0.001);

            assertEquals(30.00, (double) summary202.get("total_debits"), 0.001);

            assertEquals(70.00, (double) summary202.get("net_change"), 0.001);

            assertEquals(2, (int) summary202.get("transaction_count"));

        }



        public static void testRecordTransaction() {

            ImportSystem system = new ImportSystem();



            // A first deposit to a new account.

            system.recordTransaction("t001", 101, 500.00, TransactionType.DEPOSIT, 1, "Salary", "SRC_A");

            assertEquals(1, system.transactions.size());

            assertEquals(500.00, system.balances.get(101), 0.001);



            // A cash withdrawal from the same account.

            system.recordTransaction("t002", 101, 150.00, TransactionType.WITHDRAWAL, 2, "ATM", "SRC_A");

            assertEquals(2, system.transactions.size());

            assertEquals(350.00, system.balances.get(101), 0.001);



            // A service fee charged to the same account.

            system.recordTransaction("t003", 101, 50.00, TransactionType.FEE, 3, "Service fee", "SRC_A");

            assertEquals(3, system.transactions.size());

            assertEquals(300.00, system.balances.get(101), 0.001);



            // Monthly interest credited to the same account.

            system.recordTransaction("t004", 101, 20.00, TransactionType.INTEREST, 4, "Interest paid", "SRC_A");

            assertEquals(4, system.transactions.size());

            assertEquals(320.00, system.balances.get(101), 0.001);



            // A deposit to a different, brand-new account.

            system.recordTransaction("t005", 202, 200.00, TransactionType.DEPOSIT, 5, "Deposit", "SRC_A");

            assertEquals(5, system.transactions.size());

            assertEquals(200.00, system.balances.get(202), 0.001);

            assertEquals(320.00, system.balances.get(101), 0.001);



            // A wire transfer out of the second account.

            system.recordTransaction("t006", 202, 50.00, TransactionType.TRANSFER, 6, "Wire", "SRC_A");

            assertEquals(6, system.transactions.size());

            assertEquals(150.00, system.balances.get(202), 0.001);

        }

        public static void testGetTypeSummary() {

            ImportSystem system = new ImportSystem();



            // A mix of transactions across two accounts covering all five types.

            system.recordTransaction("t001", 101, 500.00, TransactionType.DEPOSIT,    1, "Salary",       "SRC_A");

            system.recordTransaction("t002", 101, 300.00, TransactionType.DEPOSIT,    2, "Refund",       "SRC_A");

            system.recordTransaction("t003", 202, 800.00, TransactionType.DEPOSIT,    3, "Big deposit",  "SRC_A");

            system.recordTransaction("t004", 101, 150.00, TransactionType.WITHDRAWAL, 4, "ATM",          "SRC_A");

            system.recordTransaction("t005", 202,  50.00, TransactionType.WITHDRAWAL, 5, "ATM",          "SRC_A");

            system.recordTransaction("t006", 101,  50.00, TransactionType.FEE,        6, "Service fee",  "SRC_A");

            system.recordTransaction("t007", 202, 200.00, TransactionType.TRANSFER,   7, "Wire",         "SRC_A");

            system.recordTransaction("t008", 101,  20.00, TransactionType.INTEREST,   8, "Interest",     "SRC_A");



            Map<String, Map<String, Object>> expected = new HashMap<>();

            Map<String, Object> deposit = new HashMap<>();

            deposit.put("count", 3);

            deposit.put("total_amount", 1600.00);

            expected.put("DEPOSIT", deposit);

            Map<String, Object> withdrawal = new HashMap<>();

            withdrawal.put("count", 2);

            withdrawal.put("total_amount", 200.00);

            expected.put("WITHDRAWAL", withdrawal);

            Map<String, Object> fee = new HashMap<>();

            fee.put("count", 1);

            fee.put("total_amount", 50.00);

            expected.put("FEE", fee);

            Map<String, Object> transfer = new HashMap<>();

            transfer.put("count", 1);

            transfer.put("total_amount", 200.00);

            expected.put("TRANSFER", transfer);

            Map<String, Object> interest = new HashMap<>();

            interest.put("count", 1);

            interest.put("total_amount", 20.00);

            expected.put("INTEREST", interest);

            assertEquals(expected, system.getTypeSummary());



            // A separate system where only one transaction type is used.

            ImportSystem system2 = new ImportSystem();

            system2.recordTransaction("t100", 300, 100.00, TransactionType.DEPOSIT, 1, "First",  "SRC_A");

            system2.recordTransaction("t101", 300, 200.00, TransactionType.DEPOSIT, 2, "Second", "SRC_A");



            Map<String, Map<String, Object>> expected2 = new HashMap<>();

            Map<String, Object> depositOnly = new HashMap<>();

            depositOnly.put("count", 2);

            depositOnly.put("total_amount", 300.00);

            expected2.put("DEPOSIT", depositOnly);

            assertEquals(expected2, system2.getTypeSummary());

        }

    }
