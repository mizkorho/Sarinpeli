// v 1.01

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

class SarinPeli implements MouseListener
{
	private Graphics gfx;
	
	java.util.List<JBackgroundPanel> lista;

	JFrame ikkuna;
	JPanel gamePanel;
	JBackgroundPanel taustakuva, voittopaneeli, controlPanel, wordPanel;
	Image imageikoni;
	JButton soundbutton;
	
	int sanoja = 5, sanapituus = 5, pelikoko = 195, sanojaetsitty = 0;
	int sliderSanoja = 5, sliderPituus = 5;
	
	int[][] matches;
	String[] matchesString;
	String listoffoundwords;
	
	int startBox, endBox;
	
	static final int WLS_INIT = 5;
	static final int WAS_INIT = 5;
	static final int GAS_INIT = 5;
	
	Boolean soundson = true;
	
	Color jFrameColor = new Color(89, 11, 49);
	Color controlPanelColor = new Color(245, 231, 137);
	Color wordPanelColor = new Color(245, 231, 137);
	Color gamePanelColor = new Color(252, 227, 54);
	Color sliderColor = new Color(245, 210, 100);
	
	public static void main(String[] args)
	{
		new SarinPeli();
	}
	
	public SarinPeli()
	{
		try
		{
			imageikoni = javax.imageio.ImageIO.read(getClass().getResource("ikoni.png"));
		}
		catch (Exception eee)
		{
			System.out.println(eee);
		}

		ikkuna = new JFrame();
		ikkuna.addMouseListener(this);
		ikkuna.setIconImage(imageikoni);
		ikkuna.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ikkuna.setTitle("Sarin Peli");
		ikkuna.setSize(961,639);
		ikkuna.setResizable(false);
		ikkuna.getContentPane().setBackground(jFrameColor);
		ikkuna.setLayout(null);

		taustakuva = new JBackgroundPanel(new Dimension(ikkuna.getWidth()-6, ikkuna.getHeight()-31));
		taustakuva.selectImage("T.jpg");
		taustakuva.setSize(ikkuna.getWidth()-6,ikkuna.getHeight()-31);
		taustakuva.setPreferredSize(new Dimension(ikkuna.getWidth()-6,ikkuna.getHeight()-31));
		taustakuva.setLocation(0,0);

		generateControlPanel();
		ikkuna.add(controlPanel);

		newGame();

		ikkuna.setLocationRelativeTo(null);
		ikkuna.setVisible(true);
	}
	
	public void newGame()
	{
		listoffoundwords = ";";
		sanojaetsitty = 0;

		if (voittopaneeli != null) 
			ikkuna.remove(voittopaneeli);

		wordPanel = new matchPanel();
		wordPanel.setSize(240, 250);
		wordPanel.setPreferredSize(new Dimension(240,250));
		wordPanel.selectImage("R.png");
		wordPanel.setBackground(wordPanelColor);
		wordPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 20));
		wordPanel.setLocation(10,350);
		wordPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		generateGameArea(generateNumbers());
		matches = generateMatches();
		
		ikkuna.add(wordPanel);
		ikkuna.add(gamePanel);
		ikkuna.add(taustakuva);
		
		gfx = gamePanel.getGraphics();
		ikkuna.setVisible(true);
		gamePanel.repaint();
	}

	public int[] generateNumbers()
	{
		int[] numerot = new int[pelikoko];
		for (int i = 0; pelikoko > i; i++)
		{
			numerot[i] = new Random().nextInt(10);
		}
		return numerot;
	}
	
	public int[][] generateMatches()
	{
		int[][] aloitukset = new int[sanoja][2];

		// Etsitään aloituskohtia sanamäärän mukaisesti
		for (int laskuri = 0; laskuri < sanoja; laskuri++)
		{
			int rand = new Random().nextInt(pelikoko);
			String sopivat = "";
			
			if (rand % 15 + sanapituus < 15) // oikealle
			{
				aloitukset[laskuri][0] = rand; 
				aloitukset[laskuri][1] = 0; 
				sopivat += "0";
			}

			if (rand % 15 > sanapituus) // vasemmalle
			{
				aloitukset[laskuri][0] = rand; 
				aloitukset[laskuri][1] = 1; 
				sopivat += "1";
			}
				
			if (rand / 15 > sanapituus) // ylös
			{
				aloitukset[laskuri][0] = rand; 
				aloitukset[laskuri][1] = 2; 
				sopivat += "2";
			}

			if (rand / 15 < (pelikoko/15-sanapituus)) // alas
			{
				aloitukset[laskuri][0] = rand; 
				aloitukset[laskuri][1] = 3;
				sopivat += "3";
			}

			if (sopivat.length() == 0) laskuri--;
			if (sopivat.length() > 1) 
			{
				int rando = new Random().nextInt(sopivat.length());
				aloitukset[laskuri][1] = Integer.parseInt(sopivat.substring(rando, rando+1));
			}				
		}
		
		// Tallennetaan numerot aloituskohdasta lopetuskohtaan asti: aloitukset2
		int[][] aloitukset2 = new int[sanoja][sanapituus];
		for (int ii = 0; ii < sanoja; ii++)
		{
			aloitukset2[ii][0] = aloitukset[ii][0];
			
			if (aloitukset[ii][1] == 0) // oikea
				for (int jj = 1; jj < sanapituus; jj++)
					aloitukset2[ii][jj] = aloitukset2[ii][0]+jj;

			if (aloitukset[ii][1] == 1) // vasen
				for (int jj = 1; jj < sanapituus; jj++)
					aloitukset2[ii][jj] = aloitukset2[ii][0]-jj;

			if (aloitukset[ii][1] == 2) // ylös
				for (int jj = 1; jj < sanapituus; jj++)
					aloitukset2[ii][jj] = aloitukset2[ii][0]-(15*jj);

			if (aloitukset[ii][1] == 3) // alas
				for (int jj = 1; jj < sanapituus; jj++)
					aloitukset2[ii][jj] = aloitukset2[ii][0]+(15*jj);
		}
		
		matchesString = new String[sanoja];
		for (int i = 0; i < sanoja; i++)
			for (int j = 0; j < sanapituus; j++)
			{
				if (j == 0)
					matchesString[i] = ""+lista.get(aloitukset2[i][j]).getValue();
				else 
					matchesString[i] += ""+lista.get(aloitukset2[i][j]).getValue();
			}

		return aloitukset2;
	}	
	
	public void generateControlPanel()
	{
		controlPanel = new JBackgroundPanel();
		controlPanel.setSize(240, 325);
		controlPanel.setPreferredSize(new Dimension(240, 325));
		controlPanel.selectImage("C.png");
		controlPanel.setBackground(controlPanelColor);
		controlPanel.setLayout(null);
		controlPanel.setLocation(10,10);
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		Font labelFont = new Font("Comic Sans MS Bold", Font.BOLD, 16);
		
		JLabel wordLengthSliderLabel = new JLabel("Sanapituus");
		wordLengthSliderLabel.setFont(labelFont);		
		wordLengthSliderLabel.setLocation(80, 120);
		wordLengthSliderLabel.setSize(140, 20);
		controlPanel.add(wordLengthSliderLabel);
		
		JSlider wordLengthSlider = new JSlider(JSlider.HORIZONTAL, 2, 8, WLS_INIT);
		wordLengthSlider.addChangeListener(new wordLengthSliderListener());
		wordLengthSlider.setMajorTickSpacing(8);
		wordLengthSlider.setMinorTickSpacing(1);
		wordLengthSlider.setPaintTicks(true);
		wordLengthSlider.setPaintLabels(true);
		wordLengthSlider.setBorder(BorderFactory.createLineBorder(Color.black));
		wordLengthSlider.setBackground(sliderColor);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(2), new JLabel("2"));
		labelTable.put(new Integer(3), new JLabel("3"));
		labelTable.put(new Integer(4), new JLabel("4"));
		labelTable.put(new Integer(5), new JLabel("5"));
		labelTable.put(new Integer(6), new JLabel("6"));
		labelTable.put(new Integer(7), new JLabel("7"));
		labelTable.put(new Integer(8), new JLabel("8"));
		wordLengthSlider.setLabelTable(labelTable);
		wordLengthSlider.setLocation(20,150);
		wordLengthSlider.setSize(200,50);
		controlPanel.add(wordLengthSlider);
		
		JLabel wordAmountSliderLabel = new JLabel("Sanoja");
		wordAmountSliderLabel.setLocation(95, 20);
		wordAmountSliderLabel.setSize(80, 20);
		wordAmountSliderLabel.setFont(labelFont);
		controlPanel.add(wordAmountSliderLabel);
		
		JSlider wordAmountSlider = new JSlider(JSlider.HORIZONTAL, 1, 8, WAS_INIT);
		wordAmountSlider.addChangeListener(new wordAmountSliderListener());
		wordAmountSlider.setMajorTickSpacing(10);
		wordAmountSlider.setMinorTickSpacing(1);
		wordAmountSlider.setPaintTicks(true);
		wordAmountSlider.setPaintLabels(true);
		wordAmountSlider.setBorder(BorderFactory.createLineBorder(Color.black));
		wordAmountSlider.setBackground(sliderColor);
		Hashtable<Integer, JLabel> labelTable3 = new Hashtable<Integer, JLabel>();
		labelTable3.put(new Integer(1), new JLabel("1"));
		labelTable3.put(new Integer(2), new JLabel("2"));
		labelTable3.put(new Integer(3), new JLabel("3"));
		labelTable3.put(new Integer(4), new JLabel("4"));
		labelTable3.put(new Integer(5), new JLabel("5"));
		labelTable3.put(new Integer(6), new JLabel("6"));
		labelTable3.put(new Integer(7), new JLabel("7"));
		labelTable3.put(new Integer(8), new JLabel("8"));
		wordAmountSlider.setLabelTable(labelTable3);
		wordAmountSlider.setLocation(20,50);
		wordAmountSlider.setSize(200,50);
		controlPanel.add(wordAmountSlider);

		ImageIcon iconForButton = new ImageIcon("S" + soundson + ".png"); 
		try // Jar
		{
			iconForButton = new ImageIcon(javax.imageio.ImageIO.read(getClass().getResource("S" + soundson + ".png")));
		}
		catch (Exception aeas) {}
		soundbutton = new JButton("Sounds", iconForButton);
		soundbutton.setSize(50, 50);
		soundbutton.setLocation(20, 250);
		soundbutton.setMargin(new Insets(0, 10, 0, 0));
		soundbutton.addActionListener(new ActionListener() 
		{
 			public void actionPerformed(ActionEvent e)
			{
				if (soundson) 
					soundson = false;
				else
					soundson = true;
				soundbutton.setIcon(new ImageIcon("S" + soundson + ".png"));
			}
		});
		controlPanel.add(soundbutton);

		iconForButton = new ImageIcon("B.png"); 
		try // Jar
		{
			iconForButton = new ImageIcon(javax.imageio.ImageIO.read(getClass().getResource("B.png")));
		}
		catch (Exception aeas) {}
		JButton generator = new JButton("Luo Peli", iconForButton);
		generator.setSize(140, 50);
		generator.setLocation(80, 250);
		generator.setMargin(new Insets(0, 20, 0, 0));
		generator.addActionListener(new ActionListener() 
		{
 			public void actionPerformed(ActionEvent e)
			{
				if (soundson) playSound("A.wav");
				sanoja = sliderSanoja;
				sanapituus = sliderPituus;
				ikkuna.remove(gamePanel);
				ikkuna.remove(wordPanel);
				ikkuna.remove(taustakuva);
				newGame();
				ikkuna.repaint();
			}
		});
		controlPanel.add(generator);
	}

	public void generateGameArea(int[] numerot)
	{
		gamePanel = new JPanel()
		{
			public void paintComponent( Graphics g ) 
			{
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g;
			}
		};

		Dimension pelialuekoko = new Dimension(680,590);
		gamePanel.setSize(pelialuekoko);
		gamePanel.setPreferredSize(new Dimension(pelialuekoko));
		gamePanel.setBackground(gamePanelColor);
		gamePanel.setLayout(new FlowLayout());
		gamePanel.setLocation(265,10);
//		gamePanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		lista = new ArrayList<JBackgroundPanel>();
		for (int j = 0; j < numerot.length; j++)
		{
			JBackgroundPanel numberPanel = new JBackgroundPanel(new Dimension(40,40), numerot[j]);
			numberPanel.setBackground(Color.black);
			numberPanel.setValue(numerot[j]);
			numberPanel.setOrder(j);
			numberPanel.setName(""+j+"@"+numerot[j]);
			gamePanel.add(numberPanel);
			lista.add(numberPanel);
		}
	}
	
	public class matchPanel extends JBackgroundPanel
	{
		@Override
		protected void paintComponent(Graphics g) 
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
			Font font = new Font("Comic Sans MS Bold", Font.BOLD, 24);
			g2.setFont(font);
			g2.setColor(new Color(2, 140, 208));
			for (int i = 0; i < sanoja; i++)
			{
				String strinkula = "";
				for (int j = 0; j < sanapituus; j++)
				{
					strinkula += ""+lista.get(matches[i][j]).getValue();
				}
				
				if (listoffoundwords.indexOf(strinkula) > 0) 
				{
//					g2.setColor(new Color(58, 214, 2));
					Map attributes = font.getAttributes();
					attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
					g2.setFont(new Font(attributes));
				}
				else
				{
//					g2.setColor(new Color(2, 140, 208));
					Map attributes = font.getAttributes();
					attributes.put(TextAttribute.STRIKETHROUGH, !TextAttribute.STRIKETHROUGH_ON);
					g2.setFont(new Font(attributes));
				}
				
				int start = wordPanel.getWidth()/2 - (int)(g2.getFontMetrics().getStringBounds(strinkula, g2).getWidth())/2;

				g2.drawString(strinkula, start, 30+(i*30)); 
			}				
		}
	}

	public class JBackgroundPanel extends JPanel 
	{
		private BufferedImage img;
		private String imageString;
		public int value;
		public int order;

		public void setPainted(boolean maali)
		{
			if (maali) 
				this.selectImage("" + value + "p.png");
			else 
				this.selectImage("" + value + ".png");
		}

		public void setValue(int arvo)
		{
			value = arvo;
		}

		public void setOrder(int jarkka)
		{
			order = jarkka;
		}

		public int getValue()
		{
			return value;
		}

		public int getOrder()
		{
			return order;
		}

		public JBackgroundPanel(Dimension koko, int numero) 
		{
			this.setSize(koko);
			this.setPreferredSize(koko);
			this.selectImage("" + numero + ".png");
		}
		
		public JBackgroundPanel(Dimension koko) 
		{
			this.setSize(koko);
			this.setPreferredSize(koko);
		}
		
		public JBackgroundPanel()
		{
			
		}
			
		public void selectImage(String imageStringInput)
		{
			imageString = imageStringInput;

			try // Jar
			{
				img = (javax.imageio.ImageIO.read(getClass().getResource(imageString)));
//            	img = ImageIO.read(new File(imageString));
			}
			catch (Exception aeas) {}

/*	        try 
	        {
            	img = ImageIO.read(new File(imageString));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}*/
		}

		@Override
		protected void paintComponent(Graphics g) 
		{
			super.paintComponent(g);
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		}
	}
		
	public void mousePressed(MouseEvent evt) 
	{
		int x = (int)evt.getPoint().getX();
		int y = (int)evt.getPoint().getY();
		x = x - (int)gamePanel.getLocation().getX() - 3;
		y = y - (int)gamePanel.getLocation().getY() - 27;
		
		if (soundson && gamePanel.getComponentAt(x,y) instanceof JBackgroundPanel) playSound("C.wav");

		if (gamePanel.getComponentAt(x, y) != null && gamePanel.getComponentAt(x,y) instanceof JBackgroundPanel) 
		{
			JBackgroundPanel temp = (JBackgroundPanel)gamePanel.getComponentAt(x, y);
			startBox = temp.getOrder();
			temp.setPainted(true);
			gamePanel.repaint();
		}
	}

	public void mouseReleased(MouseEvent evt) 
	{
		int x = (int)evt.getPoint().getX();
		int y = (int)evt.getPoint().getY();
		x = x - (int)gamePanel.getLocation().getX() - 7;
		y = y - (int)gamePanel.getLocation().getY() - 31;
		
		if (gamePanel.getComponentAt(x, y) != null && gamePanel.getComponentAt(x,y) instanceof JBackgroundPanel) 
		{
			JBackgroundPanel temp = (JBackgroundPanel)gamePanel.getComponentAt(x, y);
			endBox = temp.getOrder();
		}		
		
		Boolean foundsomething = false;
		String tempstring = "00000";

		if ((startBox+sanapituus-1) == endBox) // Oikealle
		{
			tempstring = ""+lista.get(startBox).getValue();
			
			for (int i = 1; i < sanapituus; i++)
				tempstring += ""+lista.get(startBox+i).getValue();

			for (int a = 0; a < matchesString.length; a++)
				if (tempstring.equals(matchesString[a])) 
				{
					if (listoffoundwords.indexOf(tempstring) < 1)
					{
						listoffoundwords += ";"+tempstring;
						foundsomething = true;
						for (int i = 1; i < sanapituus; i++)
							lista.get(startBox+i).setPainted(true);
					}
				}
		}

		if (startBox-sanapituus+1 == endBox) // Vasemmalle
		{
			tempstring = ""+lista.get(startBox).getValue();
			
			for (int i = 1; i < sanapituus; i++)
				tempstring += ""+lista.get(startBox-i).getValue();			

			for (int a = 0; a < matchesString.length; a++)
				if (tempstring.equals(matchesString[a])) 
				{
					if (listoffoundwords.indexOf(tempstring) < 1)
					{
						listoffoundwords += ";"+tempstring;
						foundsomething = true;
						for (int i = 1; i < sanapituus; i++)
							lista.get(startBox-i).setPainted(true);
					}
				}
		}

		if (startBox-(15*(sanapituus-1)) == endBox) // ylös
		{
			tempstring = ""+lista.get(startBox).getValue();
			
			for (int i = 1; i < sanapituus; i++)
				tempstring += ""+lista.get(startBox-(15*i)).getValue();
				
			for (int a = 0; a < matchesString.length; a++)
				if (tempstring.equals(matchesString[a])) 
				{
					if (listoffoundwords.indexOf(tempstring) < 1)
					{
						listoffoundwords += ";"+tempstring;
						foundsomething = true;
						for (int i = 1; i < sanapituus; i++)
							lista.get(startBox-(15*i)).setPainted(true);
					}
				}
		}

		if (startBox+(15*(sanapituus-1)) == endBox) // alas
		{
			tempstring = ""+lista.get(startBox).getValue();
			
			for (int i = 1; i < sanapituus; i++)
				tempstring += ""+lista.get(startBox+(15*i)).getValue();

			for (int a = 0; a < matchesString.length; a++)
				if (tempstring.equals(matchesString[a])) 
				{
					if (listoffoundwords.indexOf(tempstring) < 1)
					{
						listoffoundwords += ";"+tempstring;
						foundsomething = true;
						for (int i = 1; i < sanapituus; i++)
							lista.get(startBox+(15*i)).setPainted(true);
					}
				}
		}
		
		if (!foundsomething) 
			lista.get(startBox).setPainted(false);
		else 
			sanojaetsitty++;
		
		gamePanel.repaint();
		wordPanel.repaint();

		if (sanojaetsitty == sanoja) // Voitto
		{
			if (soundson) playSound("V.wav");
			voittopaneeli = new JBackgroundPanel(gamePanel.getSize());
			voittopaneeli.selectImage("V.jpg");
			voittopaneeli.setLocation(gamePanel.getLocation());
			ikkuna.remove(gamePanel);
			ikkuna.add(voittopaneeli);
			ikkuna.remove(taustakuva);
			ikkuna.add(taustakuva);
			ikkuna.repaint();
		}
	}

	public void mouseClicked(MouseEvent evt) {}	
	public void mouseExited(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}

	public void playSound(String aani)
	{
		try 
		{
			File yourFile = new File(aani);
			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;
			Clip clip;

			stream = AudioSystem.getAudioInputStream(yourFile);
			format = stream.getFormat();
			info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
		}
		catch (Exception e) {}
	}

	class wordLengthSliderListener implements ChangeListener 
	{
	    public void stateChanged(ChangeEvent e) 
	    {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) 
		    	sliderPituus = (int)source.getValue();
	    }
	}

	class wordAmountSliderListener implements ChangeListener 
	{
	    public void stateChanged(ChangeEvent e) 
	    {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) 
		    	sliderSanoja = (int)source.getValue();
	    }
	}
}