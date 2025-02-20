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

}
