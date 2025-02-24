import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        System.out.println("\u001B[32m~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\u001B[0m");
        System.out.println("Witaj! Chcesz rozpocząć nową konwersację czy wczytać poprzednią?");
        System.out.println("1. Nowa konwersacja");
        System.out.println("2. Wczytaj konwersacje");
        System.out.println("3. Pomoc");
        System.out.print("Tryb: ");
        int mode = stdin.nextInt();
        stdin.nextLine();
        System.out.println("\u001B[32m~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\u001B[0m");

        String model;
        ArrayList<String> history = new ArrayList<>();

        if(mode == 2){
            System.out.print("Nazwa chatu?:");
            String saveName = stdin.nextLine() + ".json";
            System.out.println();
            JSONObject save = Save.load(saveName);
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
            System.out.println("Pomoc:");
            System.out.println("Po wybraniu modelu lub wczytaniu chatu rozpocznie się konwersacja. Domyślny tryb to prompt.");
            System.out.println("Jeśli chcesz wyjść z programu wpisz \"quit\"");
            System.out.println("Jeśli chcesz włączyć/wyłączyć statystyki wpisz \"stat on/off\"");
            System.out.println("Jeśli chcesz wyczyścić cały kontekst rozmowy wpisz \"clear history\"");
            System.out.println("Jeśli chcesz zapisać konwersacje wpisz \"save\"");
            return;
        }
        else{
            System.out.println("Dostępne modele:");
            JSONArray models = API.getModels();
            for(int i = 0; i < models.length(); i++){
                JSONObject currentModel = models.getJSONObject(i);
                if(currentModel.getString("state").equals("loaded")){
                    System.out.printf("%d. %-50s \u001B[32m%-50s\u001B[0m\n",i+1,currentModel.getString("id"),currentModel.getString("state"));
                }else{
                    System.out.printf("%d. %-50s \u001B[31m%-50s\u001B[0m\n",i+1,currentModel.getString("id"),currentModel.getString("state"));
                }
            }
            System.out.print("Którego modelu chciałbyś dziś użyć? : ");
            int choice = stdin.nextInt()-1;
            System.out.println("\u001B[32m~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\u001B[0m");

            while(choice >= models.length() || choice < 0){
                System.out.print("Podaj poprawną wartość: ");
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
                    System.out.print("\u001B[35mChcesz zapisać ten chat?\u001B[0m (\u001B[32my\u001B[0m/\u001B[31mN\u001B[0m):");
                    if(stdin.nextLine().equals("y")){
                        System.out.print("\u001B[35mNazwa chatu: \u001B[0m");
                        String saveName = stdin.nextLine() + ".json";
                        Save save = new Save(model,history);
                        save.save(saveName);
                        System.out.println("\u001B[35mZapisano jako "+saveName+"\u001B[0m");
                        saveFileName = saveName;
                    }
                }else{
                    Save save = new Save(model,history);
                    save.save(saveFileName);
                }
                quit = true;
            }else

            if(prompt.equals("save")){
                if(saveFileName.isEmpty()){
                    System.out.print("\u001B[35mNazwa chatu: \u001B[0m");
                    String saveName = stdin.nextLine() + ".json";
                    Save save = new Save(model,history);
                    save.save(saveName);
                    System.out.println("\u001B[35mZapisano jako "+saveName+"\u001B[0m");
                    saveFileName = saveName;
                }else{
                    Save save = new Save(model,history);
                    save.save(saveFileName);
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