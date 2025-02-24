package com.tenx.simplechatbot.json;

import com.tenx.simplechatbot.utils.Choice;
import com.tenx.simplechatbot.utils.ModelStats;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResponseJsonParser {
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

    public ResponseJsonParser(JSONObject response) {
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
        // tworzy JSON w postaci stringa z danymi argumentami. Gotowe pole data do wyslania do com.tenx.simplechatbot.lmapi.API

        return String.format("""
        {
        "model": "%s",
                "messages": [
                      { "role": "system", "content": "%s" },
                      { "role": "user", "content": "%s" }
                ],
        "temperature": 0.7,
        "max_tokens": -1,
        "stream": false
        }
        """,model,system.
                replace("\n", "\\n")
                .replace("\r", "\\r")
                        .replace("\"", "'" ),
                prompt.replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\"", "'" )
        );
        // Ta zamiana jest potrzebna w przypadku gdy model zwroci wiadomosc zawierajaca wiele linii. zeby nie popsuc struktury JSON musimy zamienic te symbole na escapeowe odpowiedzniki
    }
    public static JSONArray parseResponseModels(String response){

        JSONObject full = new JSONObject(response);

        return new JSONArray(full.getJSONArray("data"));
    }
}
