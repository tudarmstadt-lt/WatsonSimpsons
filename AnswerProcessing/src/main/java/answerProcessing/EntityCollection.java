package answerProcessing;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import answerProcessing.utils.SimpsonsIOUtil;

public class EntityCollection {
	// entity lists
		private static final String FEMALE_CHAR_FILE = "/female_characters.tsv";
		private static final String MALE_CHAR_FILE = "/male_characters.tsv";
		private static final String LOCATION_FILE = "/locations.tsv";
		private static final String ANIMAL_CHAR_FILE = "/animal_characters.tsv";
		private static final String ANIMAL_FILE = "/animals.tsv";
		private static final String FAMILY_FILE = "/families.tsv";
		private static final String COLOR_FILE = "/colors.tsv";
		private static final String ORGANIZATION_FILE = "/organizations.tsv";
		private static final String EPISODES_FILE = "/list_of_simpsons_episodes_with_article_id_sorted_by_name-1.csv";
		private final static String list_of_simpsons_articles ="/list_of_simpsons_articles_with_url.tsv";
		private final static String list_of_simpsons_episodes = "/list_of_simpsons_episodes_with_article_id_sorted_by_name-1.csv";

		
		// list of episodes with url (simpsons.wikia)
		private static List<String> urls_episodes;
		// map of simpsons.wikia articles with url
		private static HashMap <Integer,String> urls;
		private static List<String> femaleCharacters;
		private static List<String> maleCharacters;
		private static List<String> locations;
		private static List<String> animals;
		private static List<String> animalCharacters;
		private static List<String> families;
		private static List<String> organizations;
		private static List<String> colors;
		private static List<String> episodes;

		/**
		 * Read all entities from files
		 */
		public static void initEntityInfos() {
			femaleCharacters = SimpsonsIOUtil.readEntityList(FEMALE_CHAR_FILE);
			maleCharacters = SimpsonsIOUtil.readEntityList(MALE_CHAR_FILE);
			locations = SimpsonsIOUtil.readEntityList(LOCATION_FILE);
			animals = SimpsonsIOUtil.readEntityList(ANIMAL_FILE);
			animalCharacters = SimpsonsIOUtil.readEntityList(ANIMAL_CHAR_FILE);
			families = SimpsonsIOUtil.readEntityList(FAMILY_FILE);
			organizations = SimpsonsIOUtil.readEntityList(ORGANIZATION_FILE);
			colors = SimpsonsIOUtil.readEntityList(COLOR_FILE);
			episodes = SimpsonsIOUtil.readEpisodeList(EPISODES_FILE);
			initUrlLists();
		}
		
		   /**
	     * Initializes the URLs of the orginalfiles
	     */
		private static  void initUrlLists() {
			urls_episodes=SimpsonsIOUtil.initializeDocumentURLs(list_of_simpsons_episodes);
			urls=SimpsonsIOUtil.initializeDocumentMapURLs(list_of_simpsons_articles );		
			}
		/**
		 * gets the URL of the original file
		 * @param title	
		 * title of the Simpsons.Wikia articles
		 * @return	URL of the original file
		 */
		public static String retrieveMetadata(String title) {
			if ( title.startsWith("[")){			
				Scanner scanner = new Scanner(title.substring(1).replaceFirst("]", ""));
				int i = scanner.nextInt();
				scanner.close();
				return urls.get(i);
			}
				
			if (title.contains("Season") && title.contains("Episode"))
		       {Scanner scanner = new Scanner(title);
				int i = scanner.nextInt();
				scanner.close();
				return urls_episodes.get(i-1);
			} else {
				title = title.split(" : ")[0];
				title = title.replace(" ", "_");
				return  "http://simpsons.wikia.com/wiki/".concat(title);
			}			
			
		}
		public static List<String> getFemaleCharacters() {
			return femaleCharacters;
		}

		public static List<String> getMaleCharacters() {
			return maleCharacters;
		}

		public static List<String> getLocations() {
			return locations;
		}

		public static List<String> getAnimals() {
			return animals;
		}

		public static List<String> getAnimalCharacters() {
			return animalCharacters;
		}

		public static List<String> getFamilies() {
			return families;
		}

		public static List<String> getOrganizations() {
			return organizations;
		}

		public static List<String> getColors() {
			return colors;
		}

		public static List<String> getEpisodes() {
			return episodes;
		}
		public static List<String> getUrls_episodes() {
			return urls_episodes;
		}

		public static HashMap<Integer, String> getUrls() {
			return urls;
		}

}
