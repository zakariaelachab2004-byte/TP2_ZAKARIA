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

public class Test5Rag1 {

    // Assistant conversationnel
    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("La variable d'environnement GEMINI_KEY n'est pas définie.");
            return;
        }

        // Modèle de chat avec une température faible (<= 0.3) pour le RAG
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.2)
                .build();

        // Chargement du document dans une base vectorielle en mémoire
        String nomDocument = "infos.txt";
        Document document = FileSystemDocumentLoader.loadDocument(nomDocument);
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Ingestion : découpage et génération des embeddings (Easy-RAG)
        EmbeddingStoreIngestor.ingest(document, embeddingStore);

        // Instanciation de l'assistant avec mémoire et retriever RAG
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        System.out.println("=== Test 5 : RAG avec document d'information ===");

        // Question 1 : Vérification de l'extraction RAG
        String q1 = "Comment s'appelle le chat de Pierre ?";
        System.out.println("Question : " + q1);
        System.out.println("Réponse  : " + assistant.chat(q1));

        // Question 2 : Test de déduction linguistique + RAG
        System.out.println("\n--- Test 2 : Déduction ---");
        String q2 = "Pierre appelle son chat. Qu'est-ce qu'il pourrait dire ?";
        System.out.println("Question : " + q2);
        System.out.println("Réponse  : " + assistant.chat(q2));
    }
}