package ma.emsi.elachab.tp2;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.Scanner;

public class Test6Rag2 {

    // Interface pour l'assistant conversationnel
    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("Erreur : La variable d'environnement GEMINI_KEY n'est pas définie.");
            return;
        }

        // 1. Instanciation du modèle avec température faible pour le RAG
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.2)
                .build();

        // 2. Chargement du document PDF
        String nomDocument = "langchain4j.pdf"; // Ajustez le nom de votre fichier PDF ici
        System.out.println("Chargement et découpage du fichier PDF en cours...");
        Document document = FileSystemDocumentLoader.loadDocument(nomDocument);

        // 3. Base vectorielle en mémoire & Ingestion (Easy-RAG)
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(document, embeddingStore);
        System.out.println("Ingestion terminée ! L'assistant est prêt.");

        // 4. Création de l'assistant avec mémoire (10 messages) et recherche documentaire
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        // 5. Boucle d'interaction interactive
        conversationAvec(assistant);
    }

    /**
     * Méthode pour gérer la boucle de conversation avec l'utilisateur via Scanner.
     */
    private static void conversationAvec(Assistant assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("==================================================");
                System.out.println("Posez votre question : ");
                String question = scanner.nextLine();

                if (question.isBlank()) {
                    continue;
                }

                System.out.println("==================================================");

                if ("fin".equalsIgnoreCase(question.trim())) {
                    System.out.println("Fin de la session de chat.");
                    break;
                }

                String reponse = assistant.chat(question);
                System.out.println("Assistant : " + reponse);
                System.out.println("==================================================");
            }
        }
    }
}