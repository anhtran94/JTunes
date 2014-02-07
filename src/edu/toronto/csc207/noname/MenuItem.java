package edu.toronto.csc207.noname;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Class which contain helper methods for main JTunes class
 */
class MenuItem {

	// for user input
	private static Scanner sc = new Scanner(System.in);

	// available main menu commands
	private static String[][] commands = {
			{ "Command [optional parameters]", "Action" },
			{ "==============================", "=======" },
			{ "list", "Display a list of songs in the music library" },
			{ "list [--detail]", "Display a detailed list of songs in the music library" },
			{ "cloud", "Log on to the cloud" },
			{ "cloud [--list]", "Display a detailed list of songs on the JTunes cloud" },
			{ "cloud [--mylist]",
					"Display a detailed list of downloaded songs associated with logged on JTunes cloud ID" },
			{ "cloud [--sync]",
					"Download songs that user had previously downloaded but not in Music Library" },
			{ "cloud [--get (id)]", "Get the song" },
			{ "cloud [--info]", "Show jTunes cloud login credential (if logged in)" },
			{ "playlist", "Display and manager playlists." },
			{ "search", "Search the songs of an artist and play the songs." },
			{ "random",
					"Play songs in random order (will activate only when a song is playing)" },
			{ "play (id)", "Play the song selected identified by its 'id'" },
			{ "pause", "Pause the player (if playing)" },
			{ "resume", "Resume playing (if paused)" },
			{ "stop", "Stop the player (resume will play song from the start)" },
			{ "help", "Display available commands" }, { "quit", "Close jTunes" } };

	/**
	 * Display the help menu
	 */
	static void help() {
		for (int i = 0; i < commands.length; i++) {
			System.out.printf("%-32s%-50s%n", commands[i][0], commands[i][1]);
		}
	}

	// available commands to handle playlist
	private static String[][] playlist_commands = {
			{ "Command (optional parameters)", "Action" },
			{ "==============================", "=======" },
			{ "list", "Display a list of songs in the current playlist" },
			{ "add (id from library)", "Add songs from Library to the playlist" },
			{ "delete (id in the playlist)", "Delete a song in the playlist" },
			{ "delete_playlist", "Delete current playlist" },
			{ "play (id)", "Play the song selected identified by its 'id'" },
			{ "quit", "Quit playlist menu and back to Library" },
			{ "switch (playlist name}", "Switch to another playlist" },
			{ "pause", "Pause a song" }, { "stop", "Stop the playlist" },
			{ "resume", "Continue playing the song" },
			{ "random", "Playing songs randomly" } };

	/**
	 * Display playlist's help menu
	 */
	static void playlist_help() {
		for (int i = 0; i < playlist_commands.length; i++) {
			System.out.printf("%-32s%-50s%n", playlist_commands[i][0],
					playlist_commands[i][1]);
		}
	}

	/**
	 * Display the provided list of Song as a simple list
	 * 
	 * @param list
	 *            an array of Song to be displayed
	 */
	static void list(Song[] list) {
		System.out.println("Simple View\n" + "---------------");
		System.out.printf("%-8s%s%n", "id", "song name");
		System.out.printf("%-8s%s%n", "===", "==========");

		for (int id = 1; id <= list.length; id++) {
			System.out.printf("%2d%-6c%-30s%n", id, '.', list[id - 1]);
		}
	}

	/**
	 * Display the provided list of Song in a detailed format
	 * 
	 * @param list
	 *            an array of Song to be displayed
	 */
	static void detailedList(Song[] list) {
		System.out.println("Detailed View\n" + "---------------");
		System.out.printf("%-8s%-30s %-20s %s%n", "id", "song name", "artist",
				"special identifier");
		System.out.printf("%-8s%-30s %-20s %s%n", "===", "==========", "========",
				"====================");

		for (int id = 1; id <= list.length; id++) {
			System.out.printf("%2d%-6c%-30s %-20s %s%n", id, '.', list[id - 1],
					list[id - 1].getArtist(), list[id - 1].getHash());
		}
	}

	/**
	 * Play the song that is in the Song array identified by songID
	 * 
	 * @param songs
	 *            an array of Song object
	 * @param songID
	 *            integer that can be used to identify the song
	 */
	static void play(Song[] songs, int songID) {
		if (songID > 0 && songID <= songs.length) {
			System.out.println("Loading Song: " + songs[songID - 1]);
			Player.play(songs[songID - 1].getMidiFile());
			System.out.println("Now playing: " + songs[songID - 1]);

		} else {
			System.out.println("Invalid id. Try again.");
		}
	}

	/**
	 * Resume playing song
	 */
	static void resume() {
		if (Player.resume()) {
			System.out.println("Resuming, type 'pause' to pause");
		} else {
			System.out.println("Song is not selected or already playing");
		}
	}

	/**
	 * Stop song from playing
	 */
	static void pause() {
		if (Player.pause()) {
			System.out.println("Paused, type 'resume' to un-pause");
		} else {
			System.out.println("Already paused.");
		}
	}

	/**
	 * Stop song from playing and reset song position to beginning
	 */
	static void stop() {
		if (Player.stop()) {
			System.out.println("Stopped, type 'resume' to start from beginning");
		} else {
			System.out.println("Not initialized.");
		}
	}

	/**
	 * Quit the program
	 */
	static void quit() {
		System.out.println("Good Bye!");
		Player.flush(); // force player to stop
		// quit variable modified in actual event loop, else uncomment below
		// System.exit(0);
	}

	/**
	 * User login to connect to JTunes cloud
	 */
	static void authenticate() {

		String prompt;

		if (User.isLoggedin()) {
			System.out
					.print("You are already logged in, do you want to switch account? (y/n): ");
			prompt = sc.nextLine();
			if (!Character.toString(prompt.charAt(0)).equalsIgnoreCase("y")) {
				System.out.println("Cancelled.");
				return;
			}
		}

		System.out.println("Do you have a JTunes Cloud account? (y/n)");
		prompt = sc.nextLine();
		if (Character.toString(prompt.charAt(0)).equalsIgnoreCase("y")) {

			// get login credential
			System.out.println("Please type your email");
			User.setEmail(sc.nextLine());
			System.out.println("Please type password");
			User.setPassword(sc.nextLine());

			// test login by checking user's download list
			if (Download
					.loadXML("http://greywolf.cdf.toronto.edu:1337/noname/listSongs?email="
							+ User.getEmail() + "&password=" + User.getPassword())) {
				User.setLoggedin(true);
				System.out.println("You have successfully logged in");

			} else {
				System.out.println("Invalid account/password. Please try again");
			}

		} else if (Character.toString(prompt.charAt(0)).equalsIgnoreCase("n")) {
			System.out.println("Please register");
			// get registration credential

			System.out.println("Please enter your name");
			User.setName(sc.nextLine());

			System.out.println("Please enter your email");
			User.setEmail(sc.nextLine());

			System.out.println("Please enter a password");
			User.setPassword(sc.nextLine());

			if (Download
					.loadXML("http://greywolf.cdf.toronto.edu:1337/noname/createUser?name="
							+ User.getName()
							+ "&email="
							+ User.getEmail()
							+ "&password="
							+ User.getPassword())) {
				User.setLoggedin(true);
				System.out.println("You have successfully registered and logged in");
			} else {
				System.out
						.println("Cannot create account, account may already exist, try another.");
			}

		} else {
			System.out.println("Invalid response. Operation cancelled.");
		}

		System.out.println("For additional 'cloud' options, see 'help' for the list");
	}

	/**
	 * Display login credentials
	 */
	static void showCredential() {
		if (User.isLoggedin()) {
			System.out.println("Logged in with email: " + User.getEmail());
		} else {
			System.out.println("You are not logged in, \n"
					+ "issue 'cloud' command to login first.");
		}
	}

	/**
	 * Download song and merge with songList
	 * 
	 * @param songList
	 *            the original list of songs
	 * @param input
	 *            contains previous user input
	 * @return a new array of Song which contain songList and the downloaded Song
	 */
	static Song[] downloadSong(Song[] songList, String[] input) {

		if (!User.isLoggedin()) {
			System.out.println("You'll need to log in first!");
		}

		// get a list of songs available on the cloud
		Song[] songs = Download.browseCloud();

		// show the list of song so user know what to pick
		System.out.println("jTunes Cloud song catalogue");
		detailedList(songs);
		System.out.println();

		int songID;
		Song song = null;

		try {
			if (input.length >= 3) {
				songID = Integer.parseInt(input[2]);
			} else {
				System.out
						.print("Please select the song you want to download identified by its id: ");
				songID = Integer.parseInt(sc.nextLine().trim());
			}

			// download confirmation and issue download command
			System.out.println("You've selected the song \"" + songs[songID - 1]
					+ "\" by \"" + songs[songID - 1].getArtist() + "\" for download.");
			System.out.print("Confirm? ('y' to confirm): ");

			if (sc.nextLine().charAt(0) == 'y') {
				System.out.println("Download starting.. please standby.");
				song = Download.getSongBase16(songs[songID - 1]);
				System.out.println("Download ended..");

			} else {
				System.out.println("Operation cancelled.");
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid song id.");
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println("No input detected, operation cancelled.");
		}

		if (song == null) {
			System.out.println("No song has been added.");
			return songList;
		}

		Song[] newSongList = new Song[songList.length + 1];
		System.arraycopy(songList, 0, newSongList, 0, songList.length);
		newSongList[newSongList.length - 1] = song;
		return newSongList;
	}

	/**
	 * Download previously downloaded songs
	 * 
	 * @param library
	 *            the original song library
	 * @return a new array of Song which contain the original library and the newly
	 *         downloaded songs
	 */
	static Song[] sync(Song[] library) {
		System.out.println("Syncing..");
		library = Download.sync(library);
		System.out.println("Sync complete, your songs should "
				+ "be added to the Music Library");
		return library;
	}

	/**
	 * Playlist
	 */
	static void playlist() {
		File[] playlist = new File("./data/playlist").listFiles();

		if (playlist.length > 0) {
			System.out.println("=======Playlists=======");
			for (int i = 0; i < playlist.length; i++) {
				System.out.println(playlist[i].getName().replace(".txt", ""));
			}
			System.out.println("");

		} else {
			System.out.println("There are no playlists available.");
			System.out.println("Create one? Please type yes or no.");
			if (!sc.nextLine().split(" ")[0].equalsIgnoreCase("yes")) {
				return;
			}

			System.out.println("Please name the playlist: ");
			// should not be creating new playlist this way!
			new Playlist(sc.nextLine());
		}

		System.out.println("If you want to go back, type no else, type any to continue");
		if (sc.nextLine().split(" ")[0].equalsIgnoreCase("no")) {
			return;
		} else {
			System.out.println("Type the name of playlist which you want to manage: ");
			System.out
					.println("or create another playlist by typing new playlist's name: ");
			Playlist current_list = new Playlist(sc.nextLine());
			// Move this into another class!
			Playlist.PlaylistManager(current_list);
		}
	}

	/**
	 * Search by artist
	 */
	static void search() {
		System.out.println("Please type the name of the artist you want to search: ");
		String art = sc.nextLine();
		JTunes.Search_Manager(art);
	}

	/**
	 * Random shuffle
	 * Will work when a song is currently playing
	 */
	static void random() {
		System.out.println("Activating random shuffle, note that this will truly activate when a song is playing.");
		randomSong.randomSong();
	}
}