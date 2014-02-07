package edu.toronto.csc207.noname;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A static class for local and online file handling
 */
class Download {

	/**
	 * A custom exception to be thrown when success status is not returned in XML
	 */
	private static class BadResponse extends Exception {
	}

	// XML
	private static DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
			.newInstance();
	private static DocumentBuilder docBuilder = null;
	private static Document doc = null;

	/**
	 * Load song(s) from local file that is written in a CSV like format.
	 * 
	 * @param path
	 *            the path of the file to be parsed, file location will be in 'src/'
	 *            directory when './' is prefixed
	 * @return an array of Song object
	 */
	static Song[] initialize(String path) {

		Scanner sc = null;

		try {
			sc = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			System.out.println("Data File Not Found!");
			System.exit(1); // error force quit
		}

		// Empty file check
		if (!sc.hasNext()) {
			return new Song[] {};
		}
		
		String content[] = sc.useDelimiter("\\Z").next().split("\n");
		sc.close();

		Song[] songs = new Song[content.length];

		// used to temporarily store songs info
		String[] info;

		for (int songNum = 0; songNum < songs.length; songNum++) {
			info = content[songNum].trim().split(", ");

			if (info.length == 1) {
				songs[songNum] = new Song("?", info[0]);
			} else if (info.length == 2) {
				songs[songNum] = new Song(info[0], info[1]);
			} else if (info.length == 3) {
				songs[songNum] = new Song(info[0], info[1], info[2]);
			} else if (info.length == 4) {
				songs[songNum] = new Song(info[0], info[1], info[2], info[3]);
			}
		}

		return songs;
	}

	/**
	 * Helper method used to load XML document into 'doc' variable
	 * 
	 * @param url
	 *            path to XML document
	 * @return true if method execute with no exception detected, false otherwise
	 */
	static boolean loadXML(String url) {

		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new URL(url).openStream());

			if (!statusOK()) {
				throw new BadResponse();
			}
		} catch (BadResponse e) {
			System.out.println("Bad response from JTunes cloud!");
			return false;
		} catch (MalformedURLException e) {
			System.out.println("Bad response from JTunes cloud!");
			return false;
		} catch (SAXException e) {
			System.out.println("Cannot parse XML.");
			return false;
		} catch (ParserConfigurationException e) {
			System.out.println("Cannot parse XML.");
			return false;
		} catch (IOException e) {
			System.out.println("An error has occured when connecting to jTunes cloud.");
			return false;
		}

		return true;
	}

	/**
	 * Helper method used to check response status of XML documents
	 * 
	 * @return true if success attribute on root element contains the true value, false
	 *         otherwise
	 */
	static boolean statusOK() {
		// Assume success status will always be on root node, and valid response values
		return Boolean.parseBoolean(doc.getDocumentElement().getAttribute("success"));
	}

	/**
	 * Get a list of songs on jTunes cloud
	 * 
	 * @return an array of Song object
	 */
	static Song[] browseCloud() {

		if (!loadXML("http://greywolf.cdf.toronto.edu:1337/noname/listSongs")) {
			return new Song[] {};
		}

		// list of XML song nodes
		NodeList songList = doc.getElementsByTagName("song");
		Element songElement;
		Song[] songs = new Song[songList.getLength()];

		// create song object from given data
		for (int i = 0; i < songList.getLength(); i++) {
			songElement = (Element) songList.item(i);

			// this song object is for displaying only, as no sound source is associated.
			songs[i] = new Song(songElement.getTextContent().trim(), "",
					songElement.getAttribute("name"), songElement.getAttribute("artist"));
		}

		return songs;
	}

	/**
	 * Get a list of user's previously downloaded songs from jTunes
	 * 
	 * @return an array of Song object
	 */
	static Song[] browseCloudwithID() {

		if (!User.isLoggedin()) {
			System.out
					.println("User authentication required, No songs have been added.\n");
			return new Song[] {};
		}

		if (!loadXML("http://greywolf.cdf.toronto.edu:1337/noname/listSongs?email="
				+ User.getEmail() + "&password=" + User.getPassword())) {
			return new Song[] {};
		}

		// this must be above, doc will be overwritten by next cloud call.
		NodeList songList = doc.getElementsByTagName("song");
		Song[] songs = new Song[songList.getLength()];

		// reuse previous method to get a list of jTunes cloud song
		Song[] list = Download.browseCloud();
		String tempHash; // temp var

		// match the hash of user's song to a list of songs on the
		// jTunes cloud and create a song object when a match is found
		for (int songIndex = 0; songIndex < songs.length; songIndex++) {
			tempHash = ((Element) songList.item(songIndex)).getTextContent().trim();
			for (int listIndex = 0; listIndex < list.length; listIndex++) {
				if (tempHash.equalsIgnoreCase(list[listIndex].getHash())) {
					songs[songIndex] = new Song(list[listIndex].getHash(), "",
							list[listIndex].getName(), list[listIndex].getArtist());
				}
			}
		}

		return songs;
	}

	/**
	 * Redownload songs which user has previously downloaded from jTunes cloud
	 * 
	 * @param library
	 *            the music library Song array
	 * @return an array of Song object which contains the original songs in library and
	 *         also the new songs added
	 */
	static Song[] sync(Song[] library) {

		if (!User.isLoggedin()) {
			System.out
					.println("User authentication required, No songs have been added.\n");
			return library;
		}

		// Convert to an arraylist for easier song addition
		List<Song> songs = new ArrayList<Song>(Arrays.asList(library));
		Song[] mylist = browseCloudwithID();

		boolean found;
		for (int mylistIndex = 0; mylistIndex < mylist.length; mylistIndex++) {
			found = false;
			for (int libraryList = 0; libraryList < songs.size(); libraryList++) {
				if (mylist[mylistIndex].getHash().equalsIgnoreCase(
						songs.get(libraryList).getHash())) {
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println("Downloading: " + mylist[mylistIndex]);
				songs.add(getSongBase16(mylist[mylistIndex]));
			}
		}

		// convert back to an simple Song array when returned
		return songs.toArray(new Song[songs.size()]);
	}

	/**
	 * Download song from jTunes cloud in base 16 encoded format
	 * 
	 * @param song
	 *            a Song object containing basic info but no file path association yet
	 * @return the original Song object passed in with file association if successful,
	 *         otherwise a null object
	 */
	static Song getSongBase16(Song song) {

		// check if user is logged in, since we need to provide user info on request
		if (!User.isLoggedin()) {
			System.out.println("User authentication required.");
			return null;
		}

		// store file with hash (id) as filename
		File file = new File("./data/song/" + song.getHash() + ".mid");

		if (file.exists()) {
			System.out.println("This song already exist in your music library, "
					+ "go check it out.");
			return null;
		}

		// downloading in base16 format as data is outputted on XML
		if (!loadXML("http://greywolf.cdf.toronto.edu:1337/noname/getSong?email="
				+ User.getEmail() + "&password=" + User.getPassword() + "&songid="
				+ song.getHash())) {
			return null;
		}

		Element raw = (Element) doc.getElementsByTagName("rawdata").item(0);
		if (!raw.getAttribute("encoding").equalsIgnoreCase("base16")) {
			System.out.println("Unexpected Encoding");
			return null;
		}

		// transcoding
		byte[] data = Base16Encoder.decode(raw.getTextContent());

		// generate song file
		OutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			fileOut.write(data);
			fileOut.flush();
			fileOut.close();

		} catch (IOException e) {
			System.out
					.println("Error, the file handle limit for this program may have been reached.\n"
							+ "Please restart the program and try again.");
			return null;
		}

		// add song info into music library
		File database = new File("./data/library.txt");
		BufferedWriter out;

		try {
			out = new BufferedWriter(new FileWriter(database, true));
			out.newLine();
			out.write(song.getHash() + ", ./data/song/" + song.getHash() + ".mid, "
					+ song.getName() + ", " + song.getArtist());
			out.close();
		} catch (IOException e) {
			// either song won't get added or database will mess up if written halfway..
			System.out.println("Failed to update database, "
					+ "you may need to add downloaded song to database manually.");
			return null;
		}

		song.setMidiFile(file);
		return song;
	}
}
