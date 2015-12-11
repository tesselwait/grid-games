package OrigCon4;

import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.util.ArrayList;
import java.util.Random;

public class ConnectComputer
{
  GameWorld world;
  Random gen = new Random();
  boolean red = true;
  int turn = 0;
  
  public ConnectComputer(GameWorld gameWorld){
    this.world = gameWorld;
  }
  
  public boolean willGameEnd(Location loc, boolean color){
    ArrayList<Integer> streaks = this.world.getStreaks(loc, color);
    for (Integer num : streaks) {
      if (num.intValue() >= 3) {
        return true;
      }
    }
    return false;
  }
  
  public Location getGameEnders(ArrayList<Location> potentialMoves){
    for (Location location : potentialMoves){
      if (willGameEnd(location, !this.red)){
        return location;
      }
    }
    for (Location location : potentialMoves){
      if (willGameEnd(location, this.red)){
        return location;
      }
    }
    for (Location location : potentialMoves){
      if (!willGameEnd(new Location(location.getRow() - 1, location.getCol()), this.red)){
        if (willGameEnd(new Location(location.getRow() - 1, location.getCol()), !this.red)){
          if (willGameEnd(new Location(location.getRow() - 2, location.getCol()), !this.red)){
            return location;
          }
        }
      }
    }
    for (Location location : potentialMoves) {
      if (!willGameEnd(new Location(location.getRow() - 1, location.getCol()), this.red)){
        this.world.superAdd(location, new GamePiece(!this.red, this.world.getGrid(), location));
        ArrayList<Location> potentialMoves2 = new ArrayList();
        for (int i = 0; i < 7; i++){
          if (this.world.getGrid().get(new Location(0, i)) == null){
            potentialMoves2.add(new Location(this.world.findLowestEmptySpace(new Location(0, i)).getRow(), i));
          }
        }
        int winLocs = 0;
        for (Location loc2 : potentialMoves2){
          if (willGameEnd(loc2, !this.red)){
            winLocs++;
            if ((willGameEnd(new Location(loc2.getRow() - 1, loc2.getCol()), !this.red)) || (winLocs >= 2)){
              this.world.remove(location);
              
              return location;
            }
          }
        }
        this.world.remove(location);
      }
    }
    return null;
  }
  
  public ArrayList<Location> findPotentialMoves(ArrayList<Location> firstPotentialMoves){
    ArrayList<Location> secondPotentialMoves = new ArrayList();
    for (int i = 0; i < firstPotentialMoves.size(); i++){
      ArrayList<Integer> nextStreaks = this.world.getStreaks(new Location(((Location)firstPotentialMoves.get(i)).getRow() - 1, ((Location)firstPotentialMoves.get(i)).getCol()), true);
      boolean threeInRow = false;
      for (Integer num : nextStreaks){
        if (num.intValue() >= 3){
          threeInRow = true;
        }
      }
      if (!threeInRow) {
        secondPotentialMoves.add((Location)firstPotentialMoves.get(i));
      }
    }
    return secondPotentialMoves;
  }
  
  public int sumFactorials(ArrayList<Integer> list){
    int streakTotal = 0;
    for (Integer streak : list){
      int streakFactorial = 0;
      for (int i = streak.intValue(); i > 0; i--){
        streakFactorial += i;
      }
      streakTotal += streakFactorial;
    }
    return streakTotal;
  }
  
  public Location findIdealMove(Location loc, ArrayList<Location> potentialMoves){
    Location greatestStreakLoc = (Location)potentialMoves.get(0);
    int greatestStreakTotal = 0;
    for (Location location : potentialMoves){
      int tempStreakTotal = 0;
      ArrayList<Integer> opStreaks = this.world.getStreaks(location, true);
      int opStreakSum = sumFactorials(opStreaks);
      tempStreakTotal += opStreakSum;
      ArrayList<Integer> myStreaks = this.world.getStreaks(location, false);
      int myStreakSum = sumFactorials(myStreaks);
      if (myStreakSum >= 2) {
        tempStreakTotal += myStreakSum;
      }
      int aboveStreakTotal = 0;
      if (location.getRow() > 0){
        ArrayList<Integer> aboveStreaks = this.world.getStreaks(new Location(location.getRow() - 1, location.getCol()), false);
        int aboveStreakFactorialSum = sumFactorials(aboveStreaks);
        
        ArrayList<Integer> aboveOpStreaks = this.world.getStreaks(new Location(location.getRow() - 1, location.getCol()), true);
        int aboveOpStreakFactorialSum = sumFactorials(aboveOpStreaks);
        
        aboveStreakTotal = aboveStreakFactorialSum + aboveOpStreakFactorialSum;
      }
      tempStreakTotal -= (int)(aboveStreakTotal / 1.0/*5D*/);
      if (tempStreakTotal > greatestStreakTotal){
        greatestStreakTotal = tempStreakTotal;
        greatestStreakLoc = location;
      }
    }
    System.out.println();
    return greatestStreakLoc;
  }
  
  public Location makeMove(Location loc){
    ArrayList<Location> potentialMoves = new ArrayList();
    for (int i = 0; i < 7; i++){
      if (this.world.getGrid().get(new Location(0, i)) == null){
        potentialMoves.add(new Location(this.world.findLowestEmptySpace(new Location(0, i)).getRow(), i));
      }
    }
    Location finishingMove = getGameEnders(potentialMoves);
    if (finishingMove != null){
      return finishingMove;
    }
    ArrayList<Location> secondPotentialMoves = findPotentialMoves(potentialMoves);
    if (secondPotentialMoves.size() == 0){
      return (Location)potentialMoves.get(0);
    }
    Location idealMove = findIdealMove(loc, secondPotentialMoves);
    if (secondPotentialMoves.size() <= 0){
      return this.world.findLowestEmptySpace(new Location(0, this.gen.nextInt(7)));
    }
    return idealMove;
  }
}
