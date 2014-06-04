import javax.swing.*;

public class ProgramWindow extends JFrame
{
	private MyPanel panel = new MyPanel();

	public ProgramWindow ()
	{
		this.setVisible (true);
		this.setSize (600, 800);
		this.add (panel.t.game);
        this.addKeyListener(panel.t.game);
	}
}