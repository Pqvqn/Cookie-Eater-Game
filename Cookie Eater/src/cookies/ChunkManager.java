package cookies;

import java.util.*;

import ce3.*;

public class ChunkManager {
	
	private Game game;
	private Board board;
	private Chunk[][] chunks;
	
	public ChunkManager(Game frame, Board gameboard, int[] chunkdims) {
		game = frame;
		board = gameboard;
		makeChunks(chunkdims, board.x_resol, board.y_resol);		
	}
	
	private void makeChunks(int[] chunkdims, int xres, int yres) {
		chunks = new Chunk[chunkdims[0]][chunkdims[1]];
		for(int i=0; i<chunks.length; i++) {
			for(int j=0; j<chunks[i].length; j++) {
				chunks[i][j] = new Chunk(new int[][] {{(xres/chunkdims[0]) * i,(xres/chunkdims[0]) * (i+1)},{(yres/chunkdims[1]) * j,(yres/chunkdims[1]) * (j+1)}});
			}
		}
	}
	
	//set and sort cookies list into chunks
	public void setCookies(ArrayList<Cookie> cookies) {
		
	}
	
	
	public class Chunk {
		
		int[][] posRanges;
		
		public Chunk(int[][] ranges) {
			posRanges = ranges;
		}
	}
}


