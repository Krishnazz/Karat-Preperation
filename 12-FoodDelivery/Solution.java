
import java.util.*;
import java.util.stream.Collectors;

import org.junit.Assert;


enum OrderStatus {
 PLACED,
 PREPARING,
 OUT_FOR_DELIVERY,
 DELIVERED,
 CANCELED
}

class Delivery {
 int deliveryId;
 int startMinute;
 int endMinute;

 Delivery(int deliveryId, int startMinute, int endMinute) {
     this.deliveryId = deliveryId;
     this.startMinute = startMinute;
     this.endMinute = endMinute;
 }

 int getDurationMinutes() {
     return endMinute - startMinute;
 }
}

class Order {
 int orderId;
 int restaurantId;
 int customerId;
 double orderValue;
 double distanceKm;
 OrderStatus status;
 List<Delivery> deliveries = new ArrayList<>();  // Task 2

 Order(int orderId, int restaurantId, int customerId,
       double orderValue, double distanceKm, OrderStatus status) {
     this.orderId = orderId;
     this.restaurantId = restaurantId;
     this.customerId = customerId;
     this.orderValue = orderValue;
     this.distanceKm = distanceKm;
     this.status = status;
 }
}

class OrderStats {
 int totalOrders;
 int activeOrders;
 int closedOrders;

 OrderStats(int totalOrders, int activeOrders, int closedOrders) {
     this.totalOrders = totalOrders;
     this.activeOrders = activeOrders;
     this.closedOrders = closedOrders;
 }
}


class OrderManager {

 List<Order> orders = new ArrayList<>();

 void addOrder(Order order) {
     orders.add(order);
 }

 void updateOrderStatus(int orderId, OrderStatus newStatus) {
     for (Order o : orders) {
         if (o.orderId == orderId) {
             o.status = newStatus;
             return;
         }
     }
 }


public void addDelivery(int orderId, Delivery delivery) {
		
		for(Order order: orders) {
			if(order.orderId == orderId) {
				order.deliveries.add(delivery);
				break;
			}
		}
		
	}

    OrderStats getOrderStatistics() {
        int total = orders.size();
        int active = 0;
        int closed = 0;
        for (Order o : orders) {
            if (o.status == OrderStatus.PLACED || o.status == OrderStatus.OUT_FOR_DELIVERY || o.status == OrderStatus.PREPARING) {
                active++;
            } else if (o.status == OrderStatus.DELIVERED ||
                    o.status == OrderStatus.CANCELED) {
                closed++;
            }
        }
        
        System.out.println();

        return new OrderStats(total, active, closed);
    }

	public Map<Integer, Double> getAverageDeliveryTimeByRestaurant() {
		
		Map<Integer, List<Order>> orderDelivery = orders.stream()
		.collect(Collectors.groupingBy(o -> o.restaurantId));
		
		return orderDelivery.entrySet()
				.stream()
				.collect(Collectors.toMap(
						order -> order.getKey(),
						order -> order.getValue().stream()
						.flatMap(k -> k.deliveries.stream())
						.mapToDouble(Delivery::getDurationMinutes)
						.average()
						.orElse(0.0)
						));
	}

}

public class Solution {

 public static void main(String[] args) {
     testOrderManager();
     testGetAverageDeliveryTimeByRestaurant();
     System.out.println("All tests passed.");
 }

 public static void testOrderManager() {
     System.out.println("Running testOrderManager");

     OrderManager om = new OrderManager();
     om.addOrder(new Order(1, 10, 100, 25.0, 3.2, OrderStatus.PLACED));
     om.addOrder(new Order(2, 10, 101, 55.0, 1.4, OrderStatus.PREPARING));
     om.addOrder(new Order(3, 11, 102, 15.0, 6.0, OrderStatus.OUT_FOR_DELIVERY));
     om.addOrder(new Order(4, 11, 103, 40.0, 2.0, OrderStatus.DELIVERED));
     om.addOrder(new Order(5, 12, 104, 18.0, 4.5, OrderStatus.CANCELED));

     OrderStats stats = om.getOrderStatistics();
     Assert.assertEquals(5, stats.totalOrders);
     Assert.assertEquals(3, stats.activeOrders);
     Assert.assertEquals(2, stats.closedOrders);
 }

 private static void assertAlmost(double expected, double actual, double eps) {
     Assert.assertTrue(Math.abs(expected - actual) <= eps);
 }

 public static void testGetAverageDeliveryTimeByRestaurant() {
     System.out.println("Running testGetAverageDeliveryTimeByRestaurant");

     OrderManager om = new OrderManager();
     om.addOrder(new Order(1, 10, 100, 25.0, 3.2, OrderStatus.DELIVERED));
     om.addOrder(new Order(2, 10, 101, 55.0, 1.4, OrderStatus.DELIVERED));
     om.addOrder(new Order(3, 11, 102, 15.0, 6.0, OrderStatus.DELIVERED));

     om.addDelivery(1, new Delivery(101, 10, 40));      // 30
     om.addDelivery(2, new Delivery(102, 50, 80));      // 30
     om.addDelivery(2, new Delivery(103, 90, 150));     // 60
     om.addDelivery(3, new Delivery(104, 20, 50));      // 30

     om.addDelivery(999, new Delivery(105, 0, 10));     // ignored

     Map<Integer, Double> avg = om.getAverageDeliveryTimeByRestaurant();

     assertAlmost(40.0, avg.get(10), 0.0001);
     assertAlmost(30.0, avg.get(11), 0.0001);
 }


}
