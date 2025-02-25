package com.tenx.simplechatbot.json;

import org.json.JSONObject;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class HistoryJSON {

    public static void saveModelAndChatHistoryToJSON(ArrayList<String> historyArray, String model,String saveName){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model",model);
        JSONObject history = new JSONObject();

        int i;
        for(i = 0; i < historyArray.size(); i++){
            history.put(i+"", historyArray.get(i));
        }
        jsonObject.put("size",i);
        jsonObject.put("history",history);

        try(FileWriter writer = new FileWriter(saveName)) {
            writer.write(jsonObject.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getJsonObjectFromFilename(String filename){
        JSONObject jsonObject = new JSONObject();

        try {
            // Wczytanie pliku jako String
            String content = new String(Files.readAllBytes(Paths.get(filename)));

            // Konwersja String do JSONObject
            jsonObject = new JSONObject(content);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}

