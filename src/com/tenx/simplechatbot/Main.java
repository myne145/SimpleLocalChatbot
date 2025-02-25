package com.tenx.simplechatbot;

import com.tenx.simplechatbot.json.HistoryJSON;
import com.tenx.simplechatbot.lmapi.API;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        System.out.println("\u001B[32m~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\u001B[0m");
        System.out.println("Hello! What do you want to do?");
        System.out.println("1. New conversation");
        System.out.println("2. Load conversation");
        System.out.println("3. Help");
        System.out.print("Mode: ");
        int mode = stdin.nextInt();
        stdin.nextLine();
        System.out.println("\u001B[32m~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\u001B[0m");

        String model;
        ArrayList<String> history = new ArrayList<>();

        if(mode == 2){
            System.out.print("Name of the chat?:");
            String saveName = stdin.nextLine() + ".json";
            System.out.println();
            JSONObject save = HistoryJSON.getJsonObjectFromFilename(saveName);
            model = save.getString("model");
            JSONObject hist =  save.getJSONObject("history");
            for(int i = 0; i < save.getInt("size"); i++){
                history.add(hist.getString(i+""));
            }

            System.out.printf("Model: \u001B[34m%s\n\u001B[0m",model);
            for(String line: history){
                System.out.println(line);
            }
        }else if(mode == 3){
            System.out.println("Help:");
            System.out.println("After selecting a model or loading a conversation, the conversation starts. The default mode is prompt.");
            System.out.println("If you want to quit \"quit\"");
            System.out.println("If you want to enable/disable model statistics, type \"stat on/off\"");
            System.out.println("If you want to clear the conversation's context, type \"clear history\"");
            System.out.println("If you want to save the conversation, type \"save\"");
            return;
        }
        else{
            System.out.println("Available models:");
            JSONArray models = API.getModels();
            for(int i = 0; i < models.length(); i++){
                JSONObject currentModel = models.getJSONObject(i);
                if(currentModel.getString("state").equals("loaded")){
                    System.out.printf("%d. %-50s \u001B[32m%-50s\u001B[0m\n",i+1,currentModel.getString("id"),currentModel.getString("state"));
                }else{
                    System.out.printf("%d. %-50s \u001B[31m%-50s\u001B[0m\n",i+1,currentModel.getString("id"),currentModel.getString("state"));
                }
            }
            System.out.print("Which model would you like to use? : ");
            int choice = stdin.nextInt()-1;
            System.out.println("\u001B[32m~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\u001B[0m");

            while(choice >= models.length() || choice < 0){
                System.out.print("Please enter a valid value: ");
                choice = stdin.nextInt();
            }
            model = models.getJSONObject(choice).getString("id");
            stdin.nextLine();

            System.out.printf("Model: \u001B[34m%s\n\u001B[0m",model);
        }

        String prompt;
        boolean quit = false;
        boolean stat = false;
        String saveFileName = "";

        while(!quit){
            System.out.print("\u001B[32m~>\u001B[0m ");
            prompt = stdin.nextLine();

            if(prompt.equals("quit")){
                if(saveFileName.isEmpty()){
                    System.out.print("\u001B[35mWould you like to save this chat?\u001B[0m (\u001B[32my\u001B[0m/\u001B[31mN\u001B[0m):");
                    if(stdin.nextLine().equals("y")){
                        System.out.print("\u001B[35mName: \u001B[0m");
                        String saveName = stdin.nextLine() + ".json";
                        HistoryJSON.saveModelAndChatHistoryToJSON(history,model,saveName);
                        System.out.println("\u001B[35mSaved the chat as "+saveName+"\u001B[0m");
                        saveFileName = saveName;
                    }
                }else{
                    HistoryJSON.saveModelAndChatHistoryToJSON(history,model,saveFileName);
                }
                quit = true;
            }else

            if(prompt.equals("save")){
                if(saveFileName.isEmpty()){
                    System.out.print("\u001B[35mName of the chat: \u001B[0m");
                    String saveName = stdin.nextLine() + ".json";
                    HistoryJSON.saveModelAndChatHistoryToJSON(history,model,saveName);
                    System.out.println("\u001B[35mSaved the chat as "+saveName+"\u001B[0m");
                    saveFileName = saveName;
                }else{
                    HistoryJSON.saveModelAndChatHistoryToJSON(history,model,saveFileName);
                }
            }else

            if(prompt.equals("stat on")){
                System.out.println("\u001B[35mStats on!\u001B[0m");
                stat = true;
            }else

            if(prompt.equals("clear history")) {
                System.out.println("\u001B[35mFresh page!\u001B[0m");
                history.removeAll(history);
            }else

            if(prompt.equals("stat off")){
                System.out.println("\u001B[35mStats off!\u001B[0m");
                stat = false;
            }else{
                JSONObject response = API.postPrompt(prompt,"<context>"+history.toString()+"</context>",model);
                //System.out.println(response.toString(4));
                JSONObject choices = response.getJSONArray("choices").getJSONObject(0);
                JSONObject message = choices.getJSONObject("message");
                String ans = message.getString("content");

                history.add("USER: "+prompt);
                history.add("ASSISTANT: "+ans);

//            Ograniczamy historię do 6 ostatnich interakcji
//            if (history.size() > 6) {
//                history.subList(0, history.size() - 6).clear(); // Usuwamy starsze wpisy
//            }

                JSONObject stats = response.getJSONObject("stats");

                System.out.printf("(\u001B[34m%s\u001B[0m): %s\n",response.getString("model"),ans);
                if(stat){
                    System.out.printf("[\u001B[35m%.1f Tokens/s, Time to first token: %.1fs, Total: %.1fs\u001B[0m]\n",stats.getDouble("tokens_per_second"),stats.getDouble("time_to_first_token"),stats.getDouble("generation_time"));
                    System.out.println();
                }
            }
        }

        stdin.close();
    }
}