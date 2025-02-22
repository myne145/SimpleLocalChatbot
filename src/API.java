import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class API {
    public static final String endpoint = "http://localhost:1234";
    private static final String get = "/api/v0/models"; // GET available models
    public static final String post = "/api/v0/chat/completions"; // POST chat mode

    public static ArrayList<String> getModels(){
        ArrayList<String> models = new ArrayList<>();       // lista dostepnych modeli na serwerze
        StringBuilder serverResponse = new StringBuilder(); // builder stringa z calkowita odpowiedzia serwera

        try {
            URL url = new URL(endpoint + get);        // tworzymy url z endpointu API
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // łączenie z endpointem
            int responseCode = conn.getResponseCode();       // pobieramy kod odpowiedzi HTTP

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream())); // zczytujemy dane ze strumienia połączenia do bufora
                String inputLine;   // zmienna pomocnicza na zczytanie linii z bufora
                while ((inputLine = in.readLine()) != null) {
                    serverResponse.append(inputLine); // dodajemy do buildera Stringa z odpowiedzia serwera
                }
                in.close();         // zamykamy bufor zeby nie zostawiac zbednych zasobow
                conn.disconnect();  // to samo z polaczeniem

                models = ResponseParser.parseResponseModels(serverResponse.toString()); // wypelniamy liste modeli nazwami modeli z JSON'a wyslanego przez serwer
            }else{
                System.out.printf("Serwer: %d\n",responseCode);
            }

        }catch(Exception e){
            System.out.println("Serwer nie odpowiada. Próbuję ponownie połączyć się z serwerem...");
            try{
                TimeUnit.SECONDS.sleep(5); // Usypia na 2 sekundy
            }catch(Exception e2){
                e2.printStackTrace();
            }
            return getModels();
        }

        return models;
    }

    public static JSONObject postPrompt(String prompt, String system, String model){

        String data = ResponseParser.parsePrompt(prompt,system,model);  // dane w postaci obiektu JSON

        StringBuilder response = new StringBuilder();      // bufor na odpowiedz serwera

        try {
            URL url = new URL(API.endpoint + API.post);     // tworzymy url endpointu
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // otwieramy połączenie
            conn.setRequestMethod("POST");      // ustawiamy rodzaj zapytania jako POST (wysylamy prompt)
            conn.setRequestProperty("Content-Type","application/json");   // dodajemy wymagane naglowki
            conn.setDoOutput(true);             // wyjscie na prompt

            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes("UTF-8"));   // wpisujemy dane z proptem do bufora strumienia polaczenia
                os.flush();                                         // odsiwerzamy strumien (wysylamy dane z bufora)
            }

            int responseCode = conn.getResponseCode();              // zczytujemy odpowiedz serwera

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                conn.disconnect();
                in.close();
            }else{
                System.out.printf("Serwer: %d\n",responseCode);
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        //odpowiedz serwera w postaci JSON zawiera nie tylko wiadomosc zwrotna ale takze statystyki wiec zwracamy caly obiekt
        return new JSONObject(response.toString());
    }
}