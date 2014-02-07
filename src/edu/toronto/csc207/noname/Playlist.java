package edu.toronto.csc207.noname;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.InputStreamReader;


class Playlist {
	private String name;
	private String filename;
	private File playlist;
	private Scanner sc;
	private int index = 1;
	private File parentDir = new File("./data/playlist");
	private String path;

	public Playlist(String name) {
		if (!parentDir.exists()) {
			parentDir.mkdir();
		}

		this.setName(name);
		this.filename = name + ".txt";
		this.playlist = new File(parentDir, this.filename);
		try {
			this.playlist.createNewFile();
			this.path = "./data/playlist/" + this.filename;
		} catch (IOException e) {
			System.out.println("Cannot create the playlist.");
		}
	}
public static void PlaylistManager (Playlist pl) {
		
		//store playlist as an array of songs
		Song[] inplaylist = Download.initialize(pl.getPath());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean exit = false;
		int songID=0;
		
		// Greet User
		System.out.println("You are now in playlist "+ pl.getName() + ", type 'help' for available commands\n");

		// store user input into array fragments
		String[] cmd;
		// bypass repeatedly creating variable in event loop
		
		//get the name of the current playlist
		String currentplaylist = pl.getName();
		// event loop
		while (!exit) {
			System.out.print("Command: ");
			try {
				// get user input
				cmd = br.readLine().trim().split(" ");

				// process input
				if (cmd[0].equalsIgnoreCase("help")) {
					// show available commands to use program
					MenuItem.playlist_help();
				} 
										
				else if (cmd[0].equalsIgnoreCase("list")) { // list all the songs in playlist
					MenuItem.list(inplaylist);
					
				} else if (cmd[0].equalsIgnoreCase("add")) { // add song to the list
						songID = Integer.parseInt(cmd[1]);

						if (songID > 0 && songID <= JTunes.availableSongs.length) {
								inplaylist = pl.add_song(JTunes.availableSongs[songID - 1], inplaylist);
						}else {
							System.out.println("Invalid id. Try again.");
				}
						
				} else if (cmd[0].equalsIgnoreCase("delete")) {
					songID = Integer.parseInt(cmd[1]);
					
					if (songID > 0 && songID <= inplaylist.length) {
						pl.delete_song(songID);
						inplaylist = Download.initialize(pl.getPath());
						
					} else {
						System.out.println("Invalid id. Try again.");
					}
				} else if (cmd[0].equalsIgnoreCase("delete_playlist")) {
					pl.delete_pl();

				} else if (cmd[0].equalsIgnoreCase("play")) {
					
					songID = Integer.parseInt(cmd[1]);
					MenuItem.play(inplaylist, songID);

				}else if (cmd[0].equalsIgnoreCase("quit")) {
					System.out.println("Quit playlist.");
					exit = true;
					
				} else if (cmd[0].equalsIgnoreCase("switch")) { // Switch between playlists within playlist mode
					
					// Obtain the name of the playlist the user wants to switch
					String inputplaylist = "";
					for (int i = 1; i < cmd.length; i++) {
						inputplaylist += cmd[i].toString() + " ";
					}
					inputplaylist = inputplaylist.trim();

					// check if he did not mistaken and inputed the name of the playlist he is in already
					
					if (cmd.length >= 2 && inputplaylist.equalsIgnoreCase(currentplaylist)) {
						
						System.out.println("You are already playing the playlist " + currentplaylist);
						
					} else {
					
						// obtain the names of all the existing playlists
						File[] playlist = new File("./data/playlist").listFiles();
						String[] playlistsavailable = new String[playlist.length];
						
						if (playlist.length >= 2) {
							for (int i = 0; i < playlist.length; i++) {
								playlistsavailable[i] = playlist[i].getName().replace(".txt", "");
							}		
						}
						// Delete the current playlist name before querying among all
						// the existing playlists
						int currentplaylistindex = Arrays.binarySearch(playlistsavailable, currentplaylist);
						
						// System.out.println(currentplaylistindex+"*");
						playlistsavailable[currentplaylistindex] = "";
						// Query among the existing playlist to see if the playlist
						// exists
						for (int i = 0; i < playlist.length; i++) {
					
					
							if (playlistsavailable[i].equals(inputplaylist)) {
								Playlist current_list = new Playlist(
										playlistsavailable[i]); // if found, switches
																// to the new playlist
								PlaylistManager(current_list);

							
							}else {
							// error message if the input playlist does not exist
							System.out.println("There are no playlists named: "	+ inputplaylist);
							}
						}	
				}} else if (cmd[0].equalsIgnoreCase("pause")) { 
					MenuItem.pause();
				} else if (cmd[0].equalsIgnoreCase("stop")){
					MenuItem.stop();
				} else if (cmd[0].equalsIgnoreCase("resume")){
					MenuItem.resume();
				}else {
					System.out.println("Invalid command. Try again.");
				}

			}catch (ArrayIndexOutOfBoundsException e) {
			// too little parameters provided in this case
			System.out.println("Please provide the correct number of parameters or an acceptable value.");
		} catch (NumberFormatException e) {
			System.out.println("Numeric value expected, try again");
		} catch (IOException e) {
			System.out.println("Sorry, please try again");
		} finally {
			// formating
			System.out.println();
		}
	}
}
	
	public Song[] add_song(Song newSong, Song[] list) {

		Song[] newList = new Song[list.length + 1];

		try {

			for (int i = 0; i < list.length; i++) {
				newList[i] = list[i];
			}

			newList[list.length] = newSong;

			BufferedWriter bw = new BufferedWriter(
					new FileWriter(this.playlist));

			for (int i = 0; i < newList.length; i++) {
				bw.write(newList[i].getHash() + ", " + newList[i].getMidiFile()
						+ ", " + newList[i].getName() + ", "
						+ newList[i].getArtist());
				bw.newLine();
			}

			index++;
			// timesadded++;
			bw.close();
		} catch (IOException e) {
			System.out.println("Cannot add this song.");
		}

		return newList;

	}

	public void delete_song (int index){

		//read the .txt file line by line, copy everything to a temporary .txt file except the line contains the song we want to delete

		try{

		final File tempFile = new File(parentDir, this.name + "temp" + ".txt");
		tempFile.createNewFile();

		BufferedReader br = new BufferedReader( new FileReader(this.playlist));
		BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile, true));

		String line = br.readLine();
		String indexToRemove= Integer.toString(index);

		while((line = br.readLine()) != null) {

		// trim newline when compare the first character of the line with indexToRemove
		//System.out.println("ghghhg");

		String trimmedLine = line.trim();

		if (trimmedLine.startsWith(indexToRemove)) continue;
		bw.write(line);
		//bw.newLine();
		}

		bw.close();
		br.close();

		//rename the temporary file
		tempFile.renameTo(this.playlist);

		}
		catch (FileNotFoundException ex) {
		System.out.println("Could not find file.");
		}
		catch (IOException e) {
		System.out.println("Could not delete song.");
		}
		catch (IllegalArgumentException i) {
		System.out.println("Deletion failed");
		}
		}

	public void delete_pl() {
		
		File playlist = this.playlist;
		playlist.delete();
		
		if (!playlist.delete()) {
		System.out.println("type quit to exit playlist menu");
		}
	}

	public String getName() {
		return name;

	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		this.path = path;
		return path;
	}
}
