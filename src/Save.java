import org.json.JSONObject;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Save {
    String model;
    ArrayList<String> history;

    Save(String model, ArrayList<String> history){
        this.model = model;
        this.history = history;
    }

    public void save(String saveName){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model",this.model);
        JSONObject history = new JSONObject();

        int i;
        for(i = 0; i < this.history.size(); i++){
            history.put(i+"", this.history.get(i));
        }
        jsonObject.put("size",i);
        jsonObject.put("history",history);

        try(FileWriter writer = new FileWriter(saveName)) {
            writer.write(jsonObject.toString(4));
            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject load(String saveName){
        JSONObject jsonObject = new JSONObject();

        try {
            // Wczytanie pliku jako String
            String content = new String(Files.readAllBytes(Paths.get(saveName)));

            // Konwersja String do JSONObject
            jsonObject = new JSONObject(content);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}

