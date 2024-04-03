package com.example.apachetomcatservlerorderbook;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MealUtility {

    public static List<HttpSession> sessions = new ArrayList<>();

    public static List<HashMap<String,Integer>> foodOnOfferAndNumber = new ArrayList<>();

    public static String days[] = {"monday","tuesday","wednesday","thursday","friday"};
    static final Object blockerDelete = new Object();
    static final Object blockerUpdate = new Object();

    public static String password;

    public static void createList(){
        BufferedReader br;
        List<HashMap<String,Integer>> tmp = new ArrayList<>();
        for(int i = 0; i < days.length;i++){
            try {
                br = new BufferedReader(
                        new FileReader("C:\\Users\\jonci\\Desktop\\WEBHW2\\Apache-Tomcat-Servler-OrderBook\\src\\main\\java\\com\\example\\apachetomcatservlerorderbook\\weekday_meals\\"+days[i] + ".txt"));
                String line;
                HashMap<String,Integer> hmTmp = new HashMap<>();
                while((line = br.readLine()) != null){
                    hmTmp.put(line,0);
                }
                tmp.add(hmTmp);
                br.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        foodOnOfferAndNumber = tmp;
    }

    public static void initUtility(){
        loadPassoword();
        createList();
    }


    public static void cleanList() {
        System.out.println("Cleaning the number of meals");
        for (HashMap<String, Integer> hm : foodOnOfferAndNumber) {
            synchronized (blockerDelete) {
                for (String key : hm.keySet()) {
                    hm.put(key, 0);
                }
            }
        }
    }


    public static void increaseMealUsage(List<String> chosenMeals) {
        if (chosenMeals.size() > days.length) {
            throw new IllegalArgumentException("Number of chosen meals exceeds available days");
        }

        for (int i = 0; i < chosenMeals.size(); i++) {
            synchronized (blockerUpdate) {
                int index = i % days.length;
                HashMap<String, Integer> dayMeals = foodOnOfferAndNumber.get(index);
                String meal = chosenMeals.get(i);
                if (dayMeals.containsKey(meal)) {
                    int currentCount = dayMeals.get(meal);
                    dayMeals.put(meal, currentCount + 1);
                } else {
                    System.err.println("Invalid meal: " + meal + " for day: " + days[index]);
                }
            }
        }
    }


    public static void loadPassoword(){
        String tmpPassword;
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jonci\\Desktop\\WEBHW2\\Apache-Tomcat-Servler-OrderBook\\src\\main\\java\\com\\example\\apachetomcatservlerorderbook\\password.txt"));
            tmpPassword = br.readLine();
            br.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        password = tmpPassword;
    }



    public static String allFoodHtml(){
        StringBuilder sb = new StringBuilder("");
        for (HashMap<String, Integer> hm : foodOnOfferAndNumber) {
            sb.append("<div class=\"form-group\">\n" +
                    "    <label for=\"" + MealUtility.days[foodOnOfferAndNumber.indexOf(hm)] + "\">" + MealUtility.days[foodOnOfferAndNumber.indexOf(hm)] + "</label>\n" +
                    "    <select class=\"form-control\" id=\"" + MealUtility.days[foodOnOfferAndNumber.indexOf(hm)] + "\" name=\"" + MealUtility.days[foodOnOfferAndNumber.indexOf(hm)] + "\">\n");

            // Loop through the meals in the current hashmap
            for (String meal : hm.keySet()) {
                sb.append("        <option>" + meal + "</option>\n");
            }

            sb.append("    </select>\n" +
                    "</div>\n");
        }
        return sb.toString();
    }


    public static void cleanSessions(){
        for (HttpSession session : sessions) {
            session.invalidate();
        }
        sessions.clear();
    }
}
