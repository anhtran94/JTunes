package edu.toronto.csc207.noname;

import java.io.File;

/**
 * Song object template
 */
class Song {
	private String name;
	private String artist;
	private File midiFile;
	private String hash;	// like id

	/**
	 * Lite Song instance creation
	 * @param hash an unique String
	 * @param midiLocation a String pointing to location of song file
	 */
	public Song(String hash, String midiLocation) {
		this(hash, midiLocation, "random placeholder");
		// use filename without extension as song's name
		if (midiLocation.lastIndexOf('/') >= 0) {
			this.setName(midiLocation.substring(midiLocation.lastIndexOf('/') + 1,
					midiLocation.lastIndexOf('.')));
		} else {
			this.setName(midiLocation.substring(0, midiLocation.lastIndexOf('.')));
		}
	}

	/**
	 * Overload Song instantiation without artist
	 * @param hash an unique String
	 * @param midiLocation a String pointing to location of song file
	 * @param name a simple String to identify the song\
	 */
	public Song(String hash, String midiLocation, String name) {
		this(hash, midiLocation, name, "");
	}

	/**
	 * Overload Song instantiation method
	 * @param hash an unique String
	 * @param midiLocation a String pointing to location of song file
	 * @param name a simple String to identify the song
	 * @param artist the artist or group name as a String
	 */
	public Song(String hash, String midiLocation, String name, String artist) {
		this.setName(name);
		this.setArtist(artist);
		this.setMidiFile(new File(midiLocation));
		this.setHash(hash);
	}

	/**
	 * override to display the name of this object
	 */
	public String toString(){
		return this.name;
	}

	/**
	 * Output the unique identifier
	 * @return the hash String
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Set the unique identifier
	 * @param hash the unique String
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * Retrieve the song's name
	 * @return the name String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the song's name
	 * @param name String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieve song's artist
	 * @return artist/group name that is a String
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * Set song's artist
	 * @param artist String
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * Retrieve the midi file
	 * @return a File object that holds a midi
	 */
	public File getMidiFile() {
		return midiFile;
	}

	/**
	 * Set the midi file
	 * @param midiFile a File object containing the midi
	 */
	public void setMidiFile(File midiFile) {
		this.midiFile = midiFile;
	}
}
