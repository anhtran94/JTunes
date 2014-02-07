package edu.toronto.csc207.noname;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

/**
 * Midi Player
 */
class Player {

	// midi synthesizer
	private static Synthesizer synth;
	private static Sequencer seq;
	private static Sequence s;

	/**
	 * Outputs the sound of a midi file
	 * 
	 * @param source
	 *            path to midi file
	 */
	public static void play(File source) {
		try {
			// free existing resource
			flush();

			synth = MidiSystem.getSynthesizer();
			seq = MidiSystem.getSequencer();
			s = MidiSystem.getSequence(source);

			synth.open();
			seq.open();
			seq.setSequence(s);
			seq.start();

		} catch (MidiUnavailableException e) {
			System.out.println("Midi Unavailable :(\n"
					+ "Your hardware may not support midi playback.");
		} catch (FileNotFoundException e) {
			System.out.println("Sound File Not Found!");
		} catch (InvalidMidiDataException e) {
			System.out
					.println("Non standard midi file given and it may be corrupt!\n"
							+ "JTunes player do not have capability to recover such file.");
			seq = null;
		} catch (IOException e) {
			System.out.println("Cannot play this midi file, sorry.");
		}
	}

	/**
	 * Pause the sound output
	 * 
	 * @return true if sound output stops, false if initially not outputting
	 */
	public static boolean pause() {
		if (isPlaying()) {
			seq.stop();
			return true;
		}
		return false;
	}

	/**
	 * Continue to output sound
	 * 
	 * @return true if sound output resumes, false if its already outputting (not paused)
	 */
	public static boolean resume() {
		if (!isPlaying()) {
			seq.start();
			return true;
		}
		return false;
	}

	/**
	 * Stop the sound output and reset position to the beginning (0:0)
	 * 
	 * @return true if stopped and position reset, false if sound output is uninitialized
	 */
	public static boolean stop() {
		if (seq != null) {
			seq.stop();
			seq.setTickPosition(0);
			return true;
		}
		return false;
	}

	/**
	 * Unset objects required to play sounds so it absolutely halts all sound output
	 */
	public static void flush() {
		if (seq != null) {
			seq.close();
		}
		if (synth != null) {
			synth.close();
		}
	}

	/**
	 * Check whether this class is outputting sound
	 * 
	 * @return true if currently outputting sound, false otherwise
	 */
	public static boolean isPlaying() {
		return seq != null && seq.isRunning();
	}

}
