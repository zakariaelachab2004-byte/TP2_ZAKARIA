package ma.emsi.elachab.tp2;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Scanner;

public class MeteoTool {

    // Classe interne pour retourner un objet JSON propre au LLM
    public static class Coordonnees {
        public double latitude;
        public double longitude;

        public Coordonnees(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return "latitude=" + latitude + ", longitude=" + longitude;
        }
    }

    @Tool("""
        Retourne true si la probabilité d'avoir de la pluie pour la ville dont on donne la latitude et la longitude
        est importante durant une période de nbJours jours. La période commence nbJoursDebut jours après aujourd'hui.
        Par exemple, si nbJoursDebut = 1 et nbJours = 2, la période de 2 jours commence demain (1 jour après aujourd'hui).
        Si nbJoursDebut = 0 et nbJours = 3, la période de 3 jours commence aujourd'hui.
        Retourne false sinon.
        """)
    public boolean isPluiePrevue(
            @P("Latitude de la ville") double latitude,
            @P("Longitude de la ville") double longitude,
            @P("Nombre de jours après aujourd'hui pour indiquer le début de la période") int nbJoursDebut,
            @P("Nombre de jours de la période") int nbJours) {

        double seuilPluie = 25.0; // Seuil de tolérance pluie en %

        try {
            String baseUrl = "https://api.open-meteo.com/v1/forecast";
            int finPeriode = Math.min(nbJoursDebut + nbJours, 7);
            String apiUri = String.format(Locale.US,
                    "%s?latitude=%f&longitude=%f&daily=precipitation_probability_max&forecast_days=%d",
                    baseUrl, latitude, longitude, finPeriode);

            HttpURLConnection connection = (HttpURLConnection) new URI(apiUri).toURL().openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonObject daily = jsonResponse.getAsJsonObject("daily");
            int[] precipitationProbabilities = gson.fromJson(daily.get("precipitation_probability_max"), int[].class);

            for (int i = nbJoursDebut; i < finPeriode; i++) {
                if (precipitationProbabilities[i] > seuilPluie) {
                    return true;
                }
            }
            return false;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Erreur lors de la récupération de la météo pour (" + latitude + ", " + longitude + ") : " + e.getMessage(), e);
        }
    }

    @Tool("Retourne les coordonnées (latitude et longitude) d'une ville à partir de son nom")
    public Coordonnees getCoordonnees(
            @P("Nom de la ville (ex: Paris, Rabat, London)") String ville) {
        try {
            String baseUrl = "https://geocoding-api.open-meteo.com/v1/search?name=";
            String apiUri = baseUrl + ville;

            HttpURLConnection connection = (HttpURLConnection) new URI(apiUri).toURL().openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

            if (jsonResponse.has("results")) {
                JsonArray results = jsonResponse.getAsJsonArray("results");
                if (!results.isEmpty()) {
                    JsonObject premierResultat = results.get(0).getAsJsonObject();
                    double latitude = premierResultat.get("latitude").getAsDouble();
                    double longitude = premierResultat.get("longitude").getAsDouble();
                    return new Coordonnees(latitude, longitude);
                }
            }
            throw new RuntimeException("Aucune coordonnée trouvée pour la ville : " + ville);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Erreur lors de la géolocalisation de la ville : " + ville, e);
        }
    }
}