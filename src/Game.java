import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Game extends Panel implements KeyListener
{
	private final int BLOCK_SIZE = 32;

	private final Color [] colours = new Color [] { new Color (0x888888), new Color (0xFFFF00), new Color (0x00FFFF)};

	public int [] [] field; //the top of the field is 0.
	public int [] [] curBlock = new int [4] [4];

	public int width, height;

	private int [] [] [] tetraminos = new int [][][] {
		{{0, 0, 0, 0},
		 {0, 1, 0, 0},
		 {0, 1, 0, 0},
		 {0, 1, 1, 0}},
		{{0, 0, 0, 0},
		 {0, 0, 1, 0},
		 {0, 0, 1, 0},
		 {0, 1, 1, 0}},
		{{0, 0, 0, 0},
		 {0, 0, 0, 0},
		 {1, 1, 1, 1},
		 {0, 0, 0, 0}},
		{{0, 0, 0, 0},
		 {0, 0, 1, 0},
		 {0, 1, 1, 0},
		 {0, 1, 0, 0}},
		{{0, 0, 0, 0},
		 {0, 1, 0, 0},
		 {0, 1, 1, 0},
		 {0, 0, 1, 0}},
		{{0, 0, 0, 0},
		 {0, 1, 1, 0},
		 {0, 1, 1, 0},
		 {0, 0, 0, 0}},
		{{0, 0, 0, 0},
		 {0, 0, 0, 0},
		 {0, 0, 1, 0},
		 {0, 1, 1, 1}}		};


	public int tetraX, tetraY;  //coordinates of the top-left corner of the tetramino.

	public Game (int width, int height)
	{
		printArray (tetraminos [0]);
		printArray (rotate (tetraminos [0]));

		this.width = width;
		this.height = height;
		this.field = new int [width] [height];

		this.spawnNewBlock();
	}

	/**
	What happens when the tetramino goes down one square.
     @return whether I need to spawn another block
	*/
	private boolean softDrop ()
	{
		tetraY += 1;
		if (collision())
        {
            tetraY -= 1;
            return true;
        }
        return false;
	}

    /**
     * What happens when the user presses the up key to rotate the block.
     */
    private void rotateCurBlock ()
    {
        int [] [] copy = curBlock.clone();
        curBlock = rotate(curBlock);
        if (collision())
        {
            curBlock = copy;
        }
    }

    /**
     * Shifts curBlock over one.
     * @param direction +1 -> right, -1 -> left.
     */
    private void translateCurBlock (int direction)
    {
        tetraX += direction;
        if (collision())
        {
            tetraX -= direction;
        }
    }

	public void update ()
	{
		if (this.softDrop())
		{
			this.spawnNewBlock();
		}
	}

	/**
	Add the current block the the field, and spawn a random block at the top.
     Check for loss: is there a block in the top 4 rows.
	*/
	public void spawnNewBlock ()
	{
        //check for loss
        for (int i = 0; i < this.width; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (field [i] [j] != 0)
                {
                    System.out.println("You have lost");
                    System.exit (100);
                }
            }
        }

		//copy current block to field.
		for (int i = 0; i < curBlock.length; i++)
		{
			for (int j = 0; j < curBlock [i].length; j++)
			{
				if (curBlock [i] [j] != 0)
				{
					field [i + tetraX] [j + tetraY] = curBlock [i] [j];
				}
			}
		}

		//spawn a new block.
		curBlock = tetraminos [(int) (Math.random() * 7)].clone();
		tetraX = width / 2 - 2;
		tetraY = 0;
	}

    /**
     * Check whether current block is in a valid position, i.e. not off the screen or 'inside' another block.
     * @return whether there is a collision. (return !(block is in valid position)
     */
    private boolean collision ()
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (curBlock[i][j] != 0)
                {
                    if (0 > i + tetraX || i + tetraX >= this.width || 0 > j + tetraY || j + tetraY >= this.height)
                    {
                        return true;
                    }
                    if (field [tetraX + i] [tetraY + j] != 0)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

	/**
	Paints the tetris blocks on the field.
	*/
	@Override
	public void paint (Graphics g)
	{
		for (int i = 0; i < this.width; i++)
		{
			for (int j = 0; j < this.height; j++)
			{
				g.setColor (colours [field [i] [j]]);
				g.fillRect (i * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
				g.setColor (Color.BLACK);
				g.drawRect (i * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			}
		}

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
                if (curBlock [i] [j] != 0)
                {
                    g.setColor (colours [curBlock [i] [j]]);
                    g.fillRect ((i + tetraX) * BLOCK_SIZE, (j + tetraY) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor (Color.BLACK);
                    g.drawRect((i + tetraX) * BLOCK_SIZE, (j + tetraY) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
			}
		}
	}

	/**
	Rotates a tetramino 90 degrees clockwise.
	*/
	private static int [] [] rotate (int [] [] a)
	{
		//sort of use the rotation matrix [[0, 1], [-1, 0]]
		int [] [] result = new int [a.length] [a[0].length];
		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[0].length; j++)
			{
				result [i] [j] = a[a.length - j - 1] [i];
			}
		}
		return result;
	}

	private static void printArray (int [] [] a)
	{
		System.out.println ("===================");
		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[0].length; j++)
			{
				System.out.print ("" + a[i] [j] + ", ");
			}
			System.out.println();
		}
	}

    @Override
    public void keyTyped(KeyEvent e)
    {
        //pass.
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                rotateCurBlock();
                break;
            case KeyEvent.VK_DOWN:
                softDrop();
                break;
            case KeyEvent.VK_LEFT:
                translateCurBlock(-1);
                break;
            case KeyEvent.VK_RIGHT:
                translateCurBlock(1);
                break;
            case KeyEvent.VK_SPACE:
                while (!softDrop());
                break;
        }
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        //do nothing.
    }
}