import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import java.util.*;

class Device {
  String deviceId;
  String deviceType;
  String location;
  String status;
  int registeredDay;
  int nextMaintenanceDay;
  int failureCount;

  /**
   * Represents a single device with its identifying and lifecycle attributes.
   */
  public Device(
    String deviceId,
    String deviceType,
    String location,
    String status,
    int registeredDay,
    int nextMaintenanceDay,
    int failureCount
  ) {
    this.deviceId = deviceId;
    this.deviceType = deviceType;
    this.location = location;
    this.status = status;
    this.registeredDay = registeredDay;
    this.nextMaintenanceDay = nextMaintenanceDay;
    this.failureCount = failureCount;
  }
}

class ReplacementEntry {
  public String deviceId;
  public int failureCount;
  public int ageDays;

  public ReplacementEntry(String deviceId, int failureCount, int ageDays) {
    this.deviceId = deviceId;
    this.failureCount = failureCount;
    this.ageDays = ageDays;
  }
}

class DeviceLifecycleManager {
  static final Set<String> SUPPORTED_DEVICE_TYPES = new HashSet<>(
    Arrays.asList("SENSOR", "CAMERA", "GATEWAY")
  );

  static final Set<String> VALID_STATUSES = new HashSet<>(
    Arrays.asList("REGISTERED", "ACTIVE", "SUSPENDED", "MAINTENANCE_DUE", "RETIRED")
  );

  static final Map<String, Set<String>> VALID_TRANSITIONS = new HashMap<>();
  static {
    VALID_TRANSITIONS.put("REGISTERED", new HashSet<>(Arrays.asList("ACTIVE", "RETIRED")));
    VALID_TRANSITIONS.put("ACTIVE", new HashSet<>(Arrays.asList("SUSPENDED", "MAINTENANCE_DUE", "RETIRED")));
    VALID_TRANSITIONS.put("SUSPENDED", new HashSet<>(Arrays.asList("ACTIVE", "RETIRED")));
    VALID_TRANSITIONS.put("MAINTENANCE_DUE", new HashSet<>(Arrays.asList("ACTIVE", "RETIRED")));
    VALID_TRANSITIONS.put("RETIRED", new HashSet<>());
  }

  /**
   * Manages the collection of registered devices, keyed by device ID.
   */
  Map<String, Device> devices = new HashMap<>();

  /**
   * Updates the device's status if the transition is valid, and returns whether the update was applied.
   */
  public boolean updateDeviceStatus(String deviceId, String newStatus) {
    Device device = devices.get(deviceId);

    if (device == null) {
      return false;
    }
    System.out.println(newStatus);
    
    Set<String> hs = VALID_TRANSITIONS.get(device.status);
    
    if(!VALID_TRANSITIONS.get(device.status).contains(newStatus))
    return false;
    
  
    if (!VALID_STATUSES.contains(newStatus) ) {
      return false;
    }
    


    device.status = newStatus;
    
    return true;
  }

  public boolean addDevice(Device device) {
	// TODO Auto-generated method stub
	 if(!devices.containsKey(device.deviceId)&&VALID_STATUSES.contains(device.status)
			 &&SUPPORTED_DEVICE_TYPES.contains(device.deviceType)
			 &&device.registeredDay>=0) {
		 devices.put(device.deviceId, device);
		 return true;
	 }
	return false;
  }

  public Map<String, Integer> getActiveDeviceCountsByType() {
	// TODO Auto-generated method stub
	  Map<String, Integer> result=new HashMap<>();
	  for(Map.Entry<String, Device>device:devices.entrySet()) {
		 String key= device.getValue().deviceType;
		 if(device.getValue().status.equals("ACTIVE")) {
			 result.put(key, result.getOrDefault(key, 0)+1);
		 }
		  
	  }
	  System.out.println(result);
	return result;
  }
  
  
}

public class Solution {

  public static class TestSuite {

    /**
     * Test updateDeviceStatus.
     */
    @Test
    public void testUpdateDeviceStatus1() {
      System.out.println("Running testUpdateDeviceStatus1");

      // ACTIVE -> SUSPENDED: valid transition.
      DeviceLifecycleManager managerA = new DeviceLifecycleManager();
      managerA.devices.put(
        "D-1",
        new Device("D-1", "SENSOR", "WAREHOUSE-A", "ACTIVE", 10, 50, 1)
      );
      boolean resultA = managerA.updateDeviceStatus("D-1", "SUSPENDED");
      assertTrue(resultA);
      assertEquals("SUSPENDED", managerA.devices.get("D-1").status);

      // MAINTENANCE_DUE -> ACTIVE.
      DeviceLifecycleManager managerB = new DeviceLifecycleManager();
      managerB.devices.put(
        "D-3",
        new Device("D-3", "GATEWAY", "WAREHOUSE-B", "MAINTENANCE_DUE", 1, 15, 2)
      );
      boolean resultB = managerB.updateDeviceStatus("D-3", "ACTIVE");
      assertTrue(resultB);
      assertEquals("ACTIVE", managerB.devices.get("D-3").status);

      // REGISTERED -> RETIRED.
      DeviceLifecycleManager managerC = new DeviceLifecycleManager();
      managerC.devices.put(
        "D-4",
        new Device("D-4", "SENSOR", "WAREHOUSE-B", "REGISTERED", 3, 30, 0)
      );
      boolean resultC = managerC.updateDeviceStatus("D-4", "RETIRED");
      assertTrue(resultC);
      assertEquals("RETIRED", managerC.devices.get("D-4").status);

      // SUSPENDED -> ACTIVE.
      DeviceLifecycleManager managerD = new DeviceLifecycleManager();
      managerD.devices.put(
        "D-5",
        new Device("D-5", "CAMERA", "WAREHOUSE-A", "SUSPENDED", 7, 40, 1)
      );
      boolean resultD = managerD.updateDeviceStatus("D-5", "ACTIVE");
      assertTrue(resultD);
      assertEquals("ACTIVE", managerD.devices.get("D-5").status);
    }

    /**
     * Test updateDeviceStatus.
     */
    @Test
    public void testUpdateDeviceStatus2() {
      System.out.println("Running testUpdateDeviceStatus2");

      // ACTIVE -> REGISTERED: invalid transition.
      DeviceLifecycleManager managerA = new DeviceLifecycleManager();
      managerA.devices.put(
        "D-1",
        new Device("D-1", "SENSOR", "WAREHOUSE-A", "ACTIVE", 10, 50, 1)
      );
      boolean resultA = managerA.updateDeviceStatus("D-1", "REGISTERED");
      assertFalse(resultA);
      assertEquals("ACTIVE", managerA.devices.get("D-1").status);

      // RETIRED device.
      DeviceLifecycleManager managerB = new DeviceLifecycleManager();
      managerB.devices.put(
        "D-2",
        new Device("D-2", "CAMERA", "WAREHOUSE-A", "RETIRED", 5, 20, 4)
      );
      boolean resultB = managerB.updateDeviceStatus("D-2", "ACTIVE");
      assertFalse(resultB);
      assertEquals("RETIRED", managerB.devices.get("D-2").status);

      // Same-status transition.
      DeviceLifecycleManager managerC = new DeviceLifecycleManager();
      managerC.devices.put(
        "D-1",
        new Device("D-1", "SENSOR", "WAREHOUSE-A", "ACTIVE", 10, 50, 1)
      );
      boolean resultC = managerC.updateDeviceStatus("D-1", "ACTIVE");
      assertFalse(resultC);
      assertEquals("ACTIVE", managerC.devices.get("D-1").status);
    }

    /**
     * Test updateDeviceStatus.
     */
    @Test
    public void testUpdateDeviceStatus3() {
      System.out.println("Running testUpdateDeviceStatus3");

      // Unknown device ID.
      DeviceLifecycleManager managerA = new DeviceLifecycleManager();
      boolean resultA = managerA.updateDeviceStatus("UNKNOWN", "ACTIVE");
      assertFalse(resultA);

      // Unrecognized status.
      DeviceLifecycleManager managerB = new DeviceLifecycleManager();
      managerB.devices.put(
        "D-1",
        new Device("D-1", "SENSOR", "WAREHOUSE-A", "ACTIVE", 10, 50, 1)
      );
      boolean resultB = managerB.updateDeviceStatus("D-1", "BROKEN");
      assertFalse(resultB);
      assertEquals("ACTIVE", managerB.devices.get("D-1").status);
    }
    
        @Test
    public void testAddDevice1() {
      // Test that a valid device is added and stored, and a duplicate device ID is rejected.
      System.out.println("Running testAddDevice1");

      DeviceLifecycleManager manager = new DeviceLifecycleManager();

      // A valid device is added and stored by its device ID.
      Device first = new Device("D-1", "SENSOR", "WAREHOUSE-A", "REGISTERED", 10, 40, 0);
      assertTrue(manager.addDevice(first));
      assertEquals(first, manager.devices.get("D-1"));

      // Duplicate device ID.
      Device second = new Device("D-1", "CAMERA", "WAREHOUSE-B", "ACTIVE", 20, 50, 1);
      assertFalse(manager.addDevice(second));
      assertEquals(first, manager.devices.get("D-1"));
    }

    @Test
    public void testAddDevice2() {
      // Test that a device failing any single validation rule is rejected and the collection is left unchanged.
      System.out.println("Running testAddDevice2");

      DeviceLifecycleManager manager = new DeviceLifecycleManager();

      // Unsupported device type is rejected and never stored.
      Device unsupportedType = new Device("D-1", "THERMOSTAT", "WAREHOUSE-A", "REGISTERED", 10, 40, 0);
      assertFalse(manager.addDevice(unsupportedType));
      assertFalse(manager.devices.containsKey("D-1"));

      // Invalid status is rejected.
      Device invalidStatus = new Device("D-1", "SENSOR", "WAREHOUSE-A", "BROKEN", 10, 40, 0);
      assertFalse(manager.addDevice(invalidStatus));
      assertFalse(manager.devices.containsKey("D-1"));

      // Negative registeredDay is rejected.
      Device negativeRegisteredDay = new Device("D-1", "SENSOR", "WAREHOUSE-A", "REGISTERED", -1, 40, 0);
      assertFalse(manager.addDevice(negativeRegisteredDay));
      assertFalse(manager.devices.containsKey("D-1"));
    }

    @Test
    public void testAddDevice3() {
      // Test that a registeredDay of 0 is a valid boundary value.
      System.out.println("Running testAddDevice3");

      DeviceLifecycleManager manager = new DeviceLifecycleManager();

      // registeredDay of 0.
      Device zeroRegisteredDay = new Device("D-1", "SENSOR", "WAREHOUSE-A", "REGISTERED", 0, 10, 0);
      assertTrue(manager.addDevice(zeroRegisteredDay));
    }

    @Test
    public void testGetActiveDeviceCountsByType1() {
      // Test that active devices are counted and grouped by device type, ignoring non-active devices.
      System.out.println("Running testGetActiveDeviceCountsByType1");

      DeviceLifecycleManager manager = new DeviceLifecycleManager();
      List<Device> devices = Arrays.asList(
        new Device("D-1", "SENSOR", "WAREHOUSE-A", "ACTIVE", 10, 40, 0),
        new Device("D-2", "SENSOR", "WAREHOUSE-B", "ACTIVE", 12, 42, 1),
        new Device("D-3", "CAMERA", "WAREHOUSE-A", "SUSPENDED", 15, 45, 2),
        new Device("D-4", "GATEWAY", "WAREHOUSE-C", "ACTIVE", 20, 50, 0),
        // REGISTERED device.
        new Device("D-5", "CAMERA", "WAREHOUSE-B", "REGISTERED", 18, 48, 0)
      );

      for (Device device : devices) {
        manager.devices.put(device.deviceId, device);
      }

      Map<String, Integer> result = manager.getActiveDeviceCountsByType();

      Map<String, Integer> expected = new HashMap<>();
      expected.put("SENSOR", 2);
      expected.put("GATEWAY", 1);

      assertEquals(expected, result);
    }

    @Test
    public void testGetActiveDeviceCountsByType2() {
      // Test that a device type with no active devices does not appear in the result.
      System.out.println("Running testGetActiveDeviceCountsByType2");

      DeviceLifecycleManager manager = new DeviceLifecycleManager();
      manager.devices.put(
        "D-1",
        new Device("D-1", "SENSOR", "WAREHOUSE-A", "SUSPENDED", 10, 40, 0)
      );

      Map<String, Integer> result = manager.getActiveDeviceCountsByType();

      assertEquals(new HashMap<>(), result);
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
