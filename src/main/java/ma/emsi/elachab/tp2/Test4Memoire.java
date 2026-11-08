package ma.emsi.elachab.tp2;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;

public class Test4Memoire {

    // Interface pour le service d'assistance conversationnelle
    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("Erreur : La variable GEMINI_KEY n'est pas définie.");
            return;
        }

        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // Création d'une mémoire qui conserve les 10 derniers messages
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Association du modèle et de la mémoire via AiServices
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();

        System.out.println("=== Test 4 : Conversation avec ChatMemory ===");

        // Premier échange : présentation
        String prompt1 = "Bonjour, je m'appelle Zakaria.";
        System.out.println("User : " + prompt1);
        String reponse1 = assistant.chat(prompt1);
        System.out.println("LLM  : " + reponse1);

        // Second échange : vérification du souvenir du prénom
        String prompt2 = "Quel est mon prénom ?";
        System.out.println("\nUser : " + prompt2);
        String reponse2 = assistant.chat(prompt2);
        System.out.println("LLM  : " + reponse2);
    }
}