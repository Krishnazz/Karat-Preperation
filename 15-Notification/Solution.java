import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.*;

public class Solution {

    enum DeliveryStatus {
        QUEUED(1),
        SENT(2),
        DELIVERED(3),
        FAILED(4);

        private final int rank;

        DeliveryStatus(int rank) {
            this.rank = rank;
        }

        int getRank() {
            return rank;
        }
    }

    /** Data about a single notification created for an order. */
    static class Notification {
        final int notificationId;
        final int orderId;
        final int createdAt;        // minutes since the start of the window

        Notification(int notificationId, int orderId, int createdAt) {
            this.notificationId = notificationId;
            this.orderId = orderId;
            this.createdAt = createdAt;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Notification n)) {
                return false;
            }
            return notificationId == n.notificationId
                    && orderId == n.orderId
                    && createdAt == n.createdAt;
        }

        @Override
        public int hashCode() {
            return Objects.hash(notificationId, orderId, createdAt);
        }

        @Override
        public String toString() {
            return "Notification " + notificationId + " (order " + orderId + ") created=" + createdAt;
        }
    }

    /**
     * Data for managing order notifications, and methods staff use to query them.
     */
    static class NotificationCenter {
        private final ArrayList<Notification> notifications = new ArrayList<>();

        /** Registers a notification for the current window. */
        void addNotification(Notification notification) {
            notifications.add(notification);
        }

        /** Returns the notification with the given ID, or null if there is no such notification. */
        Notification getNotification(int notificationId) {
            for (Notification notification : notifications) {
                if (notification.notificationId == notificationId) {
                    return notification;
                }
            }
            return null;
        }

        /** Returns the IDs of all notifications for the given order, sorted in ascending order. */
        List<Integer> getOrderNotifications(int orderId) {
            List<Integer> result = new ArrayList<>();
            for (Notification notification : notifications) {
                if (notification.orderId == orderId) {
                    result.add(notification.notificationId);
                }
            }
            result.sort(Integer::compareTo);
            return result;
        }
        
        Map<Integer,Set<String>> NotificationUpdates =new HashMap<>();
       int addUpdates(int notificationId, List<Object[]> updates){
    	   int count=0;
    	  if(getNotification(notificationId)==null) {
    		  return count;
    	  }
    		  
    		Set<String> update=   NotificationUpdates.computeIfAbsent(notificationId,k->new HashSet<>());
    		
    		for(Object[] u:updates) {
    			if(!update.contains(u[0]+" "+u[1])) {
    				update.add(u[0]+" "+u[1]);
    				count+=1;
    			}
    			
    		}
    		   
       
        	return count;
        }
       
       int getUpdateCount(int notificationId){
    	   
    	  if (NotificationUpdates.get(notificationId)==null) {
    		  return 0;
    	  }
    	  return NotificationUpdates.get(notificationId).size();
    	   
       }
    }
        
       
    public static class TestSuite {

        @Test
        public void testNotification() {
            Notification n = new Notification(1, 500, 30);
            assertEquals(1, n.notificationId);
            assertEquals(500, n.orderId);
            assertEquals(30, n.createdAt);
        }

        @Test
        public void testGetOrderNotifications() {
            NotificationCenter center = new NotificationCenter();
            center.addNotification(new Notification(1, 1, 0));
            center.addNotification(new Notification(2, 2, 0));
            center.addNotification(new Notification(3, 1, 0));
            center.addNotification(new Notification(4, 2, 0));
            center.addNotification(new Notification(5, 1, 0));

            // Order 1 owns notifications 1, 3, and 5.
            assertEquals(java.util.Arrays.asList(1, 3, 5), center.getOrderNotifications(1));
            // Order 2 owns notifications 2 and 4.
            assertEquals(java.util.Arrays.asList(2, 4), center.getOrderNotifications(2));
            // An order with no notifications returns an empty list.
            assertEquals(new ArrayList<>(), center.getOrderNotifications(99));
        }

        // Add these two methods inside the TestSuite class.
        @Test
        public void testAddUpdates() {
            NotificationCenter center = new NotificationCenter();
            center.addNotification(new Notification(10, 1, 0));
            center.addNotification(new Notification(20, 1, 0));

            // Two new updates stored.
            assertEquals(2, center.addUpdates(10, java.util.Arrays.asList(
                    new Object[] { DeliveryStatus.SENT, 5 },
                    new Object[] { DeliveryStatus.DELIVERED, 15 })));
            // (SENT, 5) is already stored, so only (FAILED, 20) is new.
            assertEquals(1, center.addUpdates(10, java.util.Arrays.asList(
                    new Object[] { DeliveryStatus.SENT, 5 },
                    new Object[] { DeliveryStatus.FAILED, 20 })));
            // Both already stored -> nothing new.
            assertEquals(0, center.addUpdates(10, java.util.Arrays.asList(
                    new Object[] { DeliveryStatus.SENT, 5 },
                    new Object[] { DeliveryStatus.FAILED, 20 })));
            // Within-batch duplicate: the second (SENT, 8) repeats the first, so only 2 are stored.
            assertEquals(2, center.addUpdates(20, java.util.Arrays.asList(
                    new Object[] { DeliveryStatus.SENT, 8 },
                    new Object[] { DeliveryStatus.SENT, 8 },
                    new Object[] { DeliveryStatus.DELIVERED, 12 })));
            // No notification with this ID: nothing is stored.
            assertEquals(0, center.addUpdates(999, java.util.Collections.singletonList(
                    new Object[] { DeliveryStatus.SENT, 0 })));
            // Same timestamp, different status -> both kept.
            assertEquals(2, center.addUpdates(10, java.util.Arrays.asList(
                    new Object[] { DeliveryStatus.SENT, 25 },
                    new Object[] { DeliveryStatus.FAILED, 25 })));
            // Same status, different timestamp -> both kept.
            assertEquals(2, center.addUpdates(20, java.util.Arrays.asList(
                    new Object[] { DeliveryStatus.SENT, 30 },
                    new Object[] { DeliveryStatus.SENT, 35 })));
        }

        @Test
        public void testGetUpdateCount() {
            NotificationCenter center = new NotificationCenter();
            center.addNotification(new Notification(10, 1, 0));
            center.addNotification(new Notification(30, 2, 0));   // never updated

            center.addUpdates(10, java.util.Arrays.asList(
                    new Object[] { DeliveryStatus.SENT, 5 },
                    new Object[] { DeliveryStatus.DELIVERED, 15 }));
            center.addUpdates(10, java.util.Arrays.asList(
                    new Object[] { DeliveryStatus.SENT, 5 },
                    new Object[] { DeliveryStatus.FAILED, 20 }));   // one duplicate
            center.addUpdates(999, java.util.Collections.singletonList(
                    new Object[] { DeliveryStatus.SENT, 0 }));                                // ignored

            assertEquals(3, center.getUpdateCount(10));
            assertEquals(0, center.getUpdateCount(30));    // exists but no updates
            assertEquals(0, center.getUpdateCount(999));   // no such notification
        }
    }

    public static void main(String[] args) {	
        Result result = JUnitCore.runClasses(TestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        if (result.wasSuccessful()) {
            System.out.println("All tests passed successfully.");
        } else {
            System.err.println("Tests failed: " + result.getFailureCount() + " test(s) failed.");
            System.exit(1);
        }
    }
}