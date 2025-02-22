import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        System.out.println("Witaj! Chcesz rozpocząć nową konwersację czy wczytać poprzednia?");
        System.out.println("1. Nowa konwersacja");
        System.out.println("2. Wczytaj konwersacje");
        int mode = stdin.nextInt();
        stdin.nextLine();

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

            System.out.printf("Model: %s\n",model);
            for(String line: history){
                System.out.println(line);
            }
        }else{
            System.out.println("Którego modelu chciałbyś dziś użyć?");
            ArrayList<String> models = API.getModels();
            for(int i = 0; i < models.size(); i++){
                System.out.println(i+". " + models.get(i));
            }
            int choice = stdin.nextInt();
            while(choice >= models.size() || choice < 0){
                System.out.print("Podaj poprawną wartość: ");
                choice = stdin.nextInt();
            }
            model = models.get(choice);
            stdin.nextLine();

            System.out.print("\033[H\033[2J");
            System.out.flush();
        }

        String prompt;
        boolean quit = false;

        while(!quit){
            System.out.print("(User):");
            prompt = stdin.nextLine();
            if(prompt.equals("quit")){
                System.out.print("Chcesz zapisać ten chat? (y/n):");
                if(stdin.nextLine().equals("y")){
                    System.out.print("Nazwa chatu:");
                    String saveName = stdin.nextLine() + ".json";
                    Save save = new Save(model,history);
                    save.save(saveName);
                }
                System.out.println();
                quit = true;
            }
            history.add("USER: "+prompt);

            JSONObject response = API.postPrompt(prompt,"</context>"+history.toString()+"<context>",model);
            JSONObject choices = (JSONObject) response.getJSONArray("choices").get(0);
            JSONObject message = (JSONObject) choices.get("message");
            String ans = message.getString("content");
            history.add("ASSISTANT: "+ans);

            JSONObject stats = response.getJSONObject("stats");

            System.out.printf("(%s): %s\n",response.getString("model"),ans);
            System.out.printf("[%.1f Tokens/s, Time to first token: %.1fs, Total: %.1fs]\n",stats.getDouble("tokens_per_second"),stats.getDouble("time_to_first_token"),stats.getDouble("generation_time"));
            System.out.println();
        }

        stdin.close();
    }
}