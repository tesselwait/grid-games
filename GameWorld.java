package OrigCon4;


import info.gridworld.actor.Actor;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import info.gridworld.world.World;
import java.awt.Color;
import java.util.ArrayList;

public class GameWorld
  extends World<Actor>
{
  private BoundedGrid<Actor> gr = new BoundedGrid(6, 7);
  private static final int DEFAULT_ROWS = 6;
  private static final int DEFAULT_COLS = 7;
  private int redWins = 0;
  private int blackWins = 0;
  private String defaultMessage = "Click an empty space in the column you wish to move in.";
  private String tieMessage = "Tie Game.";
  private String winMessage = " Wins!";
  private boolean red = true;
  private boolean winner = false;
  private boolean singlePlayer;
  private boolean firstClick = true;
  private ConnectComputer computer = new ConnectComputer(this);
  private int totalH = 0;
  private int totalDU = 0;
  private int totalDD = 0;
  private int totalV = 0;
  
  public GameWorld()
  {
    setGrid(this.gr);
    setMessage("Welcome to Connect Four! Click red for single player and black for two player.");
    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 6; j++) {
        if (((j == 1) && ((i == 2) || (i == 4))) || ((j == 3) && ((i == 1) || (i == 5))) || ((j == 4) && (1 < i) && (i < 5))) {
          add(new Location(j, i), new GamePiece(false, this.gr, new Location(j, i)));
        } else {
          add(new Location(j, i), new GamePiece(true, this.gr, new Location(j, i)));
        }
      }
    }
  }
  
  public BoundedGrid<Actor> getMyGrid()
  {
    return this.gr;
  }
  
  public void add(Location location, GamePiece occupant)
  {
    super.add(location, occupant);
    if (!this.singlePlayer) {
      this.red = (!this.red);
    }
  }
  
  public void superAdd(Location location, GamePiece occupant)
  {
    super.add(location, occupant);
  }
  
  public Location findLowestEmptySpace(Location loc)
  {
    int column = loc.getCol();
    Location temp = null;
    for (int i = 5; i >= 0; i--)
    {
      temp = new Location(i, column);
      if (getGrid().get(temp) == null) {
        return temp;
      }
    }
    return temp;
  }
  
  public boolean moreRedPieces()
  {
    int blackPieces = 0;
    int redPieces = 0;
    for (Location loc : this.gr.getOccupiedLocations()) {
      if (((Actor)this.gr.get(loc)).getColor() == Color.red) {
        redPieces++;
      } else {
        blackPieces++;
      }
    }
    if (redPieces > blackPieces) {
      return true;
    }
    return false;
  }
  
  public ArrayList<Integer> getStreaks(Location loc, boolean red)
  {
    ArrayList<Integer> streaks = new ArrayList();
    this.totalDU = 0;
    this.totalH = 0;
    this.totalDD = 0;
    this.totalV = 0;
    int tempDirection = 45;
    boolean redPiece = red;
    for (int i = 0; i < 7; i++)
    {
      Location tempLoc = loc.getAdjacentLocation(tempDirection);
      int accumulatedStreak = 0;
      if ((this.gr.isValid(tempLoc)) && (this.gr.get(tempLoc) != null) && (((GamePiece)this.gr.get(tempLoc)).getPieceColor() == redPiece))
      {
        accumulatedStreak++;
        tempLoc = tempLoc.getAdjacentLocation(tempDirection);
        if ((this.gr.isValid(tempLoc)) && (this.gr.get(tempLoc) != null) && (((GamePiece)this.gr.get(tempLoc)).getPieceColor() == redPiece))
        {
          accumulatedStreak++;
          tempLoc = tempLoc.getAdjacentLocation(tempDirection);
          if ((this.gr.isValid(tempLoc)) && (this.gr.get(tempLoc) != null) && (((GamePiece)this.gr.get(tempLoc)).getPieceColor() == redPiece)) {
            accumulatedStreak++;
          }
        }
        switch (i)
        {
        case 0: 
          this.totalDU += accumulatedStreak;
          break;
        case 1: 
          this.totalH += accumulatedStreak;
          break;
        case 2: 
          this.totalDD += accumulatedStreak;
          break;
        case 3: 
          this.totalV += accumulatedStreak;
          break;
        case 4: 
          this.totalDU += accumulatedStreak;
          break;
        case 5: 
          this.totalH += accumulatedStreak;
          break;
        case 6: 
          this.totalDD += accumulatedStreak;
        }
      }
      tempDirection += 45;
    }
    streaks.add(Integer.valueOf(this.totalDU));
    streaks.add(Integer.valueOf(this.totalH));
    streaks.add(Integer.valueOf(this.totalDD));
    streaks.add(Integer.valueOf(this.totalV));
    return streaks;
  }
  
  public boolean checkWin(Location loc)
  {
    ArrayList<Integer> streaks = getStreaks(loc, ((GamePiece)this.gr.get(loc)).getPieceColor());
    for (Integer num : streaks) {
      if (num.intValue() >= 3)
      {
        String color;
        if (((GamePiece)this.gr.get(loc)).getPieceColor())
        {
          this.redWins += 1;
          color = "Red";
        }
        else
        {
          this.blackWins += 1;
          color = "Black";
        }
        setMessage(color + this.winMessage + " Red: " + this.redWins + " Black: " + this.blackWins + " Click again for a new game");
        return true;
      }
    }
    return false;
  }
  
  public void singlePlayerRound(Location loc)
  {
    Location loco = findLowestEmptySpace(loc);
    if ((getGrid().get(loc) == null) && (getGrid().get(loco) == null) && (!moreRedPieces()))
    {
      add(loco, new GamePiece(this.red, this.gr, loco));
      this.winner = checkWin(loco);
    }
    if ((!this.winner) && (moreRedPieces()))
    {
      Location compMove = this.computer.makeMove(loc);
      if (getGrid().get(compMove) == null)
      {
        add(compMove, new GamePiece(false, this.gr, compMove));
        this.winner = checkWin(compMove);
      }
    }
  }
  
  public void twoPlayerRound(Location loc)
  {
    Location loco = findLowestEmptySpace(loc);
    if ((getGrid().get(loc) == null) && (getGrid().get(loco) == null))
    {
      add(loco, new GamePiece(this.red, this.gr, loco));
      this.winner = checkWin(loco);
    }
  }
  
  public boolean locationClicked(Location loc)
  {
    if (this.firstClick)
    {
      setMessage(this.defaultMessage);
      if (((Actor)this.gr.get(loc)).getColor() == Color.RED) {
        this.singlePlayer = true;
      } else {
        this.singlePlayer = false;
      }
      this.firstClick = false;
      for (Location location : this.gr.getOccupiedLocations()) {
        this.gr.remove(location);
      }
    }
    else if (!this.winner)
    {
      if (this.singlePlayer) {
        singlePlayerRound(loc);
      } else {
        twoPlayerRound(loc);
      }
      if (this.gr.getOccupiedLocations().size() == 42)
      {
        setMessage(this.tieMessage + " Red: " + this.redWins + " Black: " + this.blackWins + " Click again for a new game");
        this.winner = true;
      }
    }
    else
    {
      ArrayList<Location> pieces = this.gr.getOccupiedLocations();
      for (Location location : pieces) {
        this.gr.remove(location);
      }
      this.winner = false;
      this.red = true;
      setMessage(this.defaultMessage);
    }
    return true;
  }
}
