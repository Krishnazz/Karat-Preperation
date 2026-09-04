import java.util.*;

enum AppointmentStatus {
    SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
}

class Doctor {
    public int doctorId;
    public String name;

    public Doctor(int doctorId, String name) {
        this.doctorId = doctorId;
        this.name = name;
    }
}

enum VisitType {
    CONSULTATION, FOLLOWUP, EMERGENCY
}

class PatientVisit {
    int visitId;
    int patientId;
    int doctorId;
    int startTime;
    int durationMinutes;
    VisitType visitType;

    PatientVisit(int visitId, int patientId, int doctorId,
                 int startTime, int durationMinutes, VisitType visitType) {
        this.visitId = visitId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.visitType = visitType;
    }
}

class Appointment {
    public int appointmentId;
    public int doctorId;
    public int patientId;
    public int durationMinutes;
    public AppointmentStatus status;

    public Appointment(int appointmentId, int doctorId, int patientId,
                       int durationMinutes, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }
}

class AppointmentStats {
    public int totalAppointments;
    public int completedAppointments;
    public double noShowRate;

    public AppointmentStats(int totalAppointments, int completedAppointments, double noShowRate) {
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.noShowRate = noShowRate;
    }
}

class ClinicManager {
    public Map<Integer, Doctor> doctors;
    public List<Appointment> appointments;
    public List<PatientVisit>visits;
    
    public ClinicManager() {
        doctors = new HashMap<>();
        appointments = new ArrayList<>();
        visits = new ArrayList<>();
    }

    public void addDoctor(Doctor doctor) {
        doctors.put(doctor.doctorId, doctor);
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public AppointmentStats getAppointmentStatistics() {
        int total = appointments.size();

        int completed = 0;
        for (Appointment a : appointments) {
            if (a.status == AppointmentStatus.COMPLETED ) {
                completed++;
            }
        }

        int noShows = 0;
        for (Appointment a : appointments) {
            if (a.status == AppointmentStatus.NO_SHOW) {
                noShows++;
            }
        }

        double noShowRate;
        if (total > 0) {
            noShowRate = (double) noShows / total;
        } else {
            noShowRate = 0.0;
        }
        System.out.println(total+" "+completed+" "+noShowRate);
        return new AppointmentStats(total, completed, noShowRate);
    }
    
    public void addPatientVisit(PatientVisit patientVisit) {
    	if(doctors.containsKey(patientVisit.doctorId)) {
    		visits.add(patientVisit);
    	}
    	
    }
   
   public Map<VisitType, Double> getAverageVisitDurationByType(int doctorid){
	   Map<VisitType, Double> Result=new HashMap<>();
	  // List<PatientVisit>filterPaitentvisits= visits.stream().filter((i->i.doctorId==doctorid)).collect(Collectors.toList());
	   Map<VisitType,Integer> sum=new HashMap<>();
	   Map<VisitType,Integer> count=new HashMap<>();
	   for(PatientVisit visit:visits) {
		   if(visit.doctorId==doctorid) {
			   VisitType key=visit.visitType;
			   sum.put(key, sum.getOrDefault(key, 0)+visit.durationMinutes);
			   count.put(key, count.getOrDefault(key, 0)+1);
		   }
		   
	   }
	   for(Map.Entry<VisitType, Integer>hmap:sum.entrySet()) {
		  int tot= hmap.getValue();
		  int  n=    count.get(hmap.getKey());
		  Result.put(hmap.getKey(), (double)tot/n);
		  
	   }
//	   for(PatientVisit visit:visits) {
//		   VisitType key=visit.visitType;
//		   double avg=appointments.stream().filter(i->i.doctorId==doctorid).mapToDouble(i->i.durationMinutes).average().orElse(0.0);
//		   Result.put(key, avg);
//	   }
	   System.out.print(Result);
	   return Result;
    	
    }
    
    
}

public class Solution {
    public static void main(String[] args) {
        testGetAppointmentStatistics();
        testAddPatientVisit();
       
        testGetAverageVisitDurationByType();
        System.out.println("All tests pass!");
    }

    public static void testGetAppointmentStatistics() {
        System.out.println("Running testGetAppointmentStatistics");
        ClinicManager cm = new ClinicManager();

        cm.addAppointment(new Appointment(1, 10, 100, 30, AppointmentStatus.COMPLETED));
        cm.addAppointment(new Appointment(2, 10, 101, 45, AppointmentStatus.COMPLETED));
        cm.addAppointment(new Appointment(3, 10, 102, 30, AppointmentStatus.NO_SHOW));
        cm.addAppointment(new Appointment(4, 10, 103, 60, AppointmentStatus.CANCELLED));
        cm.addAppointment(new Appointment(5, 10, 104, 30, AppointmentStatus.SCHEDULED));

        AppointmentStats stats = cm.getAppointmentStatistics();
        assert stats.totalAppointments == 5 :
            "totalAppointments should be 5, was " + stats.totalAppointments;
        assert stats.completedAppointments == 2 :
            "completedAppointments should be 2, was " + stats.completedAppointments;
        assert Math.abs(stats.noShowRate - 0.2) < 1e-4 :
            "noShowRate should be 0.2, was " + stats.noShowRate;
    }

    static void testAddPatientVisit() {
        System.out.println("Running testAddPatientVisit");
        ClinicManager cm = new ClinicManager();
        cm.addDoctor(new Doctor(1, "dr_smith"));
        cm.addDoctor(new Doctor(2, "dr_jones"));
        
        cm.addPatientVisit(new PatientVisit(1, 100, 1, 540, 30, VisitType.CONSULTATION));
        cm.addPatientVisit(new PatientVisit(2, 101, 2, 600, 45, VisitType.FOLLOWUP));
        
       

        // unknown doctor ignored
        cm.addPatientVisit(new PatientVisit(3, 102, 99, 660, 30, VisitType.EMERGENCY));
    }

    static void testGetAverageVisitDurationByType() {
         System.out.println("Running testGetAverageVisitDurationByType");
         ClinicManager cm = new ClinicManager();
         cm.addDoctor(new Doctor(1, "dr_smith"));
         cm.addDoctor(new Doctor(2, "dr_jones"));

         cm.addPatientVisit(new PatientVisit(1, 100, 1, 540, 30, VisitType.CONSULTATION));
         cm.addPatientVisit(new PatientVisit(2, 101, 1, 600, 50, VisitType.CONSULTATION));
         cm.addPatientVisit(new PatientVisit(3, 102, 1, 660, 20, VisitType.FOLLOWUP));
         cm.addPatientVisit(new PatientVisit(4, 103, 2, 540, 40, VisitType.EMERGENCY));

         Map<VisitType, Double> avg1 = cm.getAverageVisitDurationByType(1);
         assert Math.abs(40.0 - avg1.get(VisitType.CONSULTATION)) < 1e-4 : "Expected 40.0 for CONSULTATION";  // (30+50)/2
         assert Math.abs(20.0 - avg1.get(VisitType.FOLLOWUP)) < 1e-4 : "Expected 20.0 for FOLLOWUP";
         assert !avg1.containsKey(VisitType.EMERGENCY) : "EMERGENCY should not be in avg1";

         Map<VisitType, Double> avg2 = cm.getAverageVisitDurationByType(2);
         assert Math.abs(40.0 - avg2.get(VisitType.EMERGENCY)) < 1e-4 : "Expected 40.0 for EMERGENCY";
         assert !avg2.containsKey(VisitType.CONSULTATION) : "CONSULTATION should not be in avg2";
         assert !avg2.containsKey(VisitType.FOLLOWUP) : "FOLLOWUP should not be in avg2";

        // doctor with no visits
         cm.addDoctor(new Doctor(3, "dr_brown"));
         assert cm.getAverageVisitDurationByType(3).isEmpty() : "Expected empty map for doctor with no visits";
    }
}