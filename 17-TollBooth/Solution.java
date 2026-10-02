import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


class LogEntry {
    /**
     * Represents an entry from a single log line. Log lines look like this
     * in the file:
     * <p>
     * 34400.409 SXY288 210E ENTRY
     * <p>
     * Where:
     * * 34400.409 is the timestamp in seconds since the software was
     * started.
     * * SXY288 is the license plate of the vehicle passing through the toll
     * booth.
     * * 210E is the location and traffic direction of the toll booth. Here,
     * the toll
     * booth is at 210 kilometers from the start of the tollway, and the
     * E indicates
     * that the toll booth was on the east-bound traffic side.
     * Tollbooths are placed
     * every ten kilometers.
     * * ENTRY indicates which type of toll booth the vehicle went through.
     * This is one of
     * "ENTRY", "EXIT", or "MAINROAD".
     **/
    private final double timestamp;
    private final String licensePlate;
    private final String boothType;
    private final int location;
    private final String direction;

    public LogEntry(String logLine) {
        String[] tokens = logLine.split(" ");
        this.timestamp = Float.parseFloat(tokens[0]);
        this.licensePlate = tokens[1];
        this.boothType = tokens[3];
        this.location =
                Integer.parseInt(tokens[2].substring(0, tokens[2].length() - 1));
        String directionLetter = tokens[2].substring(tokens[2].length() -
                1);
        if (directionLetter.equals("E")) {
            this.direction = "EAST";
        } else if (directionLetter.equals("W")) {
            this.direction = "WEST";
        } else {
            throw new IllegalArgumentException();
        }
    }

    public double getTimestamp() {
        return timestamp;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBoothType() {
        return boothType;
    }

    public int getLocation() {
        return location;
    }

    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format(
                "<TollBooth.LogEntry timestamp: %.f license: %s location: %d direction: %s booth type: %s>",
                timestamp,
                licensePlate,
                location,
                direction,
                boothType
        );
    }
}

class LogFile {
    /*
    * Represents a file containing a number of log lines, converted to
   TollBooth.LogEntry
    * objects.
    */
    List<LogEntry> logEntries;

    public LogFile(BufferedReader reader) throws IOException {
        this.logEntries = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            LogEntry logEntry = new LogEntry(line.strip());
            this.logEntries.add(logEntry);
            line = reader.readLine();
        }
    }

    public LogEntry get(int index) {
        return this.logEntries.get(index);
    }

    public int size() {
        return this.logEntries.size();
    }

    public int countJourneys() {
        int count = 0;
        count=(int) logEntries.stream().filter(i->i.getBoothType().equals("EXIT")).count();
        System.out.println(count);
        return count;
        
    }

    public List<String> catchSpeeders() {
        List<String> speeders = new ArrayList<>();
        
        Map<String,LogEntry> previous =new HashMap<>();
        Map<String,Integer> Over120Km=new HashMap<>();
        Set<String> flaggedspeeders=new HashSet<>();
        
        for(LogEntry current:logEntries) {
        	String plate=current.getLicensePlate();
        	
        	if(previous.containsKey(plate)) {
        		
        		double time=current.getTimestamp()-previous.get(plate).getTimestamp();
        		
        		double speed=10*3600/time;
        		
        		if(speed>=120) {
        			Over120Km.put(plate, Over120Km.getOrDefault(plate, 0)+1);
        		}
        		if((speed>=130||Over120Km.get(plate)>1)&&!flaggedspeeders.contains(plate)) {
        			speeders.add(plate);
        			flaggedspeeders.add(plate);
        		}
        		
        		
        	}
        	
        	previous.put(plate, current);
        	
        	if(current.getBoothType().equals("EXIT")) {
        		previous.remove(plate);
        		Over120Km.remove(plate);
        		flaggedspeeders.remove(plate);
        	}
        	
        }
        
               
        return speeders;
    }
}

public class Solution {
    public static void main(String[] argv) throws IOException {
//        testLogFile();
        testLogEntry();
        testCountJourneys();
        testCatchSpeeders();
    }

    static final String SMALL_LOG =
            "90750.191 JOX304 250E ENTRY\n" +
                    "91081.684 JOX304 260E MAINROAD\n" +
                    "91483.251 JOX304 270E MAINROAD\n" +
                    "91874.493 JOX304 280E EXIT\n" +
                    "91082.101 THX138 110E ENTRY\n" +
                    "91873.920 THX138 120E MAINROAD\n" +
                    "91982.102 THX138 290E EXIT\n" +
                    "92301.302 THX138 300E ENTRY\n" +
                    "92371.302 THX138 310E EXIT\n";

    static final String SPEEDER_LOG =
            "1000.000 TST002 270W ENTRY\n" +
                    "1275.000 TST002 260W EXIT\n" +
                    "2000.000 TST003 100E ENTRY\n" +
                    "2200.000 TST003 110E MAINROAD\n" +
                    "2400.000 TST003 120E EXIT\n" +
                    "3000.000 TST003 130E ENTRY\n" +
                    "3200.000 TST003 140E EXIT\n";

    public static void testLogFile() throws IOException {
        System.out.println("Running testLogFile");
        try (
                BufferedReader reader = new BufferedReader(
                        new StringReader(SMALL_LOG)
                );
        ) {
            LogFile logFile = new LogFile(reader);
            assertEquals(13, logFile.size());
            for (LogEntry entry : logFile.logEntries) {
                assert (entry instanceof LogEntry);
            }
        }
    }

    // Q1
    public static void testLogEntry() {
        System.out.println("Running testLogEntry");
        String logLine = "44776.619 KTB918 310E MAINROAD";
        LogEntry logEntry = new LogEntry(logLine);
        assertEquals(44776.619f, logEntry.getTimestamp(), 0.0001);
        assertEquals("KTB918", logEntry.getLicensePlate());
        assertEquals(310, logEntry.getLocation());
        assertEquals("EAST", logEntry.getDirection());
        assertEquals("MAINROAD", logEntry.getBoothType());
        logLine = "52160.132 ABC123 400W ENTRY";
        logEntry = new LogEntry(logLine);
        assertEquals(52160.132f, logEntry.getTimestamp(), 0.0001);
        assertEquals("ABC123", logEntry.getLicensePlate());
        assertEquals(400, logEntry.getLocation());
        assertEquals("WEST", logEntry.getDirection());
        assertEquals("ENTRY", logEntry.getBoothType());
    }

    // Q2
    public static void testCountJourneys() throws IOException {
        System.out.println("Running testCountJourneys");
        try (BufferedReader reader = new BufferedReader(new
                StringReader(SMALL_LOG))) {
            LogFile logFile = new LogFile(reader);
            assertEquals(3, logFile.countJourneys());
        }
    }

    //    // Q3
    public static void testCatchSpeeders() throws IOException {
        System.out.println("Running testCatchSpeeders");
        try (BufferedReader reader = new BufferedReader(new
                StringReader(SPEEDER_LOG))) {
            LogFile logFile = new LogFile(reader);
            List<String> ticketList = logFile.catchSpeeders();
            // ticketList should be a list similar to
            // ["TST002", "TST003", "TST003"]
            // In this case, TST002 had one journey with unsafe driving, and
            // TST003 had two journeys with unsafe driving. The license plates
            // may be in any order.
            Map<String, Integer> ticketCounts = new HashMap<>();
            for (String ticket : ticketList) {
                ticketCounts.put(ticket, ticketCounts.getOrDefault(ticket, 0)
                        + 1);
            }
            assertEquals(1, (int) ticketCounts.get("TST002"));
            assertEquals(2, (int) ticketCounts.get("TST003"));
            assertEquals(2, ticketCounts.size());
        }
    }
}
