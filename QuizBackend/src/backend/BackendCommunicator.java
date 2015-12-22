package backend;

import com.google.gson.Gson;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import backend.types.BackendResponse;
import backend.types.ParameterPair;

/**
 * Backend Communicator for RESTful Backend-API.<br>
 * Allows to send GET, POST and DELETE requests and returns BackendResponse-Objects.
 *
 * @author dath
 */
public class BackendCommunicator {

    private final static int TIMEOUT = 5000;

    static boolean printOutResponse;
    static boolean useTrustStore = true;

    static SSLSocketFactory sslFactory;

    private static final String PROPS_FILE = "/backend.properties";
    private static final String TRUSTSTORE_FILE = "/custom-bks-cacert";

    /**
     * Enables printing raw responses to the console (for debugging)
     */
    public static void enablePrintOutResponse() {
        printOutResponse = true;
    }

    /**
     * Disables usage of custom TrustStore for SSL connections
     */
    public static void disableTrustStoreUsage() {
        useTrustStore = false;
    }

    /**
     * Checks whether TrustStore is used (default) or not
     *
     * @return true if usage of TrustStore is not disabled
     */
    public static boolean isUseTrustStore() {
        return useTrustStore;
    }

    /**
     * Sends a GET-Request.
     *
     * @param requestUrl the url to send the request to
     * @param appKey     appKey for authenticating the application
     * @param apiKey     apiKey for authenticating the user
     * @return response from the backend
     */
    public static BackendResponse sendGetRequest(String requestUrl, String appKey, String apiKey) {
        return getBackendResponse(execute(requestUrl, "GET", appKey, apiKey, false, null));
    }

    /**
     * Sends a GET-Request with parameters.
     *
     * @param requestUrl the url to send the request to
     * @param appKey     appKey for authenticating the application
     * @param apiKey     apiKey for authenticating the user
     * @param params     parameters to append to requestUrl
     * @return response from the backend
     */
    public static BackendResponse sendGetRequest(String requestUrl, String appKey, String apiKey, List<ParameterPair> params) {
        String requestUrlWithParams = (params.isEmpty() ? requestUrl : requestUrl + "?" + buildParameterString(params));
        return getBackendResponse(execute(requestUrlWithParams, "GET", appKey, apiKey, false, null));
    }

    /**
     * Sends a POST-Request with body parameters.
     *
     * @param requestUrl the url to send the request to
     * @param appKey     appKey for authenticating the application
     * @param apiKey     apiKey for authenticating the user
     * @param params     parameters to send in the request body
     * @return response from the backend
     */
    public static BackendResponse sendPostRequest(String requestUrl, String appKey, String apiKey, List<ParameterPair> params) {
        return getBackendResponse(execute(requestUrl, "POST", appKey, apiKey, true, params));
    }

    /**
     * Sends a DELETE-Request.
     *
     * @param requestUrl the url to send the request to
     * @param appKey     appKey for authenticating the application
     * @param apiKey     apiKey for authenticating the user
     * @return response from the backend
     */
    public static BackendResponse sendDeleteRequest(String requestUrl, String appKey, String apiKey) {
        return getBackendResponse(execute(requestUrl, "DELETE", appKey, apiKey, false, null));
    }

    /**
     * Executes a HTTP-Request.
     *
     * @param requestUrl    the url to send the request to
     * @param requestMethod the HTTP request method (like GET, POST, DELETE)
     * @param appKey        appKey for authenticating the application
     * @param apiKey        apiKey for authenticating the user
     * @param doOutput      true, if body output should be enabled
     * @param params        parameters to send in the request body
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

            if (useTrustStore && url.getProtocol().equals("https")) {
                HttpsURLConnection sslConnection = (HttpsURLConnection) url.openConnection();
                sslConnection.setSSLSocketFactory(getSSLFactoryWithTrustStore());
                urlConnection = sslConnection;
            } else
                urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(requestMethod);

            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setReadTimeout(TIMEOUT);

            if (appKey != null && !appKey.isEmpty())
                urlConnection.addRequestProperty("AppKey", appKey);

            if (apiKey != null && !apiKey.isEmpty())
                urlConnection.addRequestProperty("ApiKey", apiKey);

            if (doOutput && params != null && !params.isEmpty()) {
                urlConnection.setDoOutput(doOutput);

                // set Content-Type for PHP-Post
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
                // follow redirection
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                    String movedUrl = urlConnection.getHeaderField("Location");
                    if (movedUrl != null) {
                        return execute(movedUrl, requestMethod, appKey, apiKey, doOutput, params);
                    }
                }

                System.err.println("Response Code: " + responseCode);
                responseString = readStream(urlConnection.getErrorStream());

                if (!responseString.startsWith("{\"error") && !responseString.endsWith("}")) {
                    responseString = responseString.replace("\"", "\\\"");

                    responseString = "{\"error\":true, \"responseCode\":" + responseCode + ", \"message\":\"" + responseString + "\"}";
                }

            }
            response = responseString;

        } catch (SocketTimeoutException e) {
            System.err.println("TIMEOUT! " + e.getMessage());
            response = "{\"error\":true, \"message\":\"" + "Timeout! " + e.getMessage() + "\"}";
        } catch (Exception e) {
            System.err.println(e.getMessage());
            response = "{\"error\":true, \"message\":\"" + e.getMessage() + "\"}";
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return response;
    }


    /**
     * Returns the SSLSocketFactory with initialized TrustStore for certficate validation
     * (includes intermediate certificates for simpsonswiki)
     *
     * @return sslfactory with initialized TrustStore
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static SSLSocketFactory getSSLFactoryWithTrustStore() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        if (sslFactory == null)
            sslFactory = initSSLFactoryWithCustomTrustStore();
        return sslFactory;
    }

    /**
     * Configure SSLSocketFactory with a custom TrustStore that contains an additional certficate for simpsonswiki
     *
     * Implementation based on: http://stackoverflow.com/questions/859111/how-do-i-accept-a-self-signed-certificate-with-a-java-httpsurlconnection
     */
    private static SSLSocketFactory initSSLFactoryWithCustomTrustStore() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, KeyManagementException {
        System.out.println("Init SSLFactory with custom TrustStore");
        KeyStore trustStore = loadTrustStore();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);
        SSLSocketFactory sslFactory = ctx.getSocketFactory();

        return sslFactory;
    }

    /**
     * Load custom TrustStore that contains simpsonswiki certificate chain
     *
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    private static KeyStore loadTrustStore() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        // load truststore key
        InputStream propsIn = BackendCommunicator.class.getResourceAsStream(PROPS_FILE);
        Properties props = new Properties();
        props.load(propsIn);
        char[] trustStoreKey = props.getProperty("trustStorePassword").toCharArray();

        // load truststore
        // add bouncycastle provider for bks keystore type if necessary
        if (Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());
        InputStream storeIn = BackendCommunicator.class.getResourceAsStream(TRUSTSTORE_FILE);
        KeyStore trustStore = KeyStore.getInstance("BKS");
        trustStore.load(storeIn, trustStoreKey);

        return trustStore;
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

        if (printOutResponse)
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
