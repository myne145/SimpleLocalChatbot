import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);

        System.out.println("Witaj! Którego modelu chciałbyś dziś użyć?");

        int iteration = 0;
        ArrayList<String> models = API.getModels();
        for(String model : models){
            System.out.println(iteration + ". " + model);
            iteration++;
        }
        int choice = stdin.nextInt();

        while(choice >= models.size() || choice < 0){
            System.out.printf("Podaj poprawną wartość: ");
            choice = stdin.nextInt();
        }
        System.out.println();

        String model = models.get(choice);
        System.out.printf("Wybrałeś %s",model);
        System.out.println();
        stdin.nextLine();

        String prompt = "";
        ArrayList<String> history = new ArrayList<>();
        Boolean quit = false;
        while(!quit){
            System.out.printf("(User):");
            prompt = stdin.nextLine();
            if(prompt.equals("quit")) quit = true;
            history.add("USER: "+prompt+",");

            JSONObject response = API.postPrompt(prompt,"</context>"+history.toString()+"<context>",model);
            JSONObject choices = (JSONObject) response.getJSONArray("choices").get(0);
            JSONObject message = (JSONObject) choices.get("message");
            String ans = message.getString("content");
            history.add("ASSISTANT: "+ans+",");

            JSONObject stats = (JSONObject) response.getJSONObject("stats");

            System.out.printf("(%s): %s\n",response.getString("model"),ans);
            System.out.printf("[%.1f Tokens/s, Time to first token: %.1fs, Total: %.1fs]\n",stats.getDouble("tokens_per_second"),stats.getDouble("time_to_first_token"),stats.getDouble("generation_time"));
            System.out.println();
        }

        stdin.close();
    }
}