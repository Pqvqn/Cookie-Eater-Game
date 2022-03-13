package levels;

import java.util.*;

import cookies.*;
import entities.*;

public class ChunkManager {
	
	private Level lvl;
	private Chunk[][] chunks;
	public Set<Chunk> active;
	private int chunkRad = 100;
	
	public ChunkManager(Level level, int[] chunkdims) {
		lvl = level;
		makeChunks(chunkdims, lvl.x_resol, lvl.y_resol);		
	}
	
	/*public void setChunk(double x, double y, double r) {
		ArrayList<Chunk> cch = chunksInRadius(x,y,r);
		c_cookies = new ArrayList<Cookie>();
	}*/
	
	public void updateChunkList(ArrayList<Entity> ents) {
		active = new HashSet<Chunk>();
		for(Entity e : ents) {
			ArrayList<Chunk> ch = chunksInRadius(e.getX(),e.getY(),chunkRad);
			for(Chunk c : ch)
				active.add(c);
		}
	}
	
	private void makeChunks(int[] chunkdims, int xres, int yres) {
		chunks = new Chunk[chunkdims[0]][chunkdims[1]];
		for(int i=0; i<chunks.length; i++) {
			for(int j=0; j<chunks[i].length; j++) {
				chunks[i][j] = new Chunk(new int[][] {{(xres/chunkdims[0]) * i,(xres/chunkdims[0]) * (i+1)},{(yres/chunkdims[1]) * j,(yres/chunkdims[1]) * (j+1)}});
			}
		}
	}
	//add cookie to appropriate chunk
	public void addCookie(Cookie cook) {
		surroundingChunk(cook.getX(),cook.getY()).addCookie(cook);
	}
	
	//set and sort cookies list into chunks
	public void setCookies(ArrayList<Cookie> cookies) {
		for(int c=0; c<cookies.size(); c++) {
			Cookie cook = cookies.get(c);
			addCookie(cook);
		}
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
	
	public class Chunk {
		
		public int[][] posRanges;
		private ArrayList<Cookie> cookies;
		private int centerx, centery;
		
		public Chunk(int[][] ranges) {
			posRanges = ranges;
			cookies = new ArrayList<Cookie>();
			centerx = (int)((posRanges[0][1] + posRanges[0][0])/2 + .5);
			centery = (int)((posRanges[1][1] + posRanges[1][0])/2 + .5);
		}
		
		public void addCookie(Cookie c) {
			cookies.add(c);
		}
		
		public ArrayList<Cookie> getCookies(){return cookies;}
		public int[] getCenter() {return new int[] {centerx,centery};}
	}
}


