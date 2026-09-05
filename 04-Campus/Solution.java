import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.*;

public class Solution {

    enum AccessLevel {
        VISITOR(1),
        STAFF(2),
        ADMIN(3);
        private final int rank;

        AccessLevel(int rank) {
            this.rank = rank;
        }

        int getRank() {
            return rank;
        }
    }
    /**
     * A single badge-in/badge-out session for a user. Has a unique ID and an entry
     * and exit time, each measured in minutes since the start of the day.
     */
    static class AccessEvent {
        final int id;
        final int entryTime;
        final int exitTime;
        AccessEvent(int id, int entryTime, int exitTime) {
            this.id = id;
            this.entryTime = entryTime;
            this.exitTime = exitTime;
        }
    }

   /** Data about a campus user. */
    static class User {
        final int userId;
        final String name;
        AccessLevel accessLevel;

        User(int userId, String name, AccessLevel accessLevel) {
            this.userId = userId;
            this.name = name;
            this.accessLevel = accessLevel;
        }

       @Override
        public boolean equals(Object other) {
            if (!(other instanceof User u)) {
                return false;
            }
            return userId == u.userId
                    && name.equals(u.name)
                    && accessLevel == u.accessLevel;
        }

        @Override
       public int hashCode() {
            return Objects.hash(userId, name, accessLevel);
        }

        @Override
        public String toString() {
            return "User ID: " + userId + ", Name: " + name + ", Access Level: " + accessLevel;
        }
    }
    /**
     * Data for managing campus access, and methods staff use to query or update it.
     */
    static class AccessManager {
        private final ArrayList<User> users = new ArrayList<>();
        /** Registers a user on the campus. */
        void addUser(User user) {
            users.add(user);
        }

        /** Updates the access level of the given user. */
        void updateAccessLevel(int userId, AccessLevel accessLevel) {
            for (User user : users) {
                if (user.userId == userId) {
                    user.accessLevel = accessLevel;
                    break;
                }
            }
        }

        // VISITOR(1),
        //STAFF(2),
        //ADMIN(3);
        /** Returns whether the given user is authorized for the required access level. */

        boolean isAuthorized(int userId, AccessLevel requiredLevel) {
            for (User user : users) {
                if (user.userId == userId) {
                    if(user.accessLevel.getRank() >= requiredLevel.getRank() ){
                       return true;
                    }
                    // return user.accessLevel == requiredLevel;
                }
            }
            return false;
        }

        Map<Integer, List<AccessEvent>> events=new HashMap<>();
		public boolean logAccess(int userid, AccessEvent e1) {
			// TODO Auto-generated method stub
		
			for(User user:users) {
				if(user.userId==userid) {
					events.computeIfAbsent(userid, k->new ArrayList<>()).add(e1);
			    	//System.out.println(events);
			    	return true;
				}
			}
			
			return false;
		}

		public List<Integer> getAfterHoursUsers() {
			// TODO Auto-generated method stub
			List<Integer> afterHours=new ArrayList<>();
			for(Map.Entry<Integer, List<AccessEvent>> event:events.entrySet()) {
				for(AccessEvent e:event.getValue()) {
					if(e.entryTime<480||e.exitTime>1200) {
						afterHours.add(event.getKey());
						break;
					}
				}
			}
			
			 Collections.sort(afterHours);
			
			return afterHours;
		}
        
        

       

    }

    public static class TestSuite {

        @Test
        public void testUser() {
            User u = new User(1, "John Doe", AccessLevel.VISITOR);

            assertEquals(1, u.userId);
            assertEquals("John Doe", u.name);
            assertEquals(AccessLevel.VISITOR, u.accessLevel);

        }

        @Test
        public void testIsAuthorized() {

            AccessManager manager = new AccessManager();

            manager.addUser(new User(1, "Vera Visitor", AccessLevel.VISITOR));
            manager.addUser(new User(2, "Sam Staff", AccessLevel.STAFF));
            manager.addUser(new User(3, "Ada Admin", AccessLevel.ADMIN));

            // A user is authorized for their own level and for any lower level.
            assertTrue(manager.isAuthorized(1, AccessLevel.VISITOR));
            assertFalse(manager.isAuthorized(1, AccessLevel.STAFF));
            assertTrue(manager.isAuthorized(2, AccessLevel.STAFF));
            assertTrue(manager.isAuthorized(2, AccessLevel.VISITOR));
            assertFalse(manager.isAuthorized(2, AccessLevel.ADMIN));
            assertTrue(manager.isAuthorized(3, AccessLevel.ADMIN));
            assertTrue(manager.isAuthorized(3, AccessLevel.VISITOR));

            // An unknown user is never authorized.
            assertFalse(manager.isAuthorized(999, AccessLevel.VISITOR));
        }

        // Add the tests below inside the TestSuite class.
        @Test
        public void testLogAccess() {

            AccessManager manager = new AccessManager();
            manager.addUser(new User(12, "John Doe", AccessLevel.STAFF));
            manager.addUser(new User(22, "Alex Cleeve", AccessLevel.VISITOR));

            AccessEvent e1 = new AccessEvent(111, 10, 20);
            AccessEvent e2 = new AccessEvent(112, 15, 35);
            AccessEvent e3 = new AccessEvent(113, 20, 25);
            AccessEvent e99 = new AccessEvent(999, 1, 2);

            assertTrue(manager.logAccess(12, e1));
            assertTrue(manager.logAccess(22, e2));
            assertTrue(manager.logAccess(12, e3));

            // Non-existent user: event should be ignored.
            assertFalse(manager.logAccess(404, e99));
        }

        @Test
        public void testGetAfterHoursUsers() {
            AccessManager manager = new AccessManager();
            manager.addUser(new User(30, "Late Leaver", AccessLevel.ADMIN));
            manager.addUser(new User(10, "Early Bird", AccessLevel.STAFF));
            manager.addUser(new User(50, "No Shows", AccessLevel.VISITOR));
            manager.addUser(new User(40, "On The Dot", AccessLevel.STAFF));
            manager.addUser(new User(20, "Nine To Five", AccessLevel.VISITOR));
            // user 30 -> single event enters in-hours but exits after closing (1250 > 1200)
            manager.logAccess(30, new AccessEvent(505, 1150, 1250));
            // user 10 -> one event starts before opening (400 < 480): after-hours
            manager.logAccess(10, new AccessEvent(501, 400, 450));
            manager.logAccess(10, new AccessEvent(502, 500, 600));
            // user 20 -> both events within hours
            manager.logAccess(20, new AccessEvent(503, 600, 700));
            manager.logAccess(20, new AccessEvent(504, 800, 1100));
            // user 40 -> single event exactly on the boundary (opens 480, closes 1200): within hours
            manager.logAccess(40, new AccessEvent(506, 480, 1200));
            // user 50 -> no events
            java.util.List<Integer> expected = new ArrayList<>();
            expected.add(10);
            expected.add(30);
            assertEquals(expected, manager.getAfterHoursUsers());
        }
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.getTrace());
        }
        if (result.wasSuccessful()) {
            System.out.println("All tests passed successfully.");
        } else {
            System.err.println("Tests failed: " + result.getFailureCount() + " test(s) failed.");
            System.exit(1);
        }
    }
}

