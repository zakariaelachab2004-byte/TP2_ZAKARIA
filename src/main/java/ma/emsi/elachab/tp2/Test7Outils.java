package ma.emsi.elachab.tp2;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

public class Test7Outils {

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("La variable GEMINI_KEY n'est pas définie.");
            return;
        }

        // Configuration du modèle avec logging des requêtes et réponses JSON
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.2)
                .logRequestsAndResponses(true) // Activation du logging d'outils
                .build();

        // Création de l'assistant doté des outils météo
        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .tools(new MeteoTool()) // Intégration de l'outil
                .build();

        System.out.println("=== Test 7 : Assistant avec Outils Météo ===");
        conversationAvec(assistant);
    }

    private static void conversationAvec(AssistantMeteo assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n==================================================");
                System.out.println("Posez votre question : ");
                String question = scanner.nextLine();

                if (question.isBlank()) {
                    continue;
                }

                if ("fin".equalsIgnoreCase(question.trim())) {
                    System.out.println("Fin de la session.");
                    break;
                }

                String reponse = assistant.chat(question);
                System.out.println("\nAssistant : " + reponse);
            }
        }
    }
}