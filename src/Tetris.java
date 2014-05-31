import java.util.*;
import java.awt.*;

public class Tetris extends TimerTask
{
	Game game;

	public Tetris ()
	{
		System.out.println ("Timer started");
		game = new Game (10, 14);
	}

	@Override
	public void run ()
	{
		System.out.println ("tick");
		game.update();
		game.repaint();
	}
}