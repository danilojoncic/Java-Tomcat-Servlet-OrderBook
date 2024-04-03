package com.example.apachetomcatservlerorderbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "food", value = "/food")
public class FoodServlet extends HttpServlet {
    public void init() {
        MealUtility.initUtility();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("GET CALLED");
        if(request.getQueryString() != null && request.getQueryString().contains("password=" + MealUtility.password)){

            System.out.println("PASSWORD AND TOTALS SENDING");
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            StringBuilder sb = new StringBuilder("");

            sb.append("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Chosen Food</title>\n" +
                    "    <!-- Include Bootstrap CSS -->\n" +
                    "    <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container mt-5\">\n" +
                    "        <h1>Chosen Food</h1>\n");

            for (int i = 0; i < MealUtility.foodOnOfferAndNumber.size(); i++) {
                HashMap<String, Integer> mealsForDay = MealUtility.foodOnOfferAndNumber.get(i);
                String day = MealUtility.days[i];

                sb.append("        <h4 class=\"mt-4\">" + day + "</h4>\n" +
                        "        <table class=\"table table-bordered\">\n" +
                        "            <thead>\n" +
                        "                <tr>\n" +
                        "                    <th>#</th>\n" +
                        "                    <th>Meal</th>\n" +
                        "                    <th>Number</th>\n" +
                        "                </tr>\n" +
                        "            </thead>\n" +
                        "            <tbody>\n");

                int count = 1;
                for (String meal : mealsForDay.keySet()) {
                    sb.append("                <tr>\n" +
                            "                    <td>" + count + "</td>\n" +
                            "                    <td>" + meal + "</td>\n" +
                            "                    <td>" + mealsForDay.get(meal) + "</td>\n" +
                            "                </tr>\n");
                    count++;
                }

                sb.append("            </tbody>\n" +
                        "        </table>\n");
            }

            sb.append("<form id=\"clearForm\" method=\"POST\" action=\"food\">\n" +
                    "    <input type=\"hidden\" name=\"action\" value=\"delete\">\n" + // Hidden input field
                    "    <button type=\"submit\" class=\"btn btn-danger mt-4\">Clear All</button>\n" +
                    "</form>\n" +
                    "</div>\n" +
                    "\n" +
                    "<!-- Include Bootstrap JS (optional) -->\n" +
                    "<script src=\"https://code.jquery.com/jquery-3.5.1.slim.min.js\"></script>\n" +
                    "<script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js\"></script>\n" +
                    "<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js\"></script>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>\n");

            out.println(sb.toString());

        }else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Choose your food</title>\n" +
                    "    <!-- Bootstrap CSS -->\n" +
                    "    <link href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "<div class=\"container mt-5\">\n" +
                    "    <h1 class=\"mb-4\">Choose your food</h1>\n" +
                    "    <form id=\"foodForm\" method=\"POST\" action=\"food\">\n" +
                    MealUtility.allFoodHtml() +
                    "    <button type=\"submit\" class=\"btn btn-primary mt-3\">Submit</button>\n" +
                    "</form>\n" +
                    "</div>\n" +
                    "\n" +
                    "<!-- Bootstrap JS and dependencies (jQuery, Popper.js) -->\n" +
                    "<script src=\"https://code.jquery.com/jquery-3.5.1.slim.min.js\"></script>\n" +
                    "<script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js\"></script>\n" +
                    "<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js\"></script>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>\n");

        }
    }
    public void destroy() {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        System.out.println("POST CALLED");
        BufferedReader reader = req.getReader();
        PrintWriter out = resp.getWriter();
        StringBuilder sb = new StringBuilder();
        String line;
        System.out.println(req.getQueryString());
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        if (sb.toString().contains("action=delete")) {
            System.out.println("IMAM DELETE PARAMETER");
            MealUtility.cleanList();
            MealUtility.cleanSessions();
            resp.sendRedirect("/Apache_Tomcat_Servler_OrderBook_war_exploded/food?password=" + MealUtility.password);
        }else {
            HttpSession session = req.getSession();
            if(MealUtility.sessions.contains(session)){
                out.println("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>OrderBook</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>Denied!</h1>\n" +
                        "</body>\n" +
                        "</html>");
            }else{
                System.out.println("NEMAM DELETE PARAMETER");

                System.out.println("Request body: " + sb.toString());
                String parts[] = sb.toString().split("&");
                List<String> chosenMeals = new ArrayList<>();
                for (String part : parts) {
                    String middle[] = part.split("=");
                    chosenMeals.add(middle[1].replaceAll("\\+", " "));
                }
                MealUtility.increaseMealUsage(chosenMeals);
                StringBuilder chsFood = new StringBuilder("");
                chsFood.append("<h2> ");
                for (String meal : chosenMeals) {
                    chsFood.append(meal + " ");
                }
                chsFood.append("</h2>");
                resp.setContentType("text/html");
                MealUtility.sessions.add(session);

                out.println("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>OrderBook</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>Success!</h1>\n" +
                        chsFood.toString() +
                        "</body>\n" +
                        "</html>");
            }
        }
    }
}