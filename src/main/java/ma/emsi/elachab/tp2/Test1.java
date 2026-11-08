package ma.emsi.elachab.tp2;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class Test1 {
    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("Erreur : La variable GEMINI_KEY n'est pas définie.");
            return;
        }

        // Utilisation du modèle actif recommandé : gemini-3.8-flash
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.8-flash")
                .temperature(0.7)
                .build();

        // 1. Question simple
        System.out.println("=== 1. Question simple ===");
        String reponseSimple = model.generate("Quelle est la capitale du Maroc ?");
        System.out.println("Réponse : " + reponseSimple);

        // 2. Test Heure
        System.out.println("\n=== 2. Demande de l'heure ===");
        String questionHeure = "Quelle heure est-il ?";
        String reponseHeure = model.generate(questionHeure);
        System.out.println("Question : " + questionHeure);
        System.out.println("Réponse : " + reponseHeure);

        // 3. Test Mémoire
        System.out.println("\n=== 3. Test de mémoire (sans ChatMemory) ===");
        String q1 = "Bonjour, je m'appelle Zakaria.";
        String r1 = model.generate(q1);
        System.out.println("User : " + q1);
        System.out.println("LLM  : " + r1);

        String q2 = "Quel est mon prénom ?";
        String r2 = model.generate(q2);
        System.out.println("User : " + q2);
        System.out.println("LLM  : " + r2);
    }
}