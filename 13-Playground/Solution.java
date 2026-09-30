import java.util.*;
import java.util.stream.Collectors;


enum Outcome {
   WIN, LOSS, DRAW
}

class Player {
   public int playerId;
   public String username;

   public Player(int playerId, String username) {
       this.playerId = playerId;
       this.username = username;
   }
}

class HeadToHead {
	int winsPlayer1;
	int winsPlayer2;
	int draws;
	int totalMatches;
	Outcome lastResult;
	Integer lastMatchTimestamp;

	HeadToHead(int winsPlayer1, int winsPlayer2, int draws, int totalMatches, Outcome lastResult,
			Integer lastMatchTimestamp) {
		this.winsPlayer1 = winsPlayer1;
		this.winsPlayer2 = winsPlayer2;
		this.draws = draws;
		this.totalMatches = totalMatches;
		this.lastResult = lastResult;
		this.lastMatchTimestamp = lastMatchTimestamp;
	}
}

class MatchResult {
   public int playerId;
   public int opponentId;
   public Outcome outcome;
   public int score;
   public int timestamp;

   public MatchResult(int playerId, int opponentId, Outcome outcome, int score, int timestamp) {
       this.playerId = playerId;
       this.opponentId = opponentId;
       this.outcome = outcome;
       this.score = score;
       this.timestamp = timestamp;
   }
}

class PlayerStats {
   public int totalMatches;
   public int wins;
   public double winRate;

   public PlayerStats(int totalMatches, int wins, double winRate) {
       this.totalMatches = totalMatches;
       this.wins = wins;
       this.winRate = winRate;
   }
}

class GameManager {
   public Map<Integer, Player> players;
   public List<MatchResult> matchResults;

   public GameManager() {
       players = new HashMap<>();
       matchResults = new ArrayList<>();
   }

   public void addPlayer(Player player) {
       players.put(player.playerId, player);
   }

   public PlayerStats getPlayerStatistics(int playerId) {
       List<MatchResult> playerMatches = new ArrayList<>();
       for (MatchResult m : matchResults) {
           if (m.playerId == playerId) {
               playerMatches.add(m);
           }
       }

       int totalMatches = playerMatches.size();
      
       int wins = 0;
       for (MatchResult m : playerMatches) {
           if (m.outcome == Outcome.WIN) {
               wins++;
           }
       }

       double winRate;
       if (totalMatches > 0) {
           winRate = (double) wins / totalMatches;
       } else {
           winRate = 0.0;
       }

       return new PlayerStats(totalMatches, wins, winRate);
   }
   void addMatchResult(MatchResult matchresult){
	   if(players.containsKey(matchresult.playerId)) {
		   matchResults.add(matchresult);
	   }
   }
   Map<Outcome,Double> getAverageScoreByOutcome(int playerid){
	   Map<Outcome,Double> result=new HashMap<>();
	   
	  result= matchResults.stream().filter(i->i.playerId==playerid).collect(Collectors.groupingBy(i->i.outcome,Collectors.averagingDouble(i->i.score)));
	   
	   System.out.println(result);
	   return result;
   }
   HeadToHead getHeadToHead(int playerid,int opponentid){
	   int win1count=0,win2count=0;
	   int drawcount=0,totalCount=0;
	   Outcome latestOutcome=null;
	   Integer timestamp=0;
	   matchResults.sort(Comparator.comparingInt(i->i.timestamp));
	   for(MatchResult matchresult:matchResults) {
		   if(matchresult.playerId==playerid && matchresult.opponentId==opponentid) {
			   
		   
		   if(matchresult.outcome==Outcome.WIN) {
			   win1count+=1;
		   }
		   else if(matchresult.outcome==Outcome.LOSS) {
			   win2count+=1;
		   }
		   else {
			   drawcount+=1;
		   }
		   totalCount+=1;
		   latestOutcome =matchresult.outcome;
		   timestamp=matchresult.timestamp;
		   }
	   }
	   System.out.println(win1count+" "+win2count+" "+drawcount+" "+totalCount+" "+latestOutcome+" "+timestamp);
	   return new HeadToHead(win1count,win2count,drawcount,totalCount,latestOutcome,timestamp);
	   
	   
   }

	
   List<List<Integer>> getRecentForm(int n){
	   List<List<Integer>> result=new ArrayList<>();
	   
	  for(Map.Entry<Integer, Player> player:players.entrySet()) {
		  
		 int matchcount=(int)matchResults.stream().filter(i->i.playerId==player.getKey()).count();
		 if(matchcount<n) {
			 continue;
		 }
		 
		List<MatchResult> match=matchResults.stream().filter(i->i.playerId==player.getKey()).sorted(Comparator.comparing((MatchResult i)->i.timestamp).reversed().thenComparing(i->i.playerId)).limit(n).toList();
	    int points=0;  
	    List<Integer> r=new ArrayList<>();
		for(MatchResult m:match) {
	    	  if(m.outcome==Outcome.WIN) {
	    		  points+=3;
	    	  }
	    	  else if(m.outcome==Outcome.DRAW) {
	    		  points+=1;
	    	  }
	    	  
	      }
		r.add(player.getKey());
		r.add(points);
		
	  result.add(r);
	  }
	   
	   
	   System.out.println(result);
	   return result;
	   
   }
  
}


public class Solution {
	public static void main(String[] args) {
		testGetPlayerStatistics();
		testAddMatchResult();
		testGetAverageScoreByOutcome();
		testGetHeadToHead();
		testGetRecentForm_case1();
		testGetRecentForm_case2();
		System.out.println("All Tests Passed!");
	}

	public static void testGetPlayerStatistics() {
		System.out.println("Running testGetPlayerStatistics");
		GameManager gm = new GameManager();
		gm.addPlayer(new Player(1, "player1"));
		gm.addPlayer(new Player(2, "player2"));

		gm.matchResults.add(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
		gm.matchResults.add(new MatchResult(1, 2, Outcome.LOSS, 50, 2000));
		gm.matchResults.add(new MatchResult(1, 2, Outcome.DRAW, 60, 3000));
		gm.matchResults.add(new MatchResult(1, 2, Outcome.WIN, 90, 4000));

		PlayerStats stats = gm.getPlayerStatistics(1);
		assert stats.totalMatches == 4 : "totalMatches should be 4, was " + stats.totalMatches;
		assert stats.wins == 2 : "wins should be 2, was " + stats.wins;
		assert Math.abs(stats.winRate - 0.5) < 1e-4 : "winRate should be 0.5, was " + stats.winRate;

		gm.matchResults.add(new MatchResult(2, 1, Outcome.DRAW, 60, 1000));
		gm.matchResults.add(new MatchResult(2, 1, Outcome.DRAW, 60, 2000));

		PlayerStats stats2 = gm.getPlayerStatistics(2);
		assert stats2.totalMatches == 2 : "totalMatches should be 2, was " + stats2.totalMatches;
		assert stats2.wins == 0 : "wins should be 0, was " + stats2.wins;
		assert Math.abs(stats2.winRate - 0.0) < 1e-4 : "winRate should be 0.0, was " + stats2.winRate;
	}

	static void testAddMatchResult() {
		System.out.println("Running testAddMatchResult");
		GameManager gm = new GameManager();
		gm.addPlayer(new Player(1, "player1"));
		gm.addPlayer(new Player(2, "player2"));

		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 50, 1000));

		// unknown player ignored
		gm.addMatchResult(new MatchResult(99, 1, Outcome.WIN, 100, 2000));

		assert gm.matchResults.size() == 2 : "Expected 2 match results";
		
	}

	static void testGetAverageScoreByOutcome() {
		System.out.println("Running testGetAverageScoreByOutcome");
		GameManager gm = new GameManager();
		gm.addPlayer(new Player(1, "player1"));
		gm.addPlayer(new Player(2, "player2"));

		// match 1 - player1 wins
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 50, 1000));

		// match 2 - player1 wins again
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 90, 2000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 60, 2000));

		// match 3 - draw
		gm.addMatchResult(new MatchResult(1, 2, Outcome.DRAW, 70, 3000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.DRAW, 70, 3000));

		Map<Outcome, Double> avg1 = gm.getAverageScoreByOutcome(1);

		assert Math.abs(85.0 - avg1.get(Outcome.WIN)) < 1e-4 : "Expected 85.0 for WIN"; // (80+90)/2
		assert Math.abs(70.0 - avg1.get(Outcome.DRAW)) < 1e-4 : "Expected 70.0 for DRAW";
		assert !avg1.containsKey(Outcome.LOSS) : "player1 has no losses"; // player1 has no losses

		Map<Outcome, Double> avg2 = gm.getAverageScoreByOutcome(2);
		assert Math.abs(55.0 - avg2.get(Outcome.LOSS)) < 1e-4 : "Expected 55.0 for LOSS"; // (50+60)/2
		assert Math.abs(70.0 - avg2.get(Outcome.DRAW)) < 1e-4 : "Expected 70.0 for DRAW";
		assert !avg2.containsKey(Outcome.WIN) : "player2 has no wins"; // player2 has no wins

		// player with no match results
		gm.addPlayer(new Player(3, "player3"));
		assert gm.getAverageScoreByOutcome(3).isEmpty() : "Expected empty map for player3";
	}

	static void testGetHeadToHead() {
		System.out.println("Running testGetHeadToHead");
		GameManager gm = new GameManager();
		gm.addPlayer(new Player(1, "player1"));
		gm.addPlayer(new Player(2, "player2"));
		gm.addPlayer(new Player(3, "player3"));

		// match 1 - player1 wins
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 50, 1000));

		// match 2 - player2 wins
		gm.addMatchResult(new MatchResult(1, 2, Outcome.LOSS, 60, 2000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.WIN, 90, 2000));

		// match 3 - draw
		gm.addMatchResult(new MatchResult(1, 2, Outcome.DRAW, 70, 3000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.DRAW, 70, 3000));

		// match 4 - player1 wins (most recent)
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 85, 4000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 65, 4000));

		HeadToHead h2h = gm.getHeadToHead(1, 2);
		assert h2h.winsPlayer1 == 2 : "Expected 2"; // match 1 + match 4
		assert h2h.winsPlayer2 == 1 : "Expected 1"; // match 2 only
		assert h2h.draws == 1 : "Expected 1"; // match 3 only
		assert h2h.totalMatches == 4 : "Expected 4"; // all 4 matches
		assert h2h.lastResult == Outcome.WIN : "Expected WIN"; // match 4 was a WIN for player1
		assert h2h.lastMatchTimestamp == 4000 : "Expected 4000";

		// from player2's perspective
		HeadToHead h2hReverse = gm.getHeadToHead(2, 1);
		assert h2hReverse.winsPlayer1 == 1 : "Expected 1"; // player2 won match 2
		assert h2hReverse.winsPlayer2 == 2 : "Expected 2"; // player1 won match 1 + 4
		assert h2hReverse.draws == 1 : "Expected 1";
		assert h2hReverse.totalMatches == 4 : "Expected 4";
		assert h2hReverse.lastResult == Outcome.LOSS : "Expected LOSS"; // match 4 was a LOSS for player2
		assert h2hReverse.lastMatchTimestamp == 4000 : "Expected 4000";

		// players who have never faced each other — unchanged
		HeadToHead h2hEmpty = gm.getHeadToHead(1, 3);
		assert h2hEmpty.totalMatches == 0 : "Expected 0";
		assert h2hEmpty.winsPlayer1 == 0 : "Expected 0";
		assert h2hEmpty.winsPlayer2 == 0 : "Expected 0";
		assert h2hEmpty.draws == 0 : "Expected 0";
		assert h2hEmpty.lastResult == null : "Expected null";
		assert h2hEmpty.lastMatchTimestamp == null : "Expected null";
	}

	static void testGetRecentForm_case1() {
		System.out.println("Running testGetRecentForm_case1");
		GameManager gm = new GameManager();
		for (int pid : new int[] { 1, 2, 3, 4 }) {
			gm.addPlayer(new Player(pid, "player" + pid));
		}

		// player 1: W W W -> 9 points
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 2000));
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 3000));

		// player 2: W D L -> 4 points
		gm.addMatchResult(new MatchResult(2, 1, Outcome.WIN, 80, 1000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.DRAW, 80, 2000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 80, 3000));

		// player 3: 4 matches, last 3 = L L W -> 3 points
		// older DRAW at timestamp 500 is outside the window and ignored
		gm.addMatchResult(new MatchResult(3, 1, Outcome.LOSS, 80, 1000));
		gm.addMatchResult(new MatchResult(3, 1, Outcome.WIN, 80, 3000));
		gm.addMatchResult(new MatchResult(3, 1, Outcome.DRAW, 80, 500));
		gm.addMatchResult(new MatchResult(3, 1, Outcome.LOSS, 80, 2000));

		// player 4: only 2 matches, n=3 -> excluded
		gm.addMatchResult(new MatchResult(4, 1, Outcome.WIN, 80, 1000));
		gm.addMatchResult(new MatchResult(4, 1, Outcome.WIN, 80, 2000));

		List<List<Integer>> result = gm.getRecentForm(3);
		assert result.equals(Arrays.asList(Arrays.asList(1, 9), Arrays.asList(2, 4), Arrays.asList(3, 3)))
				: "Expected [[1, 9], [2, 4], [3, 3]]";

		// n=0 -> empty list
		assert gm.getRecentForm(0).isEmpty() : "Expected empty list for n=0";
	}

	static void testGetRecentForm_case2() {
		System.out.println("Running testGetRecentForm_case2");
		GameManager gm = new GameManager();
		for (int pid : new int[] { 2, 1, 3, 4 }) {
			gm.addPlayer(new Player(pid, "player" + pid));
		}

		// player 1: 4 matches, last 3 = W W W -> 9 points
		// older WIN at timestamp 500 is outside the window and ignored
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 500));
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 2000));
		gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 3000));

		// player 2: exactly 3 matches, W W W -> 9 points
		// tied with player 1 on points, tiebreak by playerId -> player 1 ranks higher
		gm.addMatchResult(new MatchResult(2, 1, Outcome.WIN, 80, 1000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.WIN, 80, 2000));
		gm.addMatchResult(new MatchResult(2, 1, Outcome.WIN, 80, 3000));

		// player 3: only 2 matches, n=3 -> excluded
		gm.addMatchResult(new MatchResult(3, 1, Outcome.WIN, 80, 1000));
		gm.addMatchResult(new MatchResult(3, 1, Outcome.WIN, 80, 2000));

		// player 4: exactly 3 matches, L L L -> 0 points, included
		gm.addMatchResult(new MatchResult(4, 1, Outcome.LOSS, 80, 1000));
		gm.addMatchResult(new MatchResult(4, 1, Outcome.LOSS, 80, 2000));
		gm.addMatchResult(new MatchResult(4, 1, Outcome.LOSS, 80, 3000));

		List<List<Integer>> result = gm.getRecentForm(3);
		// player 3 excluded - only 2 matches
		// player 1 and 2 tied on 9 points, player 1 wins tiebreak (lower id)
		// player 4 included with 0 points
		assert result.equals(Arrays.asList(Arrays.asList(1, 9), Arrays.asList(2, 9), Arrays.asList(4, 0)))
				: "Expected [[1, 9], [2, 9], [4, 0]]";
	}
}