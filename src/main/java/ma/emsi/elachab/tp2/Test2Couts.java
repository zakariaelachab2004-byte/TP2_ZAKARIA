package ma.emsi.elachab.tp2;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.output.Response;

public class Test2Couts {
    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("Erreur : La variable GEMINI_KEY n'est pas définie.");
            return;
        }

        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.8-flash")
                .temperature(0.7)
                .build();

        String question = "Quelle est la capitale du Maroc ?";

        System.out.println("=== Test 2 : Coût en tokens ===");

        // Utilisation de UserMessage.from() pour retourner un objet Response<AiMessage>
        Response<AiMessage> response = model.generate(UserMessage.from(question));

        // Affichage de la réponse
        System.out.println("Réponse du LLM : " + response.content().text());

        // 1. Récupération du nombre de tokens
        int inputTokens = response.tokenUsage().inputTokenCount();
        int outputTokens = response.tokenUsage().outputTokenCount();
        int totalTokens = response.tokenUsage().totalTokenCount();

        System.out.println("\n--- Consommation de tokens ---");
        System.out.println("Tokens d'entrée (Input)  : " + inputTokens);
        System.out.println("Tokens de sortie (Output) : " + outputTokens);
        System.out.println("Total des tokens         : " + totalTokens);

        // 2. Calcul des coûts pour gemini-3.5-flash-lite (0,30 $ / 1M input, 2,50 $ / 1M output)
        double prixMEntreeLite = 0.30;
        double prixMSortieLite = 2.50;

        double coutEntree = (inputTokens / 1_000_000.0) * prixMEntreeLite;
        double coutSortie = (outputTokens / 1_000_000.0) * prixMSortieLite;
        double coutTotal = coutEntree + coutSortie;

        // 3. Nombre de requêtes pour 1 dollar
        long requetesParDollar = (long) (1.0 / coutTotal);

        System.out.println("\n--- Calcul du coût (Modèle gemini-3.5-flash-lite) ---");
        System.out.printf("Coût des tokens d'entrée  : %.8f $\n", coutEntree);
        System.out.printf("Coût des tokens de sortie : %.8f $\n", coutSortie);
        System.out.printf("Coût total de la requête  : %.8f $\n", coutTotal);
        System.out.println("Nombre de requêtes similaires pour 1 $ : ~" + String.format("%,d", requetesParDollar) + " requêtes");
    }
}