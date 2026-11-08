# Journal d'utilisation de l'IA - TP2 : LangChain4j & RAG

## Utilisation 1 : Résolution du conflit de version (NoClassDefFoundError avec Apache Tika)

* **Problème rencontré :** Lors du lancement des tests RAG (Test 5), le programme a planté avec une erreur `java.lang.NoClassDefFoundError: org/apache/commons/io/input/ChecksumInputStream` liée à Apache Tika et LangChain4j Easy-RAG.
* **Raison de l'utilisation :** Je ne savais pas quelle dépendance Maven manquait ou était en conflit de version dans mon `pom.xml` pour faire fonctionner le chargeur de documents Tika.
* **Prompt utilisé :** *"J'ai cette erreur avec LangChain4j Easy-RAG et Apache Tika : java.lang.NoClassDefFoundError: org/apache/commons/io/input/ChecksumInputStream. Comment la résoudre ?"*
* **Réponse de l'IA :** L'IA m'a expliqué que la version de `commons-io` par défaut était trop ancienne (2.14.0) pour la version 2.9.1 d'Apache Tika et m'a conseillé de forcer l'ajout de la dépendance `commons-io` en version `2.16.1` dans le fichier `pom.xml`.
* **Ce que j'ai appris :** J'ai compris que les modules de parsing de documents (comme Tika intégrés à Easy-RAG) s'appuient en profondeur sur des bibliothèques utilitaires comme Apache Commons IO, et qu'il est parfois nécessaire de sur-iviller explicitement la version d'une dépendance transitive dans Maven en cas de mise à jour majeure du framework.

---

## Utilisation 2 : Correction d'un MALFORMED_FUNCTION_CALL lors de l'appel d'outils (Test 7)

* **Problème rencontré :** Lors de l'exécution du Test 7 avec l'outil de géolocalisation Open-Meteo (`getCoordonnees`), le LLM renvoyait une erreur `MALFORMED_FUNCTION_CALL` et plantait avec un `NullPointerException` lorsqu'on testait avec une ville inconnue comme "Zzyzx".
* **Raison de l'utilisation :** Je ne comprenais pas pourquoi le retour de la méthode Java sous forme de tableau `double[]` posait problème au LLM lors du binding de l'outil.
* **Prompt utilisé :** *"Pourquoi est-ce que j'ai un MALFORMED_FUNCTION_CALL et un NullPointerException quand le LLM appelle getCoordonnees avec LangChain4j et Gemini ?"*
* **Réponse de l'IA :** L'IA m'a indiqué que le modèle Gemini s'attend à recevoir un objet JSON structuré (avec des attributs nommés comme `latitude` et `longitude`) plutôt qu'un tableau brut de primitives, afin de mapper correctement les arguments. Elle m'a suggéré de créer une classe interne `Coordonnees` dans mon outil.
* **Ce que j'ai appris :** J'ai appris que les LLMs et les frameworks de Function Calling comme LangChain4j interprètent beaucoup plus proprement les objets typés dotés de champs explicites que les types de tableaux bruts, ce qui évite les erreurs de parsing JSON dans les réponses de l'API Gemini.