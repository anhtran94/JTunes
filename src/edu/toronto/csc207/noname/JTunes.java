package edu.toronto.csc207.noname;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * JTunes runner class
 */
public class JTunes {

	// Songs in Music Library
	public static Song[] availableSongs = Download.initialize("./data/library.txt");

	/**
	 * Main method containing the event loop and menu prompt.
	 * 
	 * @param args
	 *            command line argument
	 */
	public static void main(String[] args) {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean exit = false;

		// Greet User
		System.out.println("Welcome to JTunes by team noname\n"
				+ "type 'help' for available commands\n");

		// store user input into array fragments
		String[] prompt;
		// bypass repeatedly creating variable in event loop
		int songID;

		// event loop
		while (!exit) {
			System.out.print("Prompt: ");

			try {
				// get user input
				prompt = br.readLine().trim().split(" ");

				// process input
				if (prompt[0].equalsIgnoreCase("help")) {
					// show available commands to use program
					MenuItem.help();

				} else if (prompt[0].equalsIgnoreCase("list")) {
					// list all songs in music library
					System.out.println("List of songs in Music Library");

					if (prompt.length <= 1 || !prompt[1].equalsIgnoreCase("--detail")) {
						// simple view
						MenuItem.list(availableSongs);

					} else if (prompt.length >= 2
							&& prompt[1].equalsIgnoreCase("--detail")) {
						// detailed view
						MenuItem.detailedList(availableSongs);
					}

				} else if (prompt[0].equalsIgnoreCase("cloud")) {
					// cloud interaction

					if (prompt.length >= 2 && prompt[1].equalsIgnoreCase("--list")) {
						// list songs on the cloud
						System.out.println("List of Songs on jTunes Cloud");
						Song[] songs = Download.browseCloud();
						MenuItem.detailedList(songs);

					} else if (prompt.length >= 2
							&& prompt[1].equalsIgnoreCase("--mylist")) {
						// list songs downloaded before with current logged on ID
						System.out.println("Songs you downloaded before");
						Song[] songs = Download.browseCloudwithID();
						MenuItem.detailedList(songs);

					} else if (prompt.length >= 2 && prompt[1].equalsIgnoreCase("--sync")) {
						// download songs from 'mylist' thats not in Music Library
						availableSongs = MenuItem.sync(availableSongs);

					} else if (prompt.length >= 2 && prompt[1].equalsIgnoreCase("--info")) {
						// display credential
						MenuItem.showCredential();

					} else if (prompt.length >= 2 && prompt[1].equalsIgnoreCase("--get")) {
						// get song from jTunes cloud
						availableSongs = MenuItem.downloadSong(availableSongs, prompt);

					} else {
						// log in to JTunes cloud
						MenuItem.authenticate();
					}
					
				} else if (prompt[0].equalsIgnoreCase("playlist")) { // playlist
					// Display playlists located in the playlist folder.
					MenuItem.playlist();

				} else if (prompt[0].equalsIgnoreCase("search")) {
					// Search and list the songs of a artist and play the songs.
					MenuItem.search();

				} else if (prompt[0].equalsIgnoreCase("random")) {
					// play songs in random order
					MenuItem.random();

				} else if (prompt[0].equalsIgnoreCase("play")) {
					// play song identified by its "id"
					songID = Integer.parseInt(prompt[1]);
					MenuItem.play(availableSongs, songID);

				} else if (prompt[0].equalsIgnoreCase("pause")) {
					// pause song (if a song playing)
					MenuItem.pause();

				} else if (prompt[0].equalsIgnoreCase("resume")) {
					// un-pause song (if a song is selected and paused)
					MenuItem.resume();

				} else if (prompt[0].equalsIgnoreCase("stop")) {
					// reset selected song to the beginning
					MenuItem.stop();

				} else if (prompt[0].equalsIgnoreCase("quit")) {
					// quit program
					MenuItem.quit();
					// return;
					exit = true;

				} else {
					System.out
							.println("Invalid command or parameters, see 'help' for usage instruction");
				}

			} catch (ArrayIndexOutOfBoundsException e) {
				// too little parameters provided in this case
				System.out
						.println("Please provide the correct number of parameters or an acceptable value.");
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

	// This should be in its separate class..
	public static void Search_Manager(String art) {
		// Search for the artist and put it into a Song array and then play it.
		int num = 0;
		int index = 0;
		// search for the first time to have the size of searchedSongs.
		for (int i = 0; i < availableSongs.length; i++) {
			if (availableSongs[i].getArtist().equalsIgnoreCase(art)) {
				num++;
			}
		}
		Song[] searchedSongs = new Song[num];
		// search for the second time to put songs in searchSongs.
		for (int i = 0; i < availableSongs.length; i++) {
			if (availableSongs[i].getArtist().equalsIgnoreCase(art)) {
				searchedSongs[index] = availableSongs[i];
				System.out.println((index + 1) + "  " + searchedSongs[index].getArtist()
						+ "  " + searchedSongs[index].getName());
				index++;
			}
		}
		if (index == 0) {
			System.out.println("Artist not found. Please search again.");
		} else {
			MenuItem.play(searchedSongs, 1);
		}
	}

}
