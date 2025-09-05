package com.example.bookrecommenderdev.client.factory;

import com.example.bookrecommenderdev.model.Libro;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public abstract class VBoxFactory {

  /**
   * Genera contenitori {@link VBox} per mostrare dati dalla classe interessata.
   * @param l classe contenente i dati
   * @param onClick metodo da chiamare
   * @return contenitore dei dati organizzati
   */
  public abstract VBox createVBox(Libro l, Consumer<Integer> onClick);

}
