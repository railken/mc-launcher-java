import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class AuthService
{
    /**
     * Constructor
     */
    public AuthService()
    {

    }

    /**
     *  Perform a request
     *
     * @param method
     * @param path
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject request(String method, String path, JSONObject params) throws Exception
    {
        URL url = new URL("https://authserver.mojang.com"+path);

        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setRequestMethod(method);
        con.setRequestProperty("Content-type", "application/json");
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(params.toString());
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        if (responseCode == 400 || responseCode == 403) {
            throw new Exception();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        if (responseCode == 204)
            return new JSONObject("{}");

        return new JSONObject(response.toString());

    }

    /**
     * Request token
     *
     * @param username user's email/username
     * @param password user's password
     * @return response
     * @throws Exception
     */
    public JSONObject authenticate(String username, String password) throws Exception
    {
        return this.request("POST", "/authenticate", new JSONObject()
            .put("clientToken", "MC-01-LAUNCHER")
            .put("requestUser", true)
            .put("username", username)
            .put("password", password)
            .put("agent", new JSONObject()
                    .put("name", "Minecraft")
                    .put("version", 1)
            )
        );
    }

    /**
     * Validate token
     *
     * @param accessToken AccessToken OAuth 2.0
     * @return response
     * @throws Exception
     */
    public JSONObject validate(String accessToken) throws Exception
    {
        return this.request("POST", "/validate", new JSONObject()
            .put("clientToken", "MC-01-LAUNCHER")
            .put("accessToken", accessToken)
        );
    }

    /**
     *  Refresh token
     *
     * @param accessToken AccessToken OAuth 2.0
     * @param playerIdentifier User's ID
     * @param playerName User's name
     * @return response
     * @throws Exception
     */
    public JSONObject refresh(String accessToken, String playerIdentifier, String playerName) throws Exception
    {
        return this.request("POST", "/refresh", new JSONObject()
            .put("clientToken", "MC-01-LAUNCHER")
            .put("requestUser", true)
            .put("accessToken", accessToken)
            .put("selectedProfile", new JSONObject()
                .put("id", playerIdentifier)
                .put("name", playerName)
            )
        );
    }
}


