package project1;

import org.junit.runner.JUnitCore;

import org.junit.runner.Result;

import org.junit.runner.notification.Failure;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.*;

 

enum ReservationStatus {

  ACTIVE,

  CANCELLED,

  EXPIRED

}

 

class ComputePool {

  int poolId;

  String region;

  int totalCapacity;

 

  public ComputePool(int poolId, String region, int totalCapacity) {

    this.poolId = poolId;

    this.region = region;

    this.totalCapacity = totalCapacity;

  }

}

 

class Reservation {

  int reservationId;

  int customerId;

  int poolId;

  int capacityUnits;

  ReservationStatus status;

  int startTime;

  int endTime;

 

  public Reservation(

    int reservationId,

    int customerId,

    int poolId,

    int capacityUnits,

    ReservationStatus status,

    int startTime,

    int endTime

  ) {

    this.reservationId = reservationId;

    this.customerId = customerId;

    this.poolId = poolId;

    this.capacityUnits = capacityUnits;

    this.status = status;

    this.startTime = startTime;

    this.endTime = endTime;

  }

}

 

class ReservationManager {

  Map<Integer, ComputePool> computePools = new HashMap<>();

  List<Reservation> reservations = new ArrayList<>();

 

  public void addComputePool(ComputePool computePool) {

    computePools.put(computePool.poolId, computePool);

  }

 

  public int getAvailableCapacity(int poolId) {

    if (!computePools.containsKey(poolId)) {

      return 0;

    }

 

    ComputePool pool = computePools.get(poolId);

 

    int usedCapacity = 0;

    for (Reservation reservation : reservations) {

      if (reservation.poolId == poolId) {

        if (ReservationStatus.ACTIVE.equals(reservation.status)) //bug - there is no if condition here before, need to add this status check

          usedCapacity += reservation.capacityUnits;

      }

    }

 

    return pool.totalCapacity - usedCapacity;

  }

  


 

  

  
  public void addReservation(Reservation reservation) {
	  
		// TODO Auto-generated method stub
		  for(Map.Entry<Integer, ComputePool> entry : computePools.entrySet()) {
			  if(entry.getValue().poolId==reservation.poolId&& reservation.capacityUnits<=getAvailableCapacity(entry.getValue().poolId)) {
				  reservations.add(reservation);
			  }
		  }

	  }



	  public Map<Integer, Integer> getReservedCapacityByCustomer() {
		// TODO Auto-generated method stub
		  Map<Integer, Integer> count=new HashMap<>();
		  for(Reservation list:reservations) {
			  if(list.status == ReservationStatus.ACTIVE) {
				  count.put(list.customerId,count.getOrDefault(list.customerId, 0)+list.capacityUnits);
			  }
		  }
		  
		return count;
	  }
	  
	  public int expireReservations(int currentTime) {
		  
		  int expiredCount = 0;
		  for(Reservation reservation:reservations){
		 
		  if (reservation.status == ReservationStatus.ACTIVE && reservation.endTime <= currentTime) {
		  
		  reservation.status = ReservationStatus.EXPIRED;
		 
		  expiredCount++;
		   
		    }
		
		  }
		
		  
		  return expiredCount;
		  
		  }
		

}
 

public class Solution {

 

  public static class TestSuite {

 

    @Test

    public void testGetAvailableCapacity() {

      System.out.println("Running testGetAvailableCapacity");

 

      ReservationManager manager = new ReservationManager();

      manager.addComputePool(new ComputePool(1, "us-east", 100));

 

      manager.reservations.add(

        new Reservation(101, 1001, 1, 30, ReservationStatus.ACTIVE, 1000, 2000)

      );

 

      manager.reservations.add(

        new Reservation(102, 1002, 1, 20, ReservationStatus.CANCELLED, 1000, 2000)

      );

 

      assertEquals(70, manager.getAvailableCapacity(1));

      assertEquals(0, manager.getAvailableCapacity(99));

    }

    

    @Test

    public void testAddReservation() {

        System.out.println("Running testAddReservation");

 

        ReservationManager manager = new ReservationManager();

        manager.addComputePool(new ComputePool(1, "us-east", 100));

 

        manager.reservations.add(

                new Reservation(201, 1001, 1, 50, ReservationStatus.CANCELLED, 1000, 2000)

        );

 

        manager.addReservation(

                new Reservation(202, 1002, 1, 30, ReservationStatus.ACTIVE, 1000, 2000)

        );

 

        manager.reservations.add(

                new Reservation(203, 1003, 1, 40, ReservationStatus.EXPIRED, 1000, 2000)

        );

 

        manager.addReservation(

                new Reservation(204, 1004, 1, 60, ReservationStatus.ACTIVE, 1000, 2000)

        );

 

        manager.addReservation(

                new Reservation(205, 1005, 99, 20, ReservationStatus.ACTIVE, 1000, 2000)

        );

 

        manager.addReservation(

                new Reservation(206, 1006, 1, 50, ReservationStatus.ACTIVE, 1000, 2000)

        );

 

        assertEquals(4, manager.reservations.size());

        assertEquals(204, manager.reservations.get(3).reservationId);

    }

 

 

    @Test

    public void testGetReservedCapacityByCustomer() {

        System.out.println("Running testGetReservedCapacityByCustomer");

 

        ReservationManager manager = new ReservationManager();

        manager.addComputePool(new ComputePool(1, "us-east", 200));

 

        manager.addReservation(

                new Reservation(301, 1001, 1, 30, ReservationStatus.ACTIVE, 1000, 2000)

        );

        manager.addReservation(

                new Reservation(302, 1001, 1, 20, ReservationStatus.ACTIVE, 1000, 2000)

        );

        manager.addReservation(

                new Reservation(303, 1002, 1, 10, ReservationStatus.ACTIVE, 1000, 2000)

        );

        manager.addReservation(

                new Reservation(304, 1003, 1, 50, ReservationStatus.CANCELLED, 1000, 2000)

        );

 

        Map<Integer, Integer> result = manager.getReservedCapacityByCustomer();

 

        Map<Integer, Integer> expected = new HashMap<>();

        expected.put(1001, 50);

        expected.put(1002, 10);

 

        assertEquals(expected, result);

    }

    

    @Test

    public void testExpireReservations() {

        System.out.println("Running testExpireReservations");

 

        ReservationManager manager = new ReservationManager();

        manager.addComputePool(new ComputePool(1, "us-east", 500));

 

        manager.addReservation(

                new Reservation(401, 1001, 1, 10, ReservationStatus.ACTIVE, 100, 1000)

        );

        manager.addReservation(

                new Reservation(402, 1002, 1, 20, ReservationStatus.ACTIVE, 100, 900)

        );

        manager.addReservation(

                new Reservation(403, 1003, 1, 30, ReservationStatus.ACTIVE, 100, 1100)

        );

        manager.addReservation(

                new Reservation(404, 1004, 1, 40, ReservationStatus.CANCELLED, 100, 800)

        );

        manager.addReservation(

                new Reservation(405, 1005, 1, 50, ReservationStatus.EXPIRED, 100, 700)

        );

 

        int changed = manager.expireReservations(1000);

 

        assertEquals(2, changed);

        assertEquals(ReservationStatus.EXPIRED, manager.reservations.get(0).status);

        assertEquals(ReservationStatus.EXPIRED, manager.reservations.get(1).status);

        assertEquals(ReservationStatus.ACTIVE, manager.reservations.get(2).status);

        assertEquals(ReservationStatus.CANCELLED, manager.reservations.get(3).status);

        assertEquals(ReservationStatus.EXPIRED, manager.reservations.get(4).status);

        assertEquals(470, manager.getAvailableCapacity(1));

    }

 

 

 

  }

 

  public static void main(String[] argv) {

    Result result = JUnitCore.runClasses(TestSuite.class);

 

    for (Failure failure : result.getFailures()) {

      System.out.println(failure.getTrace());

    }

 

    if (result.wasSuccessful()) {

      System.out.println("All tests passed.");

    } else {

      System.out.println("Some tests failed.");

    }

  }

}


 
