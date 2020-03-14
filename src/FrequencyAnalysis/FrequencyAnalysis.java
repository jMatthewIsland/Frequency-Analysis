package FrequencyAnalysis;
import java.io.*;
import java.util.ArrayList;

//
//main.cpp
//Cypher Decoder
//
//Created by Matthew Island on 7/21/15.
//Copyright © 2015 Matthew Island. All rights reserved.
//

//
//Transferred from C++ to Java by Matthew Island on 3/10/20
//

//
//Test cipher: 
//

public class FrequencyAnalysis {
	
	static char [] normAlph = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	static int [] sortedEncryptIndex = new int[26];
	static int [] points = new int[26], pointsIndex = new int[26];
	static char[] sortedNormalLetters = {'e', 't', 'a', 'o', 'i', 'n', 's', 'h', 'r', 'd', 'l', 'c', 'u', 'm', 'w', 'f', 'g', 'y', 'p', 'b', 'v', 'k', 'j', 'x', 'q', 'z'};
	static int[] sortedNormalIndex = {4, 19, 0, 14, 8, 14, 18, 7, 17, 3, 11, 2, 20, 12, 22, 5, 6, 24, 15, 1, 21, 10, 9, 23, 16, 25};
	static double[] sortedNormalFreq = {12.702, 9.056, 8.167, 7.507, 6.966, 6.749, 6.327, 6.094, 5.987, 4.253, 4.025, 2.782, 2.758, 2.406, 2.360, 2.228, 2.015, 1.974, 1.929, 1.492, 0.978, 0.772, 0.153, 0.150, 0.095, 0.074};
	static ArrayList<String> threeLetterWords = new ArrayList<String>();
	static ArrayList<Integer> threeLetterFreq = new ArrayList<Integer>(); 
	
	public static void main(String[] args) {
		String message, decrypted;
		
		for (int i = 0; i < sortedEncryptIndex.length; i ++) {
			sortedEncryptIndex[i] = i;
		}
		
		for (int i = 0; i < points.length; i++) {
			points[i] = 0;
			pointsIndex[i] = i;
		}
		
		//double [] sortedNormFreq, sortedNormIndex = new double[26];
		double [] sortedEncryptFreq = new double[26];
		double [] encryptAlphFreq = new double[26]; //alphabetical		
		
		message = readMessage();
		
		encryptAlphFreq = analyzeFrequency(message);
		
		sortedEncryptFreq = sortFrequency(encryptAlphFreq);
		
		points = addFrequencyPoints(sortedEncryptFreq, sortedEncryptIndex, message);
		
		//threeWordAnalysis();
		
		decrypted = decryptMessage(message);
		
		writeMessage(decrypted);
		
	}
	
	/**
	 * This method takes the message, sorts out what is a valid letter, and records the frequency of each letter in an
	 * alphabetically indexed array.
	 * 
	 * @param message : The encrypted message
	 * @return encryptedAlphabetFrequency : An array, in alphabetical order, containing the percentile frequency of each letter in the encrypted message
	 */
	
	public static double[] analyzeFrequency(String message) {
		char targetLetter;
		int targetIndex;
		boolean flag, illegalChar;
		double [] encryptedAlphabetFrequency = new double [26];//alphabetized
		char [] illegalCharacters = {' ','.','!','?', ',', ';', '-', '(', ')' };
		String trimmedMessage = "";
		int letterCounter = 0;
		String wordTracker = "";
		int temp;
		
		message = message.toLowerCase();
		for (int i = 0; i < 26; i ++) {
			encryptedAlphabetFrequency[i] = 0;
		}
		for (int i = 0; i < message.length(); i ++) {
			targetIndex = -1;
			targetLetter = message.charAt(i);
			flag = false;
			illegalChar = false;
				for (int j = 0; j < illegalCharacters.length; j++) {
					if(targetLetter == illegalCharacters[j]) {
						illegalChar = true;
					}
				}
				for (int j = 0; j < normAlph.length && flag == false && illegalChar == false; j ++) {
					if (targetLetter == normAlph[j]) {
						targetIndex = j;
						trimmedMessage += targetLetter;
						flag = true;
					}
				}
				if (targetIndex != -1) {
					encryptedAlphabetFrequency[targetIndex] += 1;
					letterCounter ++;
					wordTracker += targetLetter;
				}
				if (targetLetter == ' ' || i + 1 == message.length()) {
					if (letterCounter == 3) {
						if (threeLetterWords.contains(wordTracker)) {
							temp = threeLetterFreq.get(threeLetterWords.indexOf(wordTracker));
							temp ++;
							threeLetterFreq.set(threeLetterWords.indexOf(wordTracker), temp);
						} else {
							threeLetterWords.add(wordTracker);
							threeLetterFreq.add(1);
						}
					}
					letterCounter = 0;
					wordTracker = "";
				}
			
		}
		for (int i = 0; i < encryptedAlphabetFrequency.length; i++) {
			encryptedAlphabetFrequency[i] = (encryptedAlphabetFrequency[i]/trimmedMessage.length()) * 100;
		}
		
		return encryptedAlphabetFrequency;
	}
	
	/**
	 * This method sorts the given array by frequency. To track the letters associated with each frequency, the global 
	 * parallel array sortedEncryptIndex maintains the order of the letters, ordered from most frequen to least.
	 * 
	 * @param encryptAlphFreq : An array that contains the frequency of each letter indexed in alphabetical order
	 * @return sortedEncryptFreq : An array that contains the frequency of each letter, indexed by frequency (largest to smallest)
	 */
	
	public static double[] sortFrequency(double[] encryptAlphFreq) {
		boolean sorted = false;
		double [] sortedEncryptFreq = encryptAlphFreq;
		double temp;
		int inttemp;
		while (!sorted) {
			sorted = true;
			for (int i = 0; i < sortedEncryptFreq.length - 1; i ++) {
				if (sortedEncryptFreq[i] < sortedEncryptFreq[i+1]) {
					temp = sortedEncryptFreq[i];
					sortedEncryptFreq[i] = sortedEncryptFreq[i+1];
					sortedEncryptFreq[i + 1] = temp;
					inttemp = sortedEncryptIndex[i];
					sortedEncryptIndex[i] = sortedEncryptIndex[i+1];
					sortedEncryptIndex[i+1] = inttemp;
					sorted = false;
				}
			}
		}
		return sortedEncryptFreq;
	}
	
	/**
	 * This method converts the frequency of each letter to points, based on how close it is to the 
	 * normal frequency of the English alphabet. The longer the length of the message, the more points 
	 * will be awarded. Points will go to the alphabetized points array, and the indexes of their
	 * corresponding encrypted letters is stored in the parallel array pointsIndex
	 * 
	 * @param sortedEncryptFreq : The frequency of each letter in the encrypted message, sorted by frequency from greatest to least
	 * @param sortedEncryptIndex : The alphabetical index of each frequency from greatest to least. Parallel to sortedEncryptFreq
	 * @param message : The encrypted message
	 * @return points : an integer array that contains the points of each letter
	 */
	
	public static int[] addFrequencyPoints(double[] sortedEncryptFreq, int[] sortedEncryptIndex, String message) {
		double displacement;
		for(int i = 0; i < sortedEncryptFreq.length; i ++){
			displacement = Math.abs(sortedNormalFreq[i]-sortedEncryptFreq[i]);
			points[sortedNormalIndex[i]] = (int) (message.length() - displacement);
			pointsIndex[sortedNormalIndex[i]] = sortedEncryptIndex[i];
		}
		return points;
	}
	
	/**
	 * 
	 * @param message : The encrypted message
	 * @return decrypted : The decrypted message based on the points system
	 */
	
	public static String decryptMessage(String message) {
		String decrypted = "";
		boolean validChar;
		int encryptedLetterIndex = 0;
		for (int i = 0; i < message.length(); i++) {
			validChar = false;
			for (int j = 0; j < normAlph.length && validChar == false; j ++) {
				if (message.charAt(i) == normAlph[j]) {
					validChar = true;
					encryptedLetterIndex = j;
				}
			}
			if (validChar) {
				decrypted += normAlph[pointsIndex[encryptedLetterIndex]];
			} else {
				decrypted += message.charAt(i);
			}
		}
		return decrypted;
	}
	
	/**
	 * This method reads the encrypted method from the input file, message.txt. If there is
	 * no file named message.txt, a new input file named message.txt will be created, and the 
	 * application will terminate.
	 * @return message : A string containing the encrypted message.
	 */
	public static String readMessage() {
		File importFile = new File("message.txt");;
		FileReader fileReader;
		BufferedReader reader;
		String message = "";
		String line;
		try {
			fileReader = new FileReader(importFile);
			reader = new BufferedReader(fileReader);
			while((line = reader.readLine()) != null) {
				message += line;
			}
			
			reader.close();
			fileReader.close();
			
		} 
		catch (FileNotFoundException e) {
			try {
				System.out.println("Error: no import file. Creating file...");
				importFile.createNewFile();
				System.out.println("File created");
			} catch (IOException er) {
				System.err.println(er);
			}
		}
		catch (IOException e) {
			System.err.println(e);
		}
		
		return message;
	}
	
	/**
	 * This method writes the decrypted message to a file named output.txt. If there is no
	 * file named output.txt, a new file named output.txt will be created and the decrypted 
	 * message will be written to that file.
	 * @param message : A string containing the decrypted message.
	 */
	public static void writeMessage(String message) {
		File exportFile = new File ("output.txt");;
		FileWriter fileWriter;
		BufferedWriter writer;
		
		try {
			fileWriter = new FileWriter(exportFile);
			writer = new BufferedWriter(fileWriter);
			
			writer.write(message);
			
			writer.close();
			fileWriter.close();
		} 
		
		catch (FileNotFoundException e) {
			try {
				System.out.println("Error: no export file. Creating file...");
				exportFile.createNewFile();
				System.out.println("File created");
			} catch (IOException er) {
				System.err.println(er);
			}
		}
		
		catch (IOException e) {
			System.err.println(e);
		}
		
	}
	
	/**
	 * To improve the accuracy of the Frequency Analysis, this method looks at the number
	 * of three letter words in the encrypted message, and identifies the most frequently used
	 * of those words. That word, presumably, is the word "the". This method switches the 
	 * letters in the encrypted "the" to match the corresponding decrypted letters 't', 'h', 
	 * and 'e'. STILL IN DEVELOPMENT
	 */
	public static void threeWordAnalysis() {
		String encryptedThe;
		int[] normalThe = {2, 8, 0};
		int max = -1;
		int maxIndex = -1;
		int encryptedIndex = -1;
		boolean flag = false;
		int letterIndex = -1;
		
		for (int i = 0; i < threeLetterFreq.size(); i ++) {
			if (threeLetterFreq.get(i) > max) {
				max = threeLetterFreq.get(i);
				maxIndex = i;
			}
		}
		encryptedThe = threeLetterWords.get(maxIndex);
		for (int i = 0; i < encryptedThe.length(); i++) {
			flag = false;
			for (int k = 0; k < normAlph.length; k++) {
				if (normAlph[k] == encryptedThe.charAt(i)) {
					letterIndex = k;
				}
			}
			for(int j = 0; j < pointsIndex.length && !flag; j++) {
				if(pointsIndex[j] == letterIndex) {
					encryptedIndex = j;
					flag = true;
				}
			}
			
			if (encryptedIndex != normalThe[i]) {
				points = pointSwap(encryptedIndex, 1);
			}
			
		}
		
	}
	
	/**
	 * This method swaps the values of the points array at indexOne and indexTwo. The corresponding
	 * values in the parallel array, pointsIndex, will also be swapped at indexOne and indexTwo. 
	 * STILL IN DEVELOPMENT, NEEDS TESTING
	 * @param indexOne : the index of one value to be swapped with indexTwo 
	 * @param indexTwo : the index of one value to be swapped with indexOne
	 * @return returnArray : an updated copy of points
	 */
	public static int[] pointSwap(int indexOne, int indexTwo) {
		int tempIndex;
		int tempValue;
		int[] returnArray = points;
		tempValue = returnArray[indexOne];
		tempIndex = points[indexOne];
		returnArray[indexOne] = returnArray[indexTwo];
		points[indexOne] = points[indexTwo];
		returnArray[indexTwo] = tempValue;
		points[indexTwo] = tempIndex;
		return returnArray;
	}
	
}
