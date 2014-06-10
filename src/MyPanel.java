import javax.swing.*;
import java.util.Timer;

public class MyPanel extends JPanel
{
	Tetris t;

	public MyPanel ()
	{
		t = new Tetris();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(t, 0, 10);
	}
}