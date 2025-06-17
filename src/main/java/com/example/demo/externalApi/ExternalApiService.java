package com.example.demo.externalApi;

public class ExternalApiService {
    /**
     * Simule un appel externe avec un délai et retourne un prix calculé aléatoirement.
     * @param delayMs délai en millisecondes avant le calcul
     * @return prix calculé (double) entre 100 et 3000
     */
    public double calculatePrice(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Calculation interrupted", e);
        }
        // Génération d'un prix aléatoire entre 100 et 3000
        double minPrice = 100.0;
        double maxPrice = 3000.0;
        double randomPrice = minPrice + (Math.random() * (maxPrice - minPrice));
        return Math.round(randomPrice * 100.0) / 100.0; // Arrondi à 2 décimales
    }
} 