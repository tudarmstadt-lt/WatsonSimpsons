package imagefinder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map.Entry;

import backend.BackendCommunicator;
import imagefinder.types.AllimagesQuery;
import imagefinder.types.ImagesOnPage;
import imagefinder.types.PageScaledImage;

public class ImageBackendCommunicator extends BackendCommunicator {

    public static AllimagesQuery getAllImagesResponse(String url) {
        Gson gson = new Gson();
        String response = execute(url, "GET", "", "", false, null);
        return gson.fromJson(response, AllimagesQuery.class);
    }

    public static ImagesOnPage getImagesOnArticleResponse(String url) {
        Gson gson = new Gson();
        String response = execute(url, "GET", "", "", false, null);

        JsonElement root = new JsonParser().parse(response);
        if (root.getAsJsonObject().get("query") == null)
            return null;

        JsonObject query = root.getAsJsonObject().get("query").getAsJsonObject();
        if (query.getAsJsonObject().get("pages") == null)
            return null;

        JsonObject pages = query.getAsJsonObject().get("pages").getAsJsonObject();

        // Iterate over this map
        for (Entry<String, JsonElement> entry : pages.entrySet()) {
            ImagesOnPage page = gson.fromJson(entry.getValue(), ImagesOnPage.class);
            return page;
        }

        return null;
    }

    public static PageScaledImage getThumbImageResponse(String url) {
        Gson gson = new Gson();
        String response = execute(url, "GET", "", "", false, null);

        JsonElement root = new JsonParser().parse(response);
        if (root.getAsJsonObject().get("query") == null)
            return null;

        JsonObject query = root.getAsJsonObject().get("query").getAsJsonObject();
        if (query.getAsJsonObject().get("pages") == null)
            return null;

        JsonObject pages = query.getAsJsonObject().get("pages").getAsJsonObject();

        for (Entry<String, JsonElement> entry : pages.entrySet()) {
            PageScaledImage page = gson.fromJson(entry.getValue(), PageScaledImage.class);
            return page;
        }

        return null;
    }
}
