package com.medical.rdvservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.rdvservice.dto.SuggestionIaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service d'intégration avec l'API Mistral AI.
 * 
 * Mistral AI est un modèle de langage (LLM) français.
 * Le tier gratuit permet d'envoyer des requêtes via leur API.
 * 
 * Ici, on utilise RestTemplate (fourni par Spring) pour appeler
 * l'API REST de Mistral et obtenir une suggestion de spécialité
 * médicale selon les symptômes décrits.
 */
@Slf4j // Lombok : créé un logger 'log' automatiquement
@Service
@RequiredArgsConstructor
public class MistralService {

    /**
     * Clé API injectée depuis application.yml.
     * ${mistral.api.key} récupère la valeur de la config.
     */
    @Value("${mistral.api.key}")
    private String apiKey;

    @Value("${mistral.api.url}")
    private String apiUrl;

    @Value("${mistral.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Analyse les symptômes et suggère une spécialité médicale.
     * 
     * @param symptomes Description des symptômes par le patient
     * @return SuggestionIaDTO avec la spécialité et l'explication
     */
    public SuggestionIaDTO analyserSymptomes(String symptomes) {

        String systemPrompt = """
                Tu es un assistant médical intelligent. Un patient décrit ses symptômes.
                Ta mission :
                1. Analyser les symptômes
                2. Suggérer la spécialité médicale la plus adaptée parmi :
                   CARDIOLOGIE, DERMATOLOGIE, PEDIATRIE, NEUROLOGIE,
                   OPHTALMOLOGIE, ORL, GENERALISTE, GYNECOLOGIE,
                   ORTHOPEDIE, PSYCHIATRIE
                3. Expliquer brièvement pourquoi (1 phrase)

                Réponds UNIQUEMENT au format JSON suivant :
                {
                  "specialite": "NOM_DE_LA_SPECIALITE",
                  "explication": "Ton explication ici"
                }
                Ne mets aucun texte avant ou après le JSON. Pas de markdown, pas de backticks.
                """;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", symptomes)),
                "temperature", 0.3,
                "max_tokens", 150);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Appel API Mistral pour symptômes : {}", symptomes);

            String response = restTemplate.postForObject(apiUrl, request, String.class);

            // ============================================
            // EXTRACTION DE LA RÉPONSE
            // ============================================
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();

            log.info("Réponse brute Mistral : {}", content);

            // ============================================
            // NETTOYAGE DU MARKDOWN (NOUVEAU !)
            // ============================================
            // L'IA parfois entoure le JSON de ```json ... ```
            // On nettoie pour ne garder que le JSON pur.

            String jsonPropre = nettoyerMarkdown(content);

            // ============================================
            // PARSING DU JSON NETTOYÉ
            // ============================================
            JsonNode iaResponse = objectMapper.readTree(jsonPropre);

            SuggestionIaDTO suggestion = new SuggestionIaDTO();
            suggestion.setSymptomes(symptomes);
            suggestion.setSpecialiteSuggest(
                    iaResponse.path("specialite").asText("GENERALISTE"));
            suggestion.setExplication(
                    iaResponse.path("explication").asText("Consultez un généraliste."));

            return suggestion;

        } catch (Exception e) {
            log.error("Erreur appel API Mistral", e);
            SuggestionIaDTO fallback = new SuggestionIaDTO();
            fallback.setSymptomes(symptomes);
            fallback.setSpecialiteSuggest("GENERALISTE");
            fallback.setExplication(
                    "Impossible d'analyser avec l'IA pour l'instant. " +
                            "Consultez un généraliste pour un premier avis.");
            return fallback;
        }
    }

    /**
     * Nettoie les éventuels blocs Markdown (```json ... ```)
     * pour extraire uniquement le JSON.
     */
    private String nettoyerMarkdown(String texte) {
        if (texte == null || texte.isBlank()) {
            return "{}";
        }

        // Supprime le préfixe ```json ou ``` au début
        String nettoye = texte.trim();
        if (nettoye.startsWith("```json")) {
            nettoye = nettoye.substring(7); // Longueur de "```json"
        } else if (nettoye.startsWith("```")) {
            nettoye = nettoye.substring(3); // Longueur de "```"
        }

        // Supprime le suffixe ``` à la fin
        if (nettoye.endsWith("```")) {
            nettoye = nettoye.substring(0, nettoye.length() - 3);
        }

        return nettoye.trim();
    }

}