package answerProcessing.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class SimpsonsIOUtil {

	private final static String ENCODING = "UTF-8";
	private final static String COL_SEPARATE = "\t";

	/**
	 * Initialize reader for resource with given name
	 * 
	 * @param filename
	 *            resource name
	 * @return reader
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	private static BufferedReader getReader(String filename)
			throws UnsupportedEncodingException, FileNotFoundException {
		InputStream in = SimpsonsIOUtil.class.getResourceAsStream(filename);
		if (in != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, ENCODING));
			return reader;
		} else
			return new BufferedReader(new InputStreamReader(
					new FileInputStream(new File("src/main/resources"
							+ filename)), ENCODING));
	}

	/**
	 * Read episode-links from file
	 * 
	 * @param episodesFile
	 * @return list of episode urls
	 */
	public static List<String> initializeDocumentURLs(String episodesFile) {
		return readEpisodesList_col(episodesFile, 4);
	}

	public static HashMap<Integer, String> initializeDocumentMapURLs(
			String episodesFile) {
		BufferedReader reader = null;
		HashMap<Integer, String> urls = new HashMap<Integer, String>();
		try {
			reader = getReader(episodesFile);
			String zeile = "";
			while ((zeile = reader.readLine()) != null) {
				urls.put(Integer.valueOf(zeile.split(COL_SEPARATE)[0]),
						zeile.split(COL_SEPARATE)[2]);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return urls;
	}

	/**
	 * Reads Simpsons entity lists in tsv format (e.g. locations, persons) with
	 * one entity-name per line
	 * 
	 * @param filename
	 *            path to the read file
	 * @return list of read entities
	 */
	public static List<String> readEntityList(String filename) {
		List<String> entities = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = getReader(filename);
	
			String entityName;
			while ((entityName = reader.readLine()) != null) {
				entities.add(entityName.trim());
			}
	
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return entities;
	}

	/**
	 * Reads episode name of episode list
	 * 
	 * @param episodesFile
	 * @return list of episode names
	 */
	public static List<String> readEpisodeList(String episodesFile) {
		List<String> result= new ArrayList<String>();
		List<String> names= readEpisodesList_col(episodesFile, 2);
		List<String> numbers= readEpisodesList_col(episodesFile, 1);
		int season =0;
		for(int i=0; i<names.size(); i++){
			if(Integer.valueOf(numbers.get(i))==1) season++;
		result.add(""+(i+1)+" - Season "+season+" - Episode "+numbers.get(i)+" - "+names.get(i));	
		}
		return result;
	}

	/**
	 * Reads column of episodes file
	 * 
	 * @param episodesFile
	 * @param column
	 * @return list of a column in episodes file
	 */
	private static List<String> readEpisodesList_col(String episodesFile,
			int column) {
		BufferedReader reader = null;
		List<String> urls = new ArrayList<String>();
		try {
			reader = getReader(episodesFile);
			String line = "";
			while ((line = reader.readLine()) != null) {
				urls.add(line.split(COL_SEPARATE)[column].trim());
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return urls;
	}
}
