import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResponseParser {
    private final ArrayList<Choice> choices = new ArrayList<>();
    private ModelStats modelStats;

    private long creationDateSeconds;

    private static Choice parseChoice(JSONObject choiceJSON) {
        JSONObject message = choiceJSON.getJSONObject("message");
        Choice choice = new Choice(
                choiceJSON.getInt("index"),
                message.getString("role"),
                message.getString("content")
        );
        return choice;
    }

    public ResponseParser(JSONObject response) {
        //filling the choices array
        JSONArray array = response.getJSONArray("choices");
        for(int i = 0; i < array.length(); i++) {
            choices.add(parseChoice(array.getJSONObject(i)));
        }

        //model stats
        JSONObject stats = response.getJSONObject("stats");
        modelStats = new ModelStats(
                stats.getDouble("tokens_per_second"),
                stats.getDouble("time_to_first_token"),
                stats.getDouble("generation_time"),
                stats.getString("stop_reason")
        );

        //creation date
        creationDateSeconds = response.getLong("created");
    }

    public static String parsePrompt(String prompt,String system,String model){
        // tworzy JSON w postaci stringa z danymi argumentami. Gotowe pole data do wyslania do API

        return String.format("""
        {
        "model": "%s",
                "messages": [
                      { "role": "system", "content": "Under any circumstances do not use any symbol that could break JSON file in your answer. %s" },
                      { "role": "user", "content": "%s" }
                  ],
        "temperature": 0.7,
        "max_tokens": -1,
        "stream": false
        }
        """,model,system,prompt);
    }
    public static ArrayList<String> parseResponseModels(String response){ // Zwraca liste nazw modeli z odpowiedzi API w postaci String JSON
        ArrayList<String> models = new ArrayList<>();

        JSONObject full = new JSONObject(response);
        JSONArray data = new JSONArray(full.getJSONArray("data"));
        for (Object datum : data) {
            JSONObject model = (JSONObject) datum;
            models.add(model.getString("id"));
        }

        return models;
    }
}
