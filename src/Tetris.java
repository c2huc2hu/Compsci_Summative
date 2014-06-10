import java.util.TimerTask;

public class Tetris extends TimerTask
{
	Game game;
    private int counter = 0; //Counts every tick
    private int limit = 50; //How big counter can get before we drop the piece.

	public Tetris ()
	{
		System.out.println ("Timer started");
		game = new Game (10, 20);
	}

    public void nextTick ()
    {
        System.out.println ("tick");
        game.update();
        game.repaint();
    }

	@Override
	public void run ()
	{
		counter++;

        if (counter % limit == 0)
        {
            nextTick();
        }

        if (game.speedUpFlag)
        {
            limit = limit * 9 / 10; //reduce delay by 90%.
            game.speedUpFlag = false;
        }
	}
}