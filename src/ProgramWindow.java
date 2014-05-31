import java.awt.Frame;

public class ProgramWindow extends Frame
{
	private MyPanel panel = new MyPanel();

	public ProgramWindow ()
	{
		this.setVisible (true);
		this.setSize (400, 800);
		this.add (panel.t.game);
	}
}