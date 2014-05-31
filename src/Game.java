import java.util.*;
import java.awt.*;

public class Game extends Panel
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
	What happens when the tetramino goes down one square. Return whether I need to spawn another block
	*/
	private boolean softDrop ()
	{
		tetraY += 1;
		//TODO: Check for collisions better.

		if (tetraY + 4 > height)
		{
			return true;
		}

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (field [i + tetraX] [j + tetraY] != 0 && curBlock [i] [j] != 0)
				{
					return true;
				}
			}
		}
		return false;
	}

	public void update ()
	{
		if (this.softDrop())
		{
			this.spawnNewBlock();
		}
	}

	/**
	Add the current block the the field, and spawn a random block at the top
	*/
	public void spawnNewBlock ()
	{
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

		for (int i = tetraX; i < tetraX + 4; i++)
		{
			for (int j = tetraY; j < tetraY + 4; j++)
			{
				g.setColor (colours [curBlock [i - tetraX] [j - tetraY]]);
				g.fillRect (i * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
				g.setColor (Color.BLACK);
				g.drawRect (i * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			}
		}
	}

	/**
	Rotates a tetramino 90 degrees clockwise.
	*/
	private static int [] [] rotate (int [] [] a)
	{
		//use the rotation matrix [[0, 1], [-1, 0]]
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
}