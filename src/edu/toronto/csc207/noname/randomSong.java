package edu.toronto.csc207.noname;

import java.util.Random;

class randomSong{
	private static Song[] availableSongs = Download.initialize("./data/library.txt");
	
	static void randomSong(){
		int random = availableSongs.length;
		Random RandomSong = new Random();
		int RandomSongNumber = RandomSong.nextInt(random)+1;
	    while (Player.isPlaying()==true )
	    {
	    	if ( Player.isPlaying()==false)
	    	{
	    		System.out.println("Playing next Song " + RandomSongNumber );
	    		MenuItem.play(availableSongs, RandomSongNumber);
	    		randomSong.randomSong();
	    	}
	    	
	    }
	    
		
	}
	
	
}

