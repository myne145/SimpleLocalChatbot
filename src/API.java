import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class API {
    public static final String endpoint = "http://localhost:1234";
    public static final String get = "/api/v0/models"; // GET available models
    public static final String post = "/api/v0/chat/completions"; // POST chat mode

    public static String getModels(){
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(endpoint + get);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }

            return response.toString();
        }catch(Exception e){
            e.printStackTrace();
        }

        return response.toString();
    }
}
