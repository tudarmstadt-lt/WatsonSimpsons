package backend;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import backend.types.BackendResponse;
import backend.types.ParameterPair;

/**
 * Backend Communicator for RESTful Backend-API.<br>
 * Allows to send GET, POST and DELETE requests and returns BackendResponse-Objects.
 *
 * @author dath
 */
public class BackendCommunicator {

    static boolean printOutResponse;

    /**
     * Enables printing raw responses to the console (for debugging)
     */
    public static void enablePrintOutResponse() {
        printOutResponse = true;
    }

    /**
     * Sends a GET-Request.
     *
     * @param requestUrl the url to send the request to
     * @param appKey appKey for authenticating the application
     * @param apiKey apiKey for authenticating the user
     * @return response from the backend
     */
    public static BackendResponse sendGetRequest(String requestUrl, String appKey, String apiKey) {
        return getBackendResponse(execute(requestUrl, "GET", appKey, apiKey, false, null));
    }

    /**
     * Sends a GET-Request with parameters.
     *
     * @param requestUrl the url to send the request to
     * @param appKey appKey for authenticating the application
     * @param apiKey apiKey for authenticating the user
     * @param params parameters to append to requestUrl
     * @return response from the backend
     */
    public static BackendResponse sendGetRequest(String requestUrl, String appKey, String apiKey, List<ParameterPair> params) {
        String requestUrlWithParams = (params.isEmpty() ? requestUrl : requestUrl+"?"+buildParameterString(params));
        return getBackendResponse(execute(requestUrlWithParams, "GET", appKey, apiKey, false, null));
    }

    /**
     * Sends a POST-Request with body parameters.
     *
     * @param requestUrl the url to send the request to
     * @param appKey appKey for authenticating the application
     * @param apiKey apiKey for authenticating the user
     * @param params parameters to send in the request body
     * @return response from the backend
     */
    public static BackendResponse sendPostRequest(String requestUrl, String appKey, String apiKey, List<ParameterPair> params) {
        return getBackendResponse(execute(requestUrl, "POST", appKey, apiKey, true, params));
    }

    /**
     * Sends a DELETE-Request.
     *
     * @param requestUrl the url to send the request to
     * @param appKey appKey for authenticating the application
     * @param apiKey apiKey for authenticating the user
     * @return response from the backend
     */
    public static BackendResponse sendDeleteRequest(String requestUrl, String appKey, String apiKey) {
        return getBackendResponse(execute(requestUrl, "DELETE", appKey, apiKey, false, null));
    }

    /**
     * Executes a HTTP-Request.
     *
     * @param requestUrl the url to send the request to
     * @param requestMethod the HTTP request method (like GET, POST, DELETE)
     * @param appKey appKey for authenticating the application
     * @param apiKey apiKey for authenticating the user
     * @param doOutput true, if body output should be enabled
     * @param params parameters to send in the request body
     * @return response from the backend
     */
    @SuppressWarnings("unused")
	public static String execute(String requestUrl, String requestMethod, String appKey, String apiKey, boolean doOutput, List<ParameterPair> params) {
        URL url;
        HttpURLConnection urlConnection = null;
        String response = "";
        String charset = "UTF-8";

        try {
            url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(requestMethod);

            if (appKey != null && !appKey.isEmpty())
                urlConnection.addRequestProperty("AppKey", appKey);

            if (apiKey != null && !apiKey.isEmpty())
                urlConnection.addRequestProperty("ApiKey", apiKey);

            if (doOutput && params != null && !params.isEmpty()) {
                urlConnection.setDoOutput(doOutput);

                // set Contet-Type for PHP-Post
                urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(buildParameterString(params).getBytes());
                out.flush();

            }

            int responseCode = urlConnection.getResponseCode();
            String responseString;
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                responseString = readStream(urlConnection.getInputStream());
            } else {
                System.err.println("Response Code: " + responseCode);
                responseString = readStream(urlConnection.getErrorStream());

                if(! responseString.startsWith("{\"error") && !responseString.endsWith("}")) {
                    responseString = responseString.replace("\"", "\\\"");

                    responseString = "{\"error\":true, \"responseCode\":" + responseCode + ", \"message\":\"" + responseString + "\"}";
                }

            }
            response = responseString;

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR!";
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return response;
    }

    private static String readStream(InputStream in)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    /**
     * Builds a parameter string for body (x-www-form-urlencoded).
     *
     * @param params list of parameters
     * @return x-www-form-urlencoded parameter string
     */
    private static String buildParameterString(List<ParameterPair> params) {
        StringBuilder sb = new StringBuilder("");

        for (ParameterPair param : params) {
            sb.append(param.getKey() + "=" + param.getValue() + "&");
        }

        String paramString = sb.toString();

        if (paramString.endsWith("&")) {
            paramString = paramString.substring(0, paramString.length() - 1);
        }

        return paramString;

    }

    /**
     * Parses JSON response string and returns a BackendResponse-Object.
     *
     * @param responseString JSON response string to parse
     * @return response as BackendResponse-Object
     */
    private static BackendResponse getBackendResponse(String responseString) {

        if(printOutResponse)
            System.out.println(responseString);

        BackendResponse response;
        try {
            Gson gson = new Gson();
            response = gson.fromJson(responseString, BackendResponse.class);
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error: Response was not JSON formatted!");

            response = new BackendResponse();
            response.setError(true);
            response.setMessage("Response was not JSON formatted!");
        }

        return response;
    }

}
