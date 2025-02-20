/*
{
    "model": "granite-3.0-2b-instruct",
    "messages": [
      { "role": "system", "content": "Always answer in rhymes." },
      { "role": "user", "content": "Introduce yourself." }
    ],
    "temperature": 0.7,
    "max_tokens": -1,
    "stream": false
  }
 */


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestJSON extends JSONObject {
    private static final float temperature = 0.7f;
    private static final int maxTokens = -1;
    private static final boolean stream = false;

    public RequestJSON(ArrayList<Message> messages, String model) {
        put("model", model);
        JSONArray arr = new JSONArray();
        for(Message message : messages) {
            JSONObject msg = new JSONObject();
            msg.put("role", message.getRole());
            msg.put("content", message.getContent());
            arr.put(msg);
        }
        put("messages", arr);
        put("temperature", temperature);
        put("max_tokens", maxTokens);
        put("stream", stream);
    }


}
