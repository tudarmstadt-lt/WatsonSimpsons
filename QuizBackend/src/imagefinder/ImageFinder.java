package imagefinder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;

import imagefinder.types.AllimagesQuery;
import imagefinder.types.Image;
import imagefinder.types.ImagesOnPage;
import imagefinder.types.Imagetitle;
import imagefinder.types.PageScaledImage;

public final class ImageFinder {

    private final static String wikiURL = "https://simpsonswiki.com/w/api.php?action=query&format=json&";
    private final static String allImagesQuery = "list=allimages&ailimit=1&aiprop=url|size&aifrom=";
    private final static String imagesOnArticleQuery = "prop=images&imlimit=20&titles=";
    private final static String thumbImageQuery = "prop=imageinfo&iiprop=url&iiurlwidth=";

    /**
     * get 20 images of an episode article (simpsonswiki)
     *
     * @param episode
     * @return a list of images
     */
    private static ArrayList<Imagetitle> getEpisodeImage(String episode) {
        String query = wikiURL.concat(imagesOnArticleQuery).concat(episode);
        ArrayList<Imagetitle> images = new ArrayList<Imagetitle>();
        ImagesOnPage page = ImageBackendCommunicator.getImagesOnArticleResponse(query);

        if (page != null && page.getImages() != null) {
            images = page.getImages();

            // remove some icons (flags, imdb...)
            images.remove(new Imagetitle("File:Real-world.png"));
            images.remove(new Imagetitle("File:Donut Homer.png"));
            images.remove(new Imagetitle("File:The Saga of Carl - title screen.png"));
            images.remove(new Imagetitle("File:The Simpsons TV.png"));
            images.remove(new Imagetitle("File:Clip show.png"));
            images.remove(new Imagetitle("File:To-do list.png"));
            images.remove(new Imagetitle("File:Flag of Quebec.svg.png"));
            images.remove(new Imagetitle("File:France.png"));
            images.remove(new Imagetitle("File:Germany.png"));
            images.remove(new Imagetitle("File:Hispanic America.gif"));
            images.remove(new Imagetitle("File:Homer icon.gif"));
            images.remove(new Imagetitle("File:Italy Flag.png"));
            images.remove(new Imagetitle("File:Spain flag.png"));
            images.remove(new Imagetitle("File:IMDb favicon.png"));
            images.remove(new Imagetitle("File:800px-Flag of the United Kingdom.svg.png"));
            images.remove(new Imagetitle("File:Blank.png"));
            images.remove(new Imagetitle("File:Repair.png"));
            images.remove(new Imagetitle("File:Brasil Flag.png"));
            images.remove(new Imagetitle("File:Flag of Australia.png"));
            images.remove(new Imagetitle("File:Flag of the United States.png"));
            images.remove(new Imagetitle("File:Youtube favicon.png"));
            images.remove(new Imagetitle("File:BartThruTownEarlyS2.png"));
            images.remove(new Imagetitle("File:Semi-Lock.png"));
            images.remove(new Imagetitle("File:The Simpsons Archive favicon.png"));
            images.remove(new Imagetitle("File:Wikidump.png"));
            images.remove(new Imagetitle("File:Coldplay.jpg"));

            HashSet<Imagetitle> scriptImages = new HashSet<>();

            // remove script images
            for (int i = 0; i < images.size(); i++) {
                if (images.get(i).getTitle().contains(" scene")
                        || images.get(i).getTitle().contains(" Scenes")
                        || images.get(i).getTitle().contains("Script")
                        || images.get(i).getTitle().contains("script")) {
                    scriptImages.add(new Imagetitle(images.get(i).getTitle()));
                }
            }
            images.removeAll(scriptImages);

            if (images.size() > 1)
                // Future Simpsons image if there are no other images
                images.remove(new Imagetitle(
                        "File:Days of Future Future Simpsons House.png"));
        }

        return images;
    }

    /**
     * finds scaled episode image via Simpsonswiki api
     *
     * @param scaledWidth
     * @param episode
     * @return
     * @throws UnsupportedEncodingException
     */
    private static PageScaledImage getScaledImages(int scaledWidth, String episode)
            throws UnsupportedEncodingException {
        String queryURL = wikiURL.concat(thumbImageQuery)
                .concat(scaledWidth + "&titles=")
                .concat(URLEncoder.encode(episode, "UTF8"));
        return ImageBackendCommunicator.getThumbImageResponse(queryURL);
    }

    /**
     * create thumbImage of an Imagetitle
     *
     * @param scaledWidth
     * @param image
     * @return
     * @throws UnsupportedEncodingException
     */
    private static Image getThumbimage(int scaledWidth, Imagetitle image)
            throws UnsupportedEncodingException {
        PageScaledImage page = getScaledImages(scaledWidth, image.getTitle());

        if (page != null && page.getImages() != null && !page.getImages().isEmpty()) {
            int width = page.getImages().get(0).getThumbwidth();
            int height = page.getImages().get(0).getThumbheight();
            String descriptionurl = page.getImages().get(0).getDescriptionurl();
            String url = page.getImages().get(0).getUrl();
            String thumburl = page.getImages().get(0).getThumburl();
            String name = page.getTitle().replace("File:", "");
            int size = width * height;

            return new Image(name, size, width, height, url, descriptionurl,
                    thumburl, page.getNs(), page.getTitle());
        } else
            return new Image(
                    "Simpsons_family.png",
                    177017,
                    300,
                    438,
                    "https://simpsonswiki.com/w/images/0/02/Simpsons_family.png",
                    "https://simpsonswiki.com/wiki/File:Simpsons_family.png",
                    "https://simpsonswiki.com/w/images/0/02/Simpsons_family.png",
                    6, "File:Simpsons family.png");
    }

    /**
     * adds thumburl to an Image
     *
     * @param scaledWidth
     * @param image
     * @return
     * @throws UnsupportedEncodingException
     */
    private static Image getThumbimage(int scaledWidth, Image image)
            throws UnsupportedEncodingException {
        PageScaledImage page = getScaledImages(scaledWidth, image.getTitle());
        String thumburl;

        if (page != null && page.getImages() != null
                && !page.getImages().isEmpty()
                && page.getImages().get(0) != null
                && page.getImages().get(0) != null
                && page.getImages().get(0).getThumburl() != null) {
            thumburl = page.getImages().get(0).getThumburl();
        } else
            thumburl = image.getUrl();

        image.setThumburl(thumburl);

        return image;
    }

    /**
     * find a Simpson picture according to a String if String is null return fix
     * Image special treatment for episode names (gets images of the Simpsonswiki articles)
     *
     * @param answer
     * @param isSeasonorEpisode
     * @param scaledWidth
     * @return a Simpsonswiki image
     * @throws IOException
     */
    public static Image findPicture(String answer, boolean isSeasonorEpisode,
                                    int scaledWidth) throws IOException {
        if (answer == null)
            return getThumbimage(220, new Imagetitle("File:Simpson_Family.png"));
        if (isSeasonorEpisode && !answer.startsWith("Season "))
            return getThumbimage(scaledWidth, findEpisodePicture(answer));
        else
            return getThumbimage(scaledWidth, findPicture(answer));

    }

    /**
     * finds one of the images of an episode returns a random image of the
     * corresponding simpsonswiki article
     *
     * @param episode
     * @return an image of the episode atricle
     * @throws UnsupportedEncodingException
     */
    private static Imagetitle findEpisodePicture(String episode)
            throws UnsupportedEncodingException {
        String query;

        switch (episode) {
            case "Bart%27s_Dog_Gets_an_F":
                query = "Bart's_Dog_Gets_an_\"F\"";
                break;
            case "Boy-Scoutz_%27n_the_Hood":
                query = "Boy-Scoutz_'N_the_Hood";
                break;
            case "A_Star_is_Burns":
                query = "A_Star_Is_Burns";
                break;
            case "Summer_of_4_Ft._2_2":
                query = "Summer_of_4_Ft._2";
                break;
            case "El_Viaje_Misterioso_de_Nuestro_Jomer_(The_Mysterious_Voyage_of_Homer)":
                query = "El_Viaje_Misterioso_de_Nuestro_Jomer";
                break;
            case "E-I-E-I-D%27oh":
                query = "E-I-E-I-(Annoyed_Grunt)";
                break;
            case "HOMR":
                query = "HOMЯ";
                break;
            case "A_Star_Is_Born-Again":
                query = "A_Star_Is_Born_Again";
                break;
            case "Today,_I_Am_a_Clown":
                query = "Today_I_Am_a_Clown";
                break;

            case "I,_(Annoyed_Grunt)-Bot":
                query = "I,_(Annoyed_Grunt)-bot";
                break;
            case "Catch_%27Em_if_You_Can":
                query = "Catch_'Em_If_You_Can";
                break;
            case "A_Star_is_Torn":
                query = "A_Star_Is_Torn";
                break;
            case "Simpson_Christmas_Stories":
                query = "Simpsons_Christmas_Stories";
                break;
            case "We%27re_on_The_Road_To_D%27ohwhere":
                query = "We're_on_the_Road_to_D'ohwhere";
                break;
            case "Revenge_is_a_Dish_Best_Served_Three_Times":
                query = "Revenge_Is_a_Dish_Best_Served_Three_Times";
                break;
            case "Rome-Old_and_Julie-Eh":
                query = "Rome-Old_and_Juli-Eh";
                break;
            case "Sex,_Pies,_and_Idiot_Scrapes":
                query = "Sex,_Pies_and_Idiot_Scrapes";
                break;
            case "Mypods_and_Boomsticks":
                query = "MyPods_and_Boomsticks";
                break;
            case "The_Burns_And_The_Bees":
                query = "The_Burns_and_the_Bees";
                break;
            case "How_The_Test_Was_Won":
                query = "How_the_Test_Was_Won";
                break;
            case "In_The_Name_Of_The_Grandfather":
                query = "In_the_Name_of_the_Grandfather";
                break;
            case "The_Good,_the_Sad_and_the_Drugly":
                query = "The_Good,_the_Sad,_and_the_Drugly";
                break;
            case "Waverly_Hills,_9-0-2-1-D%27oh":
                query = "Waverly_Hills_9-0-2-1-D'oh";
                break;
            case "Coming_To_Homerica":
                query = "Coming_to_Homerica";
                break;
            case "Pranks_And_Greens":
                query = "Pranks_and_Greens";
                break;
            case "O_Brother,_Where_Bart_Thou%3F":
                query = "Oh_Brother,_Where_Bart_Thou%3F";
                break;
            case "Postcards_From_the_Wedge":
                query = "Postcards_from_the_Wedge";
                break;
            case "MoneyBART":
                query = "MoneyBart";
                break;
            case "Moms_I%27d_Like_To_Forget":
                query = "Moms_I'd_Like_to_Forget";
                break;
            case "A_Midsummer%27s_Nice_Dream":
                query = "A_Midsummer's_Nice_Dreams";
                break;
            case "Love_Is_a_Many_Strangled_Thing":
                query = "Love_Is_a_Many-Strangled_Thing";
                break;
            case "Moe_Goes_From_Rags_To_Riches":
                query = "Moe_Goes_from_Rags_to_Riches";
                break;
            case "Ned_%27N_Edna%27s_Blend":
                query = "Ned_'N'_Edna's_Blend";
                break;
            case "To_Cur_With_Love":
                query = "To_Cur,_with_Love";
                break;
            case "Black-eyed_Please":
                query = "Black-Eyed,_Please";
                break;
            case "Four_Regrettings_And_A_Funeral":
                query = "Four_Regrettings_and_a_Funeral";
                break;
            case "Yolo":
                query = "YOLO";
                break;
            case "I_Won%27t_Be_Home_for_Christmas":
                query = "I_Won't_Be_Home_For_Christmas";
                break;
            case "The_Man_Who_Came_to_Be_Dinner":
                query = "The_Man_Who_Came_To_Be_Dinner";
                break;
            default:
                query = episode;
        }

        ArrayList<Imagetitle> images = getEpisodeImage(query);
        int randomImage = (int) (Math.random() * (images.size()));

        if (!images.isEmpty())
            return images.get(randomImage);
        else
            return new Imagetitle("File:Donut Homer.png");
    }

    /**
     * find an Image to a name string via a Simpsonswiki allImagesQuery
     *
     * @param name
     * @return an Image that starts with the parameter name.
     * @throws IOException
     */
    private static Image findPicture(String name) throws IOException {

        name = filter(name);
        String queryResult = name.replace(" ", "_");
        if (!(queryResult.contains(".png") || queryResult.contains(".png")))
            queryResult = queryResult.concat(".");

        AllimagesQuery allimagesQ = ImageBackendCommunicator
                .getAllImagesResponse(wikiURL.concat(allImagesQuery).concat(
                        queryResult));
        Image response;
        if (allimagesQ != null && allimagesQ.getQuery() != null
                && allimagesQ.getQuery().getAllimages() != null
                && !allimagesQ.getQuery().getAllimages().isEmpty()) {
            response = allimagesQ.getQuery().getAllimages().get(0);
            String answer = response.getTitle().substring(5)
                    .replace(".png", "").replace(".gif", "")
                    .replace(".jpg", "");

            name = name.replace(".p", "");
            if (name.equals(answer) || name.split(" ").length < 2) {
                return response;
            }
            if (answer.split(" ").length > 1
                    && answer.split(" ")[1].equals(name.split(" ")[1])) {
                return response;
            }

            name = filter(name.substring(name.split(" ")[0].length() + 1));
            queryResult = name.replace(" ", "_") + ".";
            allimagesQ = ImageBackendCommunicator
                    .getAllImagesResponse(wikiURL.concat(allImagesQuery)
                            .concat(queryResult));
            if (allimagesQ != null && allimagesQ.getQuery() != null
                    && allimagesQ.getQuery().getAllimages() != null
                    && !allimagesQ.getQuery().getAllimages().isEmpty())
                return response;
        }
        return getThumbimage(220, new Imagetitle("File:Simpson_Family.png"));
    }

    /**
     * if possible change a string to an existing image filename of the
     * mostcommon Simpson characters, all 27 Seasons, Springfield and
     * Springfield Elementary School using the following pattern:
     * 1) firstname
     * -> File:firstname lastname f.i.: Homer -> File:Homer Simpson
     * 2) lastname
     * -> File:firstname lastname f.i.: Flanders -> File:Ned Flanders
     * 3) name
     * -> File:name.p (no jpg/3d pictures) f.i.: Crazy Cat Lady -> File:Crazy_Cat_Lady.p
     *
     * @param answer string of an NE
     * @return if possible a filename of an NE, else return given param answer
     */

    private static String filter(String answer) {
        String word;

        switch (answer) {
            case "Agnes":
                word = "Agnes Skinner";
                break;
            case "Allison":
            case "Taylor":
                word = "Allison Taylor";
                break;
            case "Arnie":
            case "Pye":
                word = "Arnie Pye";
                break;
            case "Arthur":
                word = "Arthur Crandall";
                break;
            case "Artie":
            case "Ziff":
                word = "Artie Ziff";
                break;
            case "Gerald":
                word = "Baby Gerald";
                break;
            case "Bernice":
                word = "Bernice Hibbert";
                break;
            case "Murphy":
                word = "Bleeding Gums Murphy";
                break;
            case "Brandine":
                word = "Brandine Spuckler";
                break;
            case "Brunella":
            case "Pommelhorst":
                word = "Brunella Pommelhorst";
                break;
            case "Murdock":
            case "Lance Murdock":
            case "Lance":
                word = "Captain Lance Murdock";
                break;
            case "Cecil":
                word = "Cecil Terwilliger";
                break;
            case "Lugash":
                word = "Coach Lugash";
                break;
            case "Kwan":
                word = "Cookie Kwan";
                break;
            case "Eleanor Abernathy":
            case "Eleanor":
            case "Abernathy":
            case "Crazy Cat Lady":
                word = "Crazy_Cat_Lady.p";
                break;
            case "Dave":
            case "Shutton":
                word = "Dave Shutton";
                break;
            case "Declan":
            case "Desmond":
                word = "Declan Desmond";
                break;
            case "Dewey":
            case "Largo":
                word = "Dewey Largo";
                break;
            case "Stu":
                word = "Disco Stu";
                break;
            case "Colossus":
                word = "Doctor Colossus";
                break;
            case "Dolph":
            case "Starbeam":
                word = "Dolph Starbeam";
                break;
            case "Tatum":
            case "Drederick":
                word = "Drederick Tatum";
                break;
            case "Hoover":
            case "Elizabeth":
                word = "Elizabeth Hoover";
                break;
            case "Francesca":
                word = "Francesca Terwilliger";
                break;
            case "Frankie":
                word = "Frankie the Squealer";
                break;
            case "Gil":
            case "Gunderson":
                word = "Gil Gunderson";
                break;
            case "Gino":
                word = "Gino Terwilliger";
                break;
            case "Greta":
                word = "Greta Wolfcastle";
                break;
            case "Helen":
                word = "Helen Lovejoy";
                break;
            case "Herman":
            case "Hermann":
                word = "Herman Hermann";
                break;
            case "Jack":
            case "Marley":
                word = "Jack Marley";
                break;
            case "Janey":
                word = "Janey Powell";
                break;
            case "Jasper":
                word = "Jasper Beardly";
                break;
            case "Jay":
                word = "Jay Sherman";
                break;
            case "Jebediah":
                word = "Jebediah Springfield";
                break;
            case "Jimbo":
            case "Jones":
                word = "Jimbo Jones";
                break;
            case "Johnny":
                word = "Johnny Tightlips";
                break;
            case "Constance":
            case "Harm":
            case "Constance Harm":
                word = "Judge Constance Harm";
                break;
            case "Snyder":
                word = "Judge Snyder";
                break;
            case "Kearney":
            case "Zzyzwicz":
                word = "Kearney Zzyzwicz";
                break;
            case "Kirk":
                word = "Kirk Van Houten";
                break;
            case "Lindsey":
            case "Naegle":
                word = "Lindsey Naegle";
                break;
            case "Ling":
                word = "Ling Bouvier";
                break;
            case "Lois":
            case "Pennycandy":
                word = "Lois Pennycandy";
                break;
            case "Luann":
                word = "Luann Van Houten";
                break;
            case "Lucius":
            case "Sweet":
                word = "Lucius Sweet";
                break;
            case "Luigi":
            case "Risotto":
                word = "Luigi Risotto";
                break;
            case "Doris":
            case "Dora":
            case "Freedman":
                word = "Lunchlady Doris";
                break;
            case "Lurleen":
            case "Lumpkin":
                word = "Lurleen Lumpkin";
                break;
            case "Marvin":
            case "Monroe":
                word = "Marvin Monroe";
                break;
            case "Manjula":
                word = "Manjula Nahasapeemapetilon";
                break;
            case "Martha":
                word = "Martha Prince";
                break;
            case "Martin":
            case "Prince":
                word = "Martin Prince";
                break;
            case "Bailey":
            case "Mary":
                word = "Mary Spuckler";
                break;
            case "Maude":
                word = "Maude Flanders";
                break;
            case "Kashmir":
                word = "Princess Kashmir";
                break;
            case "Dondelinger":
                word = "Principal Dondelinger";
                break;
            case "Hyman":
            case "Krustofski":
                word = "Rabbi Hyman Krustofski";
                break;
            case "Rachel":
            case "Jordan":
                word = "Rachel Jordan";
                break;
            case "Wolfcastle":
            case "Rainier":
                word = "Rainier Wolfcastle";
                break;
            case "Rod":
                word = "Rod Flanders";
                break;
            case "Roger":
            case "Meyers":
                word = "Roger Meyers Jr";
                break;
            case "Ruth":
                word = "Ruth Powers";
                break;
            case "Sanjay":
                word = "Sanjay Nahasapeemapetilon";
                break;
            case "Sarah":
                word = "Sarah Wiggum";
                break;
            case "Scott":
            case "Christian":
                word = "Scott Christian";
                break;
            case "Shauna":
                word = "Shauna Chalmers";
                break;
            case "Mel":
            case "Melvin":
            case "Van Horne":
            case "Melvin Van Horne":
            case "Sideshow Mel":
                word = "Sideshow_Mel.p";
                break;
            case "Chalmers":
            case "Gary":
                word = "Gary Chalmers";
                break;
            case "Ballerina":
            case "Tina":
                word = "Tina Ballerina";
                break;
            case "Todd":
                word = "Todd Flanders";
                break;
            case "Üter":
            case "Zörker":
                word = "Üter Zörker";
                break;
            case "Wendell":
                word = "Wendell Borton";
                break;
            case "Homer":
            case "Homer Jay Simpson, Sr.":
            case "Homer Jay Simpson":
                word = "Homer Simpson";
                break;
            case "Marge":
            case "Marjorie":
            case "Simpson-Bouvier":
                word = "Marge Simpson";
                break;
            case "Maggie":
            case "Margaret":
            case "Margaret `` Maggie '' Simpson":
                word = "Maggie Simpson";
                break;
            case "Bart":
            case "Bartholomew JoJo `` Bart '' Simpson":
                word = "Bart Simpson";
                break;
            case "Lisa":
                word = "Lisa Simpson";
                break;
            case "Abe":
            case "Abraham":
            case "Grampa":
            case "Grampa Simpson":
                word = "Abraham Simpson";
                break;
            case "Patty":
                word = "Patty Bouvier";
                break;
            case "Selma":
            case "Selma Terwilliger-Hutz-McClure-Stu-Simpson":
            case "Terwilliger-Hutz-McClure-Stu-Simpson":
                word = "Selma Bouvier";
                break;
            case "Mona":
                word = "Mona Simpson";
                break;
            case "Kent":
            case "Brockman":
            case "Kent Brockman":
                word = "Kent_Brockman.p";
                break;
            case "Riviera":
                word = "Dr. Riviera";
                break;
            case "Jeff Albertson":
            case "Albertson":
            case "Jeff":
                word = "Comic Book Guy";
                break;
            case "Ned":
                word = "Ned Flanders";
                break;
            case "Frink":
                word = "Professor Frink";
                break;
            case "Barney":
            case "Bernard":
            case "Gumble":
                word = "Barney Gumble";
                break;
            case "Hibbert":
            case "Julius":
                word = "Julius Hibbert";
                break;
            case "Hutz":
            case "Lionel":
                word = "Lionel Hutz";
                break;
            case "Rory B.":
            case "Rory":
            case "Bellows":
            case "Rory B. Bellows":
            case "Krusty":
                word = " Krusty the Clown";
                break;
            case "Reverend Lovejoy":
            case "Lovejoy":
            case "Timothy":
                word = "Timothy Lovejoy, Jr.";
                break;
            case "Troy":
            case "McClure":
                word = "Troy McClure";
                break;
            case "Hans":
            case "Moleman":
                word = "Hans Moleman";
                break;
            case "Apu":
            case "Nahasapeemapetilon":
                word = "Apu Nahasapeemapetilon";
                break;
            case "Joseph":
            case "Quimby":
            case "Joe":
            case "Fitzgerald":
                word = "Joe Quimby";
                break;
            case "Cletus":
            case "Spuckler":
                word = "Cletus Spuckler";
                break;
            case "Szyslak":
            case "Moe":
                word = "Moe Szyslak";
                break;
            case "Wiggum":
            case "Clancy":
                word = "Clancy Wiggum";
                break;
            case "Skinner":
            case "Armin Tamzarian":
            case "Tamzarian":
            case "Armin":
            case "Seymour":
                word = "Seymour Skinner";
                break;
            case "Otto":
            case "Mann":
                word = "Otto Mann";
                break;
            case "Milhouse":
            case "Van Houten":
            case "Milhouse Van Houton":
                word = "Milhouse Van Houten";
                break;
            case "Nelson":
            case "Muntz":
                word = "Nelson Muntz";
                break;
            case "Ralph":
                word = "Ralph_Wiggum.p ";
                break;
            case "Burns":
            case "Mr. Burns":
            case "Charles":
            case "Montgomery":
            case "Monty":
                word = "Charles Montgomery Burns";
                break;
            case "Lenny":
            case "Lenford":
            case "Leonard":
                word = "Lenny Leonard";
                break;
            case "Leon":
            case "Michael":
            case "Leon Kompowsky":
            case "Kompowsky":
                word = "Leon_Kompowsky.p";
                break;
            case "Carl":
            case "Carlton":
            case "Carlson":
            case "Carlson Jr.":
            case "Carlsson":
                word = "Carl Carlson";
                break;
            case "Waylon":
            case "Smithers":
            case "Waylon Smithers":
                word = "Waylon Smithers, Jr.";
                break;
            case "Willie Nelson":
                word = "Willie Nelson";
                break;
            case "Robert Terwilliger":
            case "Bob":
            case "Robert":
            case "Terwilliger":
            case "Robert Terwilliger Sr.":
            case "Sideshow Bob":
                word = "Sideshow_Bob.p";
                break;
            case "Snake":
            case "Jailbird":
                word = "Snake Jailbird";
                break;
            case "Marion":
            case "Anthony":
            case "Tony":
            case "D'Amico":
                word = "Fat Tony";
                break;
            case "Sherri":
            case "Terri":
            case "Mackleberry":
                word = "Sherri and Terri";
                break;
            case "Sophie Krustofsky":
                word = "Sophie";
                break;
            case "Mrs. Mackleberry":
                word = "Sherri and Terri's mother";
                break;
            case "Mr. Mackleberry":
                word = "Sherri and Terri's father";
                break;
            case "Rich Texan":
                word = "The Rich Texan";
                break;
            case "Willie":
                word = "Groundskeeper Willie";
                break;
            case "Hugo":
                word = "Hugo Simpson";
                break;
            case "Frank":
            case "Grimes":
                word = "Frank Grimes";
                break;
            case "Simpson":
            case "Simpsons":
                word = "Simpson Family";
                break;
            case "Flanders":
                word = "Flanders Family";
                break;

            // Locations
            case "Springfield":
                word = "Springfield.p";
                break;
            case "Springfield Elementary":
                word = "Springfield Elementary School";
                break;

            // Seasons
            case "Season 1":
                word = "Season 1 iTunes logo";
                break;
            case "Season 2":
                word = "Season 2 iTunes logo";
                break;
            case "Season 3":
                word = "Season 3 iTunes logo";
                break;
            case "Season 4":
                word = "Season 4 iTunes logo";
                break;
            case "Season 5":
                word = "Season 5 iTunes logo";
                break;
            case "Season 6":
                word = "Simpsons s6";
                break;
            case "Season 7":
                word = "Simpsons s7";
                break;
            case "Season 8":
                word = "Simpsons s8";
                break;
            case "Season 9":
                word = "Simpsons s9";
                break;
            case "Season 10":
                word = "Simpsons s10";
                break;
            case "Season 11":
                word = "Simpsonss11";
                break;
            case "Season 12":
                word = "The Complete Twelfth Season";
                break;
            case "Season 13":
                word = "Season 13 DVD";
                break;
            case "Season 14":
                word = "The Complete Fourteenth Season";
                break;
            case "Season 15":
                word = "The Complete Fifteenth Season";
                break;
            case "Season 16":
                word = "The Complete Sixteenth Season";
                break;
            case "Season 17":
                word = "The Complete Seventeenth Season";
                break;
            case "Season 18":
                word = "The Mook The Chef the Wife and Her Homer";
                break;
            case "Season 19":
                word = "He Loves to Fly and He D'ohs";
                break;
            case "Season 20":
                word = "Season 20 iTunes logo";
                break;
            case "Season 21":
                word = "Season 21 iTunes logo";
                break;
            case "Season 22":
                word = "Season 22 Logo";
                break;
            case "Season 23":
                word = "Season 23 iTunes logo";
                break;
            case "Season 24":
                word = "Season 24 iTunes logo";
                break;
            case "Season 25":
                word = "Season 25 iTunes logo";
                break;
            case "Season 26":
                word = "Season 26 iTunes logo";
                break;
            case "Season 27":
                word = "Every Man's Dream";
                break;

            default:
                word = answer;
        }
        return word;
    }

}
