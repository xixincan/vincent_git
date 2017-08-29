package vincent.tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Tetris extends JPanel {
	
	private Timer timer;	//瀹氭椂鍣�
	private int levelTime=600;
	
	private int status;
	private static final int RUNNING=0;
	private static final int PAUSE=1;
	private static final int OVER=2;
	private static final int START=3;

	private int score;	//鐢ㄤ簬绉垎
	private int lines;	//娑堢殑琛屾暟
	private int level=1;
	private int[] scoreTab={0,5,10,20,40};//绉垎鏉�
	
	private Cell[][] wall;
	private Tetromino tetromino;
	private Tetromino nextOne;
	
	private static BufferedImage background;
	private static BufferedImage gameOver;
	private static BufferedImage pause;
	private static BufferedImage start;
	public static BufferedImage T;
	public static BufferedImage J;
	public static BufferedImage L;
	public static BufferedImage Z;
	public static BufferedImage S;
	public static BufferedImage O;
	public static BufferedImage I;
	
	public static final int ROWS=20;
	public static final int COLS=10;
	public static final int CELL_SIZE=26;
	
	static{
		try{
			background=ImageIO.read(Tetris.class.getResource("tetris.png"));
			gameOver=ImageIO.read(Tetris.class.getResource("game-over.png"));
			pause=ImageIO.read(Tetris.class.getResource("pause.png"));
			start=ImageIO.read(Tetris.class.getResource("start.png"));
			T=ImageIO.read(Tetris.class.getResource("T.png"));
			J=ImageIO.read(Tetris.class.getResource("J.png"));
			L=ImageIO.read(Tetris.class.getResource("L.png"));
			Z=ImageIO.read(Tetris.class.getResource("Z.png"));
			S=ImageIO.read(Tetris.class.getResource("S.png"));
			O=ImageIO.read(Tetris.class.getResource("O.png"));
			I=ImageIO.read(Tetris.class.getResource("I.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		JFrame frame=new JFrame("Vincent TETRIS");
		Tetris tetris=new Tetris();
		tetris.setBackground(new Color(0x0000ff));
		frame.add(tetris);
		frame.setSize(530, 580);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);//浼氬敖蹇皟鐢╬aint()鏂规硶
		
		tetris.action();	//璋冪敤鍚姩鏂规硶
	}
	/**
	 * 閲嶅啓JPanel涓殑paint()鏂规硶,鐢ㄦ潵缁樺埗
	 * */
	@Override
	public void paint(Graphics g){
		g.drawImage(background, 0, 0, null);
		g.translate(15, 15);
		
		paintWall(g);
		paintTetromino(g);
		paintNextOne(g);
		paintScore(g);
		paintStatus(g);
	}
	
	/**
	 * Tetris鐨勫惎鍔ㄦ柟娉�
	 * */
	public void action(){
		wall=new Cell[ROWS][COLS];
		//wall[5][5]=new Cell(5,5,T);
		tetromino=Tetromino.randomOne();
		nextOne=Tetromino.randomOne();
		
		//status=RUNNING;
		status=START;
		
		//澶勭悊閿洏鎸変笅浜嬩欢锛屽湪鎸変笅閿椂鎵ц鐩稿簲鐨勬柟娉�
		KeyAdapter l=new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				int key=e.getKeyCode();
				switch(status){
				case OVER:
					processGameOverKey(key);
					break;
				case PAUSE:
					processPauseKey(key);
					break;
				case RUNNING:
					processRunningKey(key);
					break;
				case START:
					processStartKey(key);
					break;
				}
				repaint();//閲嶆柊璋冪敤paint()
			}

		};
		this.requestFocus();
		//this.requestFocusInWindow();//the difference???
		this.addKeyListener(l);
		
		/**鍦ˋction鏂逛腑娣诲姞瀹氭椂鍣�*/
		timer= new Timer();
		timer.schedule(new TimerTask(){
			public void run(){
				if(status==RUNNING){
					softDropAction();
				}
				repaint();
			}
		}, 0, levelTime-(level-1)*35);
	}
	private void processStartKey(int key){
		switch(key){
		case KeyEvent.VK_ENTER:
			status=RUNNING;
			break;
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			System.exit(0);
		}
	}
	private void processPauseKey(int key){
		switch(key){
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			System.exit(0);
			break;
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_C:
			status=RUNNING;
			break;
		}
	}
	private void processRunningKey(int key){
		switch(key){
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			System.exit(0);
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			softDropAction();
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			moveLeftAction();
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			moveRightAction();
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			rotateRightAction();
			break;
		case KeyEvent.VK_SPACE:
			hardDropAction();
			break;
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_P:
			status=PAUSE;
			break;
		}
	}
	private void processGameOverKey(int key){
		switch(key){
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			System.exit(0);
			break; 
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_S:
			this.level=1;
			this.score=0;
			this.wall=new Cell[ROWS][COLS];
			this.tetromino=Tetromino.randomOne();
			this.nextOne=Tetromino.randomOne();
			status=START;
			break;
		}
	}
	/**
	 * 缁樺埗姝ｅ湪涓嬭惤鐨勫洓鏍兼柟鍧�
	 * @param g
	 * */
	public void paintTetromino(Graphics g){
		if(tetromino==null){
			return;
		}
		//灏嗘瘡涓牸瀛愮殑row锛宑ol鎹㈢畻涓哄涓殑x锛寉骞舵樉绀�
		for(int i=0;i<tetromino.cells.length;i++){
			int x=tetromino.cells[i].getCol()*CELL_SIZE;
			int y=tetromino.cells[i].getRow()*CELL_SIZE;
			g.drawImage(tetromino.cells[i].getImage(), x, y, null);
		}
	}
	/**
	 * 缁樺埗鍗冲皢涓嬭惤鐨勫洓鏍兼柟鍧�
	 * */
	public void paintNextOne(Graphics g){
		if(nextOne==null){
			return;
		}
		for(int i=0;i<nextOne.cells.length;i++){
			int x=(nextOne.cells[i].getCol()+10)*CELL_SIZE;
			int y=(nextOne.cells[i].getRow()+1)*CELL_SIZE;
			g.drawImage(nextOne.cells[i].getImage(), x, y, null);
		}
	}

	private void paintWall(Graphics g) {
		for(int row=0;row<wall.length;row++){
			for(int col=0;col<wall[row].length;col++){
				int x=col*CELL_SIZE;
				int y=row*CELL_SIZE;	//x,y琛ㄧず鏍煎瓙鍦ㄨ儗鏅涓殑鍧愭爣
				if(wall[row][col]==null){
					//g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
				}else{
					//g.drawImage(T, x, y, null);
					g.drawImage(wall[row][col].getImage(), x, y, null);	//x,y-1浠呬粎鏄负浜嗙編瑙�
				}
			}
		}
	}
	
	private boolean outOfBounds(){
		for(int i=0;i<tetromino.cells.length;i++){
			int row=tetromino.cells[i].getRow();
			int col=tetromino.cells[i].getCol();
			if(row<0||row>=ROWS||col<0||col>=COLS){
				return true;
			}
		}
		return false;
	}
	private boolean coincide(){
		for(int i=0;i<tetromino.cells.length;i++){
			int row=tetromino.cells[i].getRow();
			int col=tetromino.cells[i].getCol();
			if(wall[row][col]!=null){
				return true;
			}
		}
		return false;
	}

	public void moveLeftAction(){
		tetromino.moveLeft();
		if(outOfBounds()||coincide()){
			tetromino.moveRight();
		}	
	}
	public void moveRightAction(){
		tetromino.moveRight();
		if(outOfBounds()||coincide()){
			tetromino.moveLeft();
		}
	}
	public void softDropAction(){
		if(canDrop()){
			tetromino.softDrop();
		}else{
			landIntoWall();
			destroyLines();
			if(!isGameOver()){
				tetromino=nextOne;
				nextOne=Tetromino.randomOne();
			}else{
				status=OVER;
			}
		}
	}
	public void hardDropAction(){
		while(canDrop()){
			tetromino.softDrop();
		}
		landIntoWall();
		destroyLines();
		if(!isGameOver()){
			tetromino=nextOne;
			nextOne=Tetromino.randomOne();
		}else{
			status=OVER;
		}
	}
	public void rotateRightAction(){
		tetromino.rotateRight();
		if(outOfBounds()||coincide()){	//鍑虹晫涓庨噸鍚堜綅缃笉鑳借皟鎹�
			tetromino.rotateLeft();
		}
	}
	
	private boolean canDrop(){
		for(Cell cell:tetromino.cells){
			int row=cell.getRow();
			if(row==ROWS-1){
				return false;
			}
		}
		for(Cell cell:tetromino.cells){
			int row=cell.getRow();
			int col=cell.getCol();
			if(row>=0&&row<ROWS&&col>=0&&col<COLS&&wall[row+1][col]!=null){
				return false;
			}
		}
		return true;
	}
	private void landIntoWall(){
		for(Cell cell:tetromino.cells){
			int row=cell.getRow();
			int col=cell.getCol();
			wall[row][col]=cell;
		}
		tetromino=null;
	}
	private boolean fullCells(int row){
		for(int col=0;col<COLS;col++){
			if(wall[row][col]==null){
				return false;
			}
		}
		return true;
	}
	private void destroyLines(){
		lines=0;
		for(int row=0;row<ROWS;row++){
			if(fullCells(row)){
				deleteRow(row);
				lines++;
			}
		}
		score=score+scoreTab[lines];//娉ㄦ剰+=锛屾槸绱锛侊紒锛�
		level=score/100+1;
		
	}
	private void deleteRow(int row){
		for(int i=row;i>0;i--){
			System.arraycopy(wall[i-1], 0, wall[i], 0, COLS);
		}
		Arrays.fill(wall[0], null);
	}
	
	public static final int FONT_COLOR=0xaa11ff;
	public static final int FONT_SIZE=30;
	private void paintScore(Graphics g) {
		String str1="SCORE:"+score;
		String str2="LEVEL:"+level;
		String str3="Via Vincent";
		g.setColor(new Color(FONT_COLOR));
		Font font=g.getFont();	//鑾峰彇褰撳墠g鐨勫瓧浣�
		font=new Font(font.getName(),font.getStyle(),FONT_SIZE);
		g.setFont(font);
		g.drawString(str1, 290, 165);
		g.drawString(str2, 290, 220);
		font=new Font(font.getName(),font.getStyle(),20);
		g.drawString(str3, 290, 275);
	}
	
	private void paintStatus(Graphics g) {
		switch(status){
		case PAUSE:
			g.drawImage(pause, -15, -15, null);
			break;
		case OVER:
			g.drawImage(gameOver, -15, -15, null);
			break;
		case START:
			g.drawImage(start, -15, -15, null);
			break;
		}
	}
	
	private boolean isGameOver(){
		for(Cell cell:nextOne.cells){
			int row=cell.getRow();
			int col=cell.getCol();
			if(wall[row][col]!=null)
				return true;
		}
		return false;
	}
}







