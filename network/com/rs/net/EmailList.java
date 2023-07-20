package com.rs.net;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rs.utilities.Utility;

/**
 * @author Seth Rogen
 * @since 7/6/2023
 * TODO: Change MAX EMAIL TO USERNAMES 
 */
public class EmailList {

	/**
	 * The max amount of emails a single player can have
	 */
	public static final int MAX_EMAIL_PER_USERNAME = 1;

	/**
	 * The map of emails that were received, with the key being the mac address and the value being the amount of
	 * starters received.
	 */
	private static final Map<String, Integer> emailMap = new HashMap<>();

	/**
	 * The directory of the file that will have starter information
	 */
	private static final String FILE_DIRECTORY = "data/playerEmails.txt";

	/**
	 * The object to synchronize functions with
	 */
	private static final Object LOCK = new Object();

	/**
	 * Loads all emails from the file in {@link #FILE_DIRECTORY} into the {@link #emailMap} sequentially
	 */
	public static void loadEmailFiles() {
		synchronized (LOCK) {
			emailMap.clear();
			for (String line : Utility.getFileText(FILE_DIRECTORY)) {
				String[] split = line.split("\t->\t");
				String email = split[0];
				Integer username = Integer.parseInt(split[1]);
				emailMap.put(email, username);
			}
		}
	}
	
	

	/**
	 * This method dumps all the emails from the {@link #emailMap} into the {@link #FILE_DIRECTORY} file.
	 */
	private static void dumpAllEmails() {
		synchronized (LOCK) {
			Utility.clearFile(FILE_DIRECTORY);
			for (Entry<String, Integer> entry : emailMap.entrySet()) {
				String email = entry.getKey();
				Integer username = entry.getValue();
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_DIRECTORY, true))) {
					writer.write(email + "\t->\t" + username);
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Finds out how many emails were received
	 *
	 * @param EmailAddress
	 */
	public static int ListRecieveEmail(String emailAddress) {
		synchronized (LOCK) {
			Integer userName = emailMap.get(emailAddress);
			if (userName == null) {
				return 0;
			}
			return userName;
		}
	}

	/**
	 * Finds out if the email is in use or not.
	 * @param email address
	 */
	public static boolean emailAlreadyUsed(String emailAddress) {
		synchronized (LOCK) {
			int startersReceived = ListRecieveEmail(emailAddress);
			System.out.println("MAx Recieved " + startersReceived);
			return startersReceived >= MAX_EMAIL_PER_USERNAME;
		}
	}

	/**
	 * Inserts the mapAddress to the {@link #emailMap} and reloads the map
	 *
	 * @param macAddress
	 * 		The address to insert.
	 */
	public static void addEmailtoList(String emailAddress) {
		synchronized (LOCK) {
			emailMap.put(emailAddress, ListRecieveEmail(emailAddress) + 1);
			dumpAllEmails();
			loadEmailFiles();
		}
	}

}
