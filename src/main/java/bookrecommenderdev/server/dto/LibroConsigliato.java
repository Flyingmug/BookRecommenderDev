package bookrecommenderdev.server.dto;

import bookrecommenderdev.model.Libro;

public record LibroConsigliato(Libro libro, int numConsigliato) {
}
