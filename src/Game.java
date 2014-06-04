import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;


public class Game extends JPanel implements KeyListener
{
	private static final int BLOCK_SIZE = 25;

	private static final Color [] colours = new Color [] { new Color (0x888888), new Color (0xFFA500), new Color (0x0000FF), new Color (0x00FFFF), new Color (0xFF0000), new Color (0x00FF00), new Color (0xFFFF00), new Color(0x8B008B)};

	private int [] [] field; //the top of the field is 0.
	private int [] [] curBlock = new int [1][1];
    private int [] [] nextBlock;
    private int [] [] heldBlock = new int [1][1];
    private boolean hasHeld = false; //whether the player has already used hold on this block.

    private LinkedList<Integer> blockQueue = new LinkedList<Integer> ();

	private final int fieldWidth;
    private final int fieldHeight;

    private Image offscreen = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

	private final int [] [] [] tetraminos = new int [][][] {
		{{1, 0},
		 {1, 0},
		 {1, 1}},
		{{2, 2},
		 {2, 0},
		 {2, 0}},
		{{0, 3, 0},
         {0, 3, 0},
         {0, 3, 0},
         {0, 3, 0}},
		{{0, 4},
		 {4, 4},
		 {4, 0}},
		{{5, 0},
		 {5, 5},
		 {0, 5}},
		{{6, 6},
         {6, 6}},
		{{7, 0},
		 {7, 7},
         {7, 0}}	};


	private int tetraX, tetraY;  //coordinates of the top-left corner of the tetramino.

	public Game (int width, int height)
	{
		printArray (tetraminos [0]);
		printArray (rotate (tetraminos [0]));

		this.fieldWidth = width;
		this.fieldHeight = height;
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

    public void holdBlock ()
    {
        if (hasHeld)
        {
            return;
        }
        if (this.heldBlock.length == 1 && this.heldBlock[0].length == 1) //if it has its initial value
        {
            this.heldBlock = this.curBlock;
            this.curBlock = new int[1][1];
            this.spawnNewBlock();
        }
        else
        {
            int [] [] temp = this.curBlock;
            this.curBlock = this.heldBlock;
            this.heldBlock = temp;
        }
        this.tetraX = this.fieldWidth / 2;
        this.tetraY = 0;
        this.hasHeld = true;
    }


	/**
	Add the current block the the field, and spawn a random block at the top.
     Check for loss: is there a block in the top 4 rows.
	*/
    void spawnNewBlock()
	{
        //check for loss
        if (collision())
        {
            System.out.println("You have lost");
            System.exit (100);
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

        //check for line filled.
        boolean filled;
        for (int j = this.fieldHeight - 1; j > 0; j--)
        {
            filled = true;
            for (int i = 0; i < this.fieldWidth; i++)
            {
                if (field [i][j] == 0)
                {
                    filled = false;
                    break;
                }
            }

            if (filled)
            {
                //clear line j.
                for (int i = 0; i < this.fieldWidth; i++)
                {
                    for (int k = j; k > 0; k--)
                    {
                        field [i][k] = field [i][k - 1];
                    }
                }
                j++;
            }
        }

		//spawn a new block.
        if (blockQueue.size() <= 1)
        {
            for (int i = 0; i < 7; i++)
            {
                blockQueue.add (i);
            }
            curBlock = tetraminos [blockQueue.remove()].clone();
            Collections.shuffle(blockQueue);
        }
        else
        {
		    curBlock = tetraminos [blockQueue.remove()].clone();
        }
		tetraX = fieldWidth / 2 - 2;
		tetraY = 0;

        nextBlock = tetraminos [blockQueue.element()];

        this.hasHeld = false;
    }

    /**
     * Check whether current block is in a valid position, i.e. not off the screen or 'inside' another block.
     * @return whether there is a collision. (return !(block is in valid position)
     */
    private boolean collision ()
    {
        for (int i = 0; i < curBlock.length; i++)
        {
            for (int j = 0; j < curBlock[0].length; j++)
            {
                if (curBlock[i][j] != 0)
                {
                    if (0 > i + tetraX || i + tetraX >= this.fieldWidth || 0 > j + tetraY || j + tetraY >= this.fieldHeight)
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

        if (this.getSize().width != this.offscreen.getWidth(null) || this.getSize().getHeight() != this.offscreen.getHeight(null))
        {
            this.offscreen = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        
        //draw field etc.
		this.paintBuffer(offscreen.getGraphics());
        g.drawImage(offscreen,0,0,null);       
	}

    public void paintBuffer (Graphics g)
    {
        //draw grid
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, this.fieldWidth * BLOCK_SIZE, this.fieldHeight * BLOCK_SIZE);

        g.setColor(Color.BLACK);
        for (int i = 0; i < this.fieldWidth; i++)
        {
            for (int j = 0; j < this.fieldHeight; j++)
            {
                g.drawLine (i * BLOCK_SIZE, 0, i * BLOCK_SIZE, this.fieldHeight * BLOCK_SIZE);
                g.drawLine (0, j * BLOCK_SIZE, this.fieldWidth * BLOCK_SIZE, j * BLOCK_SIZE);
            }
        }

        //paint field
        drawBlock(g, field, 0, 0);

        //paint current block
        for (int i = 0; i < curBlock.length; i++)
        {
            for (int j = 0; j < curBlock[0].length; j++)
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

        //draw next block
        int offsetX = (this.fieldWidth + 1) * BLOCK_SIZE; //coordinates of where to draw the next block.
        int offsetY = BLOCK_SIZE;

        g.setColor(Color.WHITE);
        g.drawString("Next block", offsetX, offsetY - 10);
        drawBlock(g, nextBlock, offsetX, offsetY);

        offsetY += BLOCK_SIZE * 5;
        g.setColor(Color.WHITE);
        g.drawString("Held block", offsetX, offsetY - 10);
        drawBlock(g, heldBlock, offsetX, offsetY);
    }

    /**
     * For drawing a group of blocks. The top left corner is at offsetX, offsetY.
     * @param g
     * @param block Either the block or the field.
     * @param offsetX
     * @param offsetY
     */
    private void drawBlock (Graphics g, int [] [] block, int offsetX, int offsetY)
    {
        for (int i = 0; i < block.length; i++)
        {
            for (int j = 0; j < block[i].length; j++)
            {
                if (block [i] [j] != 0)
                {
                    g.setColor(colours [block [i] [j]]);
                    g.fillRect(i * BLOCK_SIZE + offsetX, j * BLOCK_SIZE + offsetY, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(i * BLOCK_SIZE + offsetX, j * BLOCK_SIZE + offsetY, BLOCK_SIZE, BLOCK_SIZE);
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
		int [] [] result = new int [a[0].length] [a.length];
		for (int i = 0; i < a[0].length; i++)
		{
			for (int j = 0; j < a.length; j++)
			{
				result [i] [j] = a[j] [a[0].length - i - 1];
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
            case KeyEvent.VK_W:
                rotateCurBlock();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                softDrop();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                translateCurBlock(-1);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                translateCurBlock(1);
                break;
            case KeyEvent.VK_SPACE:
                while (!softDrop());
                break;
            case KeyEvent.VK_SHIFT:
                holdBlock();
                break;
        }
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        //pass
    }
}