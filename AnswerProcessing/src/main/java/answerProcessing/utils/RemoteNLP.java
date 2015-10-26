package answerProcessing.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.ws.http.HTTPException;

import answerProcessing.types.Text;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

public class RemoteNLP {

    private static String urlNlpServer = "http://localhost:8080/";

    public static String getUrlNlpServer() {
        return urlNlpServer;
    }

    public static void setUrlNlpServer(String urlNlpServer) {
        RemoteNLP.urlNlpServer = urlNlpServer;
    }

    /**
     * Annotate given text (split into sentences, annotate tokens, ...)
     *
     * @param text the text to annotate
     * @return annotated Text-Object
     */
    public static Text annotate(String text) throws IOException {

        String jsonResponse = getRemoteJsonResponse(text);
        Gson gson = new Gson();
        Text annotatedText = gson.fromJson(jsonResponse, Text.class);

        return annotatedText;
    }

    /**
     * Returns json response with annotated text from StandfordCoreNLPServer
     *
     * @param text
     * @return annotated text as json
     */
    public static String getRemoteJsonResponse(String text) throws IOException {
        return getRemoteResponse("json", text);
    }

    /**
     * Returns xml response with annotated text from StandfordCoreNLPServer
     *
     * @param text
     * @return annotated text as xml
     */
    public static String getRemoteXMLResponse(String text) throws IOException {
        return getRemoteResponse("xml", text);
    }

    private static String getRemoteResponse(String outputMode, String text) throws IOException {

        String result = "";
        HttpURLConnection urlConnection = null;
        URL queryUrl = null;
        String charset = "UTF-8";

        queryUrl = new URL(urlNlpServer);
        urlConnection = (HttpURLConnection) queryUrl.openConnection();

        String parameters = String.format("outputMode=%s&text=%s",
                URLEncoder.encode(outputMode, charset),
                URLEncoder.encode(text, charset));

        // Connect
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");

        // Send post request
        OutputStream output = urlConnection.getOutputStream();
        output.write(parameters.getBytes(charset));

        urlConnection.connect();

        int status = urlConnection.getResponseCode();

        // Read Response
        InputStream in;
        if (status >= HttpURLConnection.HTTP_BAD_REQUEST)
            in = urlConnection.getErrorStream();
        else
            in = urlConnection.getInputStream();

        result = convertStreamToString(in);
        in.close();
        if (status >= HttpURLConnection.HTTP_BAD_REQUEST) {
            throw new HTTPException(status);
        }

        if (urlConnection != null) {
            urlConnection.disconnect();
        }

        return result;
    }

    private static String getFromJsonFile(String fileName) {
        String jsonFile = "src/main/resources/" + fileName;
        String jsonString = "";

        try {
            jsonString = new String(readAllBytes(get(jsonFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    public static String toJsonString(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public static Text jsonToAnnotatedText(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Text.class);
    }

    private static String convertStreamToString(InputStream is)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
}
