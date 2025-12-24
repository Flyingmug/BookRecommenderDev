package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.factory.LibraryItemFactory;
import bookrecommenderdev.model.Libreria;
import bookrecommenderdev.model.auth.AuthContext;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.server.dto.LibraryResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LibrariesController implements Routable {

  @FXML private FlowPane librariesContainer;

  AppContext context;

  @Override
  public void onRoute(Map<String, String> params, AppContext context) {
    this.context = context;
    resolveLibraries();
  }

  private void resolveLibraries() {

//    if (!AuthContext.isAuthenticated()) return;   // router shouldn't allow to be here regardless
    long userId = 3;

    try {

      List<LibraryResult> librerie = context.server().getListLibrerie(userId);

      if (librerie == null) {
        System.out.println("UI error retrieving libraries");
        return;
      }
      System.out.println("UI Libraries found: " + librerie.size());

//
//      librerie = new LinkedList<>();
//      librerie.add(new LibraryResult(
//          new Libreria(1, 1, "temp1"),
//          50
//      ));
//      librerie.add(new LibraryResult(
//          new Libreria(1, 1, "temp1"),
//          9
//      ));
//      librerie.add(new LibraryResult(
//          new Libreria(1, 1, "temp1"),
//          78456
//      ));
//      librerie.add(new LibraryResult(
//          new Libreria(1, 1, "temp1"),
//          43256
//      ));librerie.add(new LibraryResult(
//          new Libreria(1, 1, "temp1"),
//          612
//      ));librerie.add(new LibraryResult(
//          new Libreria(1, 1, "temp1"),
//          12
//      ));
//      librerie.add(new LibraryResult(
//          new Libreria(1, 1, "temp1"),
//          345
//      ));

      load(librerie);

    } catch (RemoteException e) {
      e.printStackTrace();
    }

  }

  private void load(List<LibraryResult> libraries) {

    for (LibraryResult libr : libraries) {
      librariesContainer.getChildren().add(
          LibraryItemFactory.createLibraryItem(libr.library(), libr.bookCount())
      );
    }

  }


  @FXML
  protected void onLibraryOpen() {

  }


  @FXML
  protected void onLibraryDelete() {

  }

  @FXML
  protected void onLibraryCreate() {

  }

  @FXML
  protected void onLibraryInsert() {

  }

}
