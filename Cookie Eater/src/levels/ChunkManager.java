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
	
	/*public void updateChunkList(ArrayList<Entity> ents) {
		active = new HashSet<Chunk>();
		for(Entity e : ents) {
			ArrayList<Chunk> ch = chunksInRadius(e.getX(),e.getY(),chunkRad);
			for(Chunk c : ch) {
				if(active.add(c)) {
					c.update();
				}
			}
		}
	}*/
	
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
		surroundingChunk(cook.getX(),cook.getY()).addCookie(cook);
	}
	
	public void removeCookie(Cookie cook) {
		cookies.remove(cook);
		surroundingChunk(cook.getX(),cook.getY()).removeCookie(cook);
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
				cs.add(chunks[i2][j2].getCookies());
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
		if(!fullSprite.rendered()) {
			fullSprite.render(false);
		}
		fullSprite.paint(g);
	}
	
	public class Chunk {
		
		public int[][] posRanges;
		private ArrayList<Cookie> cookies;
		private int centerx, centery;
		private int[] indices;
		public SpriteCombo sprite;
		private Level lvl;
		private SpriteImage bgSlice;
		
		public Chunk(Level level, int[] ind, int[][] ranges) {
			posRanges = ranges;
			cookies = new ArrayList<Cookie>();
			centerx = (int)((posRanges[0][1] + posRanges[0][0])/2 + .5);
			centery = (int)((posRanges[1][1] + posRanges[1][0])/2 + .5);
			indices = ind;
			lvl = level;
			sprite = new SpriteCombo(lvl.board, new ArrayList<Sprite>(), (posRanges[0][1] - posRanges[0][0]), (posRanges[1][1] - posRanges[1][0]));
			sprite.setPos(posRanges[0][0],posRanges[1][0]);
			bgSlice = new SpriteImage(lvl.board);
			bgSlice.setPos(posRanges[0][0],posRanges[1][0]);
			sprite.addSprite(bgSlice);
			//sprite.render(false);
		}
		
		public void addCookie(Cookie c) {
			cookies.add(c);
			sprite.addSprite(c.getSprite());
		}
		public void removeCookie(Cookie c) {
			cookies.remove(c);
			sprite.removeSprite(c.getSprite());
		}
		public ArrayList<Cookie> getCookies(){return cookies;}
		public boolean containsCookie(Cookie c) {return cookies.contains(c);}
		public int[] getCenter() {return new int[] {centerx,centery};}
		public int[] getIndices() {return indices;}
		public void kill() {
			for(int c=cookies.size()-1; c>=0; c--) {
				cookies.get(c).kill(null);
			}
			cookies = new ArrayList<Cookie>();
		}
		public void updateSprite() {
			if(!bgSlice.hasImage()) {
				bgSlice.setImg(((BufferedImage)lvl.game.draw.boardImage.floor).getSubimage(posRanges[0][0],posRanges[1][0],(posRanges[0][1] - posRanges[0][0]),(posRanges[1][1] - posRanges[1][0])));
			}
			sprite.render(false);
			fullSprite.addSprite(sprite,false);
		}
	}
}


