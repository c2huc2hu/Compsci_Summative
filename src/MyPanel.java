import javax.swing.JPanel;
import java.util.Timer;

public class MyPanel extends JPanel
{
	Tetris t;

	public MyPanel ()
	{
		t = new Tetris();
		Timer timer = new Timer();
		timer.schedule(t, 0, 500);
	}
}