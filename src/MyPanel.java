import java.awt.Panel;
import java.util.Timer;

public class MyPanel extends Panel
{
	Tetris t;

	public MyPanel ()
	{
		t = new Tetris();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(t, 0, 500);
	}
}