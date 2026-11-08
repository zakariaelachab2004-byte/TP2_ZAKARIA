package ma.emsi.elachab.tp2;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

public class Test3Traducteur {
    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("Erreur : La variable GEMINI_KEY n'est pas définie.");
            return;
        }

        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.8-flash")
                .temperature(0.3)
                .build();

        // -------------------------------------------------------------
        // Partie 1 : PromptTemplate avec 1 seul paramètre (texte)
        // -------------------------------------------------------------
        System.out.println("=== 1. Traduction en anglais (1 paramètre) ===");

        PromptTemplate template1 = PromptTemplate.from("Traduis le texte suivant en anglais : {{texte}}");

        Map<String, Object> variables1 = new HashMap<>();
        variables1.put("texte", "Bonjour tout le monde, j'espère que vous allez bien.");

        Prompt prompt1 = template1.apply(variables1);
        String reponse1 = model.generate(prompt1.text());

        System.out.println("Prompt appliqué : " + prompt1.text());
        System.out.println("Résultat LLM   : " + reponse1);

        // -------------------------------------------------------------
        // Partie 2 : PromptTemplate avec 2 paramètres (langue et texte)
        // -------------------------------------------------------------
        System.out.println("\n=== 2. Traduction dynamique (2 paramètres) ===");

        PromptTemplate template2 = PromptTemplate.from("Traduis le texte suivant en {{langue}} : {{texte}}");

        Map<String, Object> variables2 = new HashMap<>();
        variables2.put("langue", "espagnol");
        variables2.put("texte", "L'intelligence artificielle transforme l'ingénierie logicielle.");

        Prompt prompt2 = template2.apply(variables2);
        String reponse2 = model.generate(prompt2.text());

        System.out.println("Prompt appliqué : " + prompt2.text());
        System.out.println("Résultat LLM   : " + reponse2);
    }
}