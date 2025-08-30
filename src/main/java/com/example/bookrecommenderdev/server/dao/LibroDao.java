package com.example.bookrecommenderdev.server.dao;

import com.example.bookrecommenderdev.model.Libro;

import java.util.List;
import java.util.Optional;

public class LibroDao implements DAO<Libro> {

  @Override
  public Optional<Libro> get(long id) {

    // 1. connessione al database
    // 2. query SELECT tramite id del libro
    // 3. trasformazione del risultato in oggetto di classe libro



    return Optional.empty();
  }

  @Override
  public List<Libro> getAll() {
    return List.of();
  }

  @Override
  public void save(Libro libro) {

  }

  @Override
  public void update(Libro libro, String[] params) {

  }

  @Override
  public void delete(Libro libro) {

  }
}
