package com.tenx.simplechatbot.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResponseJsonParser {

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
