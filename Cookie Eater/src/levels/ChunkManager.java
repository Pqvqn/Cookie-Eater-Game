package levels;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import cookies.*;
import entities.*;
import sprites.*;

public class ChunkManager {
	
	private Level lvl;
	private Chunk[][] chunks;
	public Set<Chunk> active;
	private int chunkRad = 100;
	private ArrayList<Cookie> cookies;
	public SpriteCombo fullSprite;
	
	public ChunkManager(Level level, int[] chunkdims) {
		lvl = level;
		cookies = new ArrayList<Cookie>();
		ArrayList<Sprite> parts = makeChunks(chunkdims, lvl.x_resol, lvl.y_resol);
		fullSprite = new SpriteCombo(lvl.board, parts, lvl.x_resol, lvl.y_resol);
		fullSprite.setPos(0,0);
	}
	
	/*public void setChunk(double x, double y, double r) {
		ArrayList<Chunk> cch = chunksInRadius(x,y,r);
		c_cookies = new ArrayList<Cookie>();
	}*/
	
	public void updateChunkList(ArrayList<Entity> ents) {
		active = new HashSet<Chunk>();
		for(Entity e : ents) {
			ArrayList<Chunk> ch = chunksInRadius(e.getX(),e.getY(),chunkRad);
			for(Chunk c : ch) {
				if(active.add(c)) {
					c.updateSprite();
				}
			}
		}
	}
	
	private ArrayList<Sprite> makeChunks(int[] chunkdims, int xres, int yres) {
		ArrayList<Sprite> chunkSprites = new ArrayList<Sprite>();
		chunks = new Chunk[chunkdims[0]][chunkdims[1]];
		for(int i=0; i<chunks.length; i++) {
			for(int j=0; j<chunks[i].length; j++) {
				chunks[i][j] = new Chunk(lvl, new int[] {i,j},new int[][] {{(xres/chunkdims[0]) * i,(xres/chunkdims[0]) * (i+1)},{(yres/chunkdims[1]) * j,(yres/chunkdims[1]) * (j+1)}});
				chunkSprites.add(chunks[i][j].sprite);
			}
		}
		return chunkSprites;
	}
	//add cookie to appropriate chunk
	public void addCookie(Cookie cook) {
		cookies.add(cook);
		Chunk container = surroundingChunk(cook.getX(),cook.getY());
		container.addCookie(cook,Chunk.IN);

		//add to overhangers
		
	}
	
	public void removeCookie(Cookie cook) {
		cookies.remove(cook);
		surroundingChunk(cook.getX(),cook.getY()).removeCookie(cook,Chunk.IN);
	}
	
	public boolean containsCookie(Cookie cook) {
		return surroundingChunk(cook.getX(),cook.getY()).containsCookie(cook);
	}
	
	//set and sort cookies list into chunks
	public void setCookies(ArrayList<Cookie> cookies) {
		for(int c=0; c<cookies.size(); c++) {
			Cookie cook = cookies.get(c);
			addCookie(cook);
		}
	}
	
	public ArrayList<Cookie> cookies() {
		return cookies;
	}
	
	// create list of cookies in an n x n square of chunks around given chunk
	public ArrayList<ArrayList<Cookie>> cookiesNear(Chunk c, int n){
		ArrayList<ArrayList<Cookie>> cs = new ArrayList<ArrayList<Cookie>>();
		int i=c.getIndices()[0], j=c.getIndices()[1];
		for(int i2=Math.max(i-(n-1)/2,0); i2<=i+(n-1)/2 && i2<chunks.length; i2++) {
			for(int j2=Math.max(j-(n-1)/2,0); j2<=j+(n-1)/2 && j2<chunks[i2].length; j2++) {
				cs.add(chunks[i2][j2].getCookies(Chunk.IN));
			}
		}
		return cs;
	}
	
	public Chunk surroundingChunk(double x, double y) {
		int xindex = (int)((x/lvl.x_resol) * chunks.length);
		int yindex = (int)((y/lvl.y_resol) * chunks[0].length);
		return chunks[xindex][yindex];
	}
	
	public ArrayList<Chunk> chunksInRadius(double x, double y, double r){
		ArrayList<Chunk> cchunks = new ArrayList<Chunk>();
		for(int i=0; i<chunks.length; i++) {
			for(int j=0; j<chunks[i].length; j++) {
				if(Math.sqrt(Math.pow(x-chunks[i][j].getCenter()[0],2)+Math.pow(y-chunks[i][j].getCenter()[1],2)) <= r)
					cchunks.add(chunks[i][j]);
			}
		}
		return cchunks;
	}
	
	public ArrayList<Chunk> chunksInGrid(Chunk c, double r){
		ArrayList<Chunk> cchunks = new ArrayList<Chunk>();
		int[] indices = c.getIndices();
		for(int i=0; i<chunks.length; i++) {
			for(int j=0; j<chunks.length; j++) {
				if(Math.sqrt(Math.pow(indices[0]-i, 2)+Math.pow(indices[1]-j, 2)) <= r){
					cchunks.add(chunks[i][j]);
				}
			}
		}
		return cchunks;
	}
	
	// removes a random cookie from the chunk it belongs to
	public Cookie removeRandomCookie() {
		int c = (int)(Math.random() * cookies.size());
		Cookie gotten = cookies.get(c);
		removeCookie(gotten);
		return gotten;
	}
	
	// removes all chunk information
	public void kill() {
		for(int i=0; i<chunks.length; i++) {
			for (int j=0; j<chunks[i].length; j++) {
				chunks[i][j].kill();
			}
		}
		cookies = new ArrayList<Cookie>();
	}
	
	// updates all chunk images
	public void updateSprites() {
		fullSprite.render(false);

		for(int i=0; i<chunks.length; i++) {
			for(int j=0; j<chunks[i].length; j++) {
				chunks[i][j].updateSprite();
			}
		}
	}
	
	public void paint(Graphics g) {
		/*for(int i=0; i<chunks.length; i++) {
			for(int j=0; j<chunks[i].length; j++) {
				chunks[i][j].paint(g);
				int[][] posRanges = chunks[i][j].posRanges;
				SpriteImage bgbit = new SpriteImage(lvl.board);
				bgbit.setImg(((BufferedImage)lvl.game.draw.boardImage.floor).getSubimage(posRanges[0][0],posRanges[1][0],(posRanges[0][1] - posRanges[0][0]),(posRanges[1][1] - posRanges[1][0])));
				bgbit.paint(g,posRanges[0][0],posRanges[1][0]);
			}
		}*/
		updateChunkList(lvl.board.allEntities());
		if(!fullSprite.rendered()) {
			fullSprite.render(false);
		}
		fullSprite.paint(g);
	}
	
	public static boolean hasDir(int dirInt, int dirToCheck) {
		//return dirInt % (dirToCheck*2) >= dirToCheck;
		return (Math.log(dirInt - dirToCheck)/Math.log(DIRBASE))%1==0; //must use linear algerbra i think
	}
	public static final int DIRBASE=3,NONE=0,UP=1,DOWN=3,LEFT=9,RIGHT=27;
	public static void main(String[] args) {
		for(int i=0; i<=1; i++) {
			for(int j=0; j<=1; j++) {
				for(int k=0; k<=1; k++) {
					for(int l=0; l<=1; l++) {
						int dir = i*UP+j*DOWN+k*LEFT+l*RIGHT;
						System.out.println(i+" "+j+" "+k+" "+l);
						System.out.println((i==1) + "  :  " + hasDir(dir,UP));
						System.out.println((j==1) + "  :  " + hasDir(dir,DOWN));
						System.out.println((k==1) + "  :  " + hasDir(dir,LEFT));
						System.out.println((l==1) + "  :  " + hasDir(dir,RIGHT));
					}
				}
			}
		}
	}
	
	public class Chunk {
		
		public int[][] posRanges;
		private ArrayList<ArrayList<Cookie>> cookies;
		public static final int IN=0,BORDER=1;
		public static final int DIRBASE=3,NONE=0,UP=1,DOWN=3,LEFT=9,RIGHT=27;
		private int centerx, centery;
		private int[] indices;
		public SpriteCombo sprite;
		private Level lvl;
		private SpriteImage floor, wall;
		
		public Chunk(Level level, int[] ind, int[][] ranges) {
			posRanges = ranges;
			cookies = new ArrayList<ArrayList<Cookie>>();
			cookies.add(IN,new ArrayList<Cookie>());
			cookies.add(BORDER,new ArrayList<Cookie>());
			centerx = (int)((posRanges[0][1] + posRanges[0][0])/2 + .5);
			centery = (int)((posRanges[1][1] + posRanges[1][0])/2 + .5);
			indices = ind;
			lvl = level;
			sprite = new SpriteCombo(lvl.board, new ArrayList<Sprite>(), (posRanges[0][1] - posRanges[0][0]), (posRanges[1][1] - posRanges[1][0]));
			sprite.setPos(posRanges[0][0],posRanges[1][0]);
			
			//create constant image layers
			floor = new SpriteImage(lvl.board);
			floor.setPos(posRanges[0][0],posRanges[1][0]);
			sprite.addSprite(floor);
			wall = new SpriteImage(lvl.board);
			wall.setPos(posRanges[0][0],posRanges[1][0]);
			sprite.addSprite(wall);
			//sprite.render(false);
		}
		
		public void addCookie(Cookie c, int loc) {
			cookies.get(loc).add(c);
			sprite.addSprite(c.getSprite());
			if(loc==IN) {
				ArrayList<Chunk> overhangs = overhangChunks(c);
				for(int i=0; i<overhangs.size(); i++) {
					overhangs.get(i).addCookie(c, BORDER);
				}
			}
		}
		public void removeCookie(Cookie c, int loc) {
			cookies.get(loc).remove(c);
			sprite.removeSprite(c.getSprite());
			if(loc==IN) {
				ArrayList<Chunk> overhangs = overhangChunks(c);
				for(int i=0; i<overhangs.size(); i++) {
					overhangs.get(i).removeCookie(c, BORDER);
				}
			}
		}
		public ArrayList<ArrayList<Cookie>> getCookies(){return cookies;}
		public ArrayList<Cookie> getCookies(int loc){return cookies.get(loc);}
		public boolean containsCookie(Cookie c) {return cookies.get(0).contains(c);}
		public int[] getCenter() {return new int[] {centerx,centery};}
		public int[] getIndices() {return indices;}
		
		// returns relative directions of chunks that a cookie overhangs into if it does
		public int overhangs(Cookie c) {
			int dirs = NONE;
			if(c.getY()-c.getRadius() <= posRanges[1][0])dirs += UP;
			if(c.getY()+c.getRadius() >= posRanges[1][1])dirs += DOWN;
			if(c.getX()-c.getRadius() <= posRanges[0][0])dirs += LEFT;
			if(c.getX()+c.getRadius() >= posRanges[0][1])dirs += RIGHT;
			return dirs;
		}
		
		// checks if a direction is represented in the single int combination. also works if multiple dirs summed into dirToCheck
		public boolean hasDir(int dirInt, int dirToCheck) {
			//return dirInt % (dirToCheck*2) >= dirToCheck;
			return Math.log(dirInt - dirToCheck)/Math.log(DIRBASE)%1==0;
		}
		
		// returns list of chunk overhangs
		public ArrayList<Chunk> overhangChunks(Cookie c){
			ArrayList<Chunk> rets = new ArrayList<Chunk>();
			int ovh = overhangs(c);
			if(hasDir(ovh,UP) && indices[1]>0)rets.add(chunks[indices[0]][indices[1]-1]);
			if(hasDir(ovh,DOWN) && indices[1]<chunks[0].length-1)rets.add(chunks[indices[0]][indices[1]+1]);
			if(hasDir(ovh,LEFT) && indices[0]>0)rets.add(chunks[indices[0]-1][indices[1]]);
			if(hasDir(ovh,RIGHT) && indices[0]<chunks.length-1)rets.add(chunks[indices[0]+1][indices[1]]);
			if(hasDir(ovh,UP+LEFT) && indices[1]>0 && indices[0]>0)rets.add(chunks[indices[0]-1][indices[1]-1]);
			if(hasDir(ovh,UP+RIGHT) && indices[1]>0 && indices[0]<chunks.length-1)rets.add(chunks[indices[0]+1][indices[1]-1]);
			if(hasDir(ovh,DOWN+LEFT) && indices[1]<chunks[0].length-1 && indices[0]>0)rets.add(chunks[indices[0]-1][indices[1]+1]);
			if(hasDir(ovh,DOWN+RIGHT) && indices[1]<chunks[0].length-1 && indices[0]<chunks.length-1)rets.add(chunks[indices[0]+1][indices[1]+1]);
			
			return rets;
		}
		
		
		public void kill() {
			ArrayList<Cookie> inC = getCookies(IN);
			for(int c=inC.size()-1; c>=0; c--) {
				inC.get(c).kill(null);
			}
			inC = new ArrayList<Cookie>();
			cookies = new ArrayList<ArrayList<Cookie>>();
		}
		
		// rerenders this chunk
		public void updateSprite() {
			if(!floor.hasImage()) {
				floor.setImg(((BufferedImage)lvl.bgImg).getSubimage(posRanges[0][0],posRanges[1][0],(posRanges[0][1] - posRanges[0][0]),(posRanges[1][1] - posRanges[1][0])));
			}
			if(!wall.hasImage()) {
				wall.setImg(((BufferedImage)lvl.wallImg).getSubimage(posRanges[0][0],posRanges[1][0],(posRanges[0][1] - posRanges[0][0]),(posRanges[1][1] - posRanges[1][0])));
				wall.setClip(lvl.board.wallSpace);
			}
			sprite.render(false);
			fullSprite.addSprite(sprite,false);
		}
	}
}


