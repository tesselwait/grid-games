package OrigCon4;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;

public class GamePiece
  extends Actor
{
  private boolean isRed;
  
  public GamePiece(boolean red, Grid<Actor> grid, Location loc)
  {
    putSelfInGrid(grid, loc);
    setDirection(0);
    if (red)
    {
      setColor(Color.RED);
      this.isRed = true;
    }
    else
    {
      setColor(Color.BLACK);
      this.isRed = false;
    }
  }
  
  public boolean getPieceColor()
  {
    return this.isRed;
  }
}
