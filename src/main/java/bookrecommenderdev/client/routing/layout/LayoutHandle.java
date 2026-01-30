package bookrecommenderdev.client.routing.layout;

/**
 * Rappresenta un'istanza concreta del controller di un layout caricato.
 *
 * <p>Viene utilizzato solo per scopi di leggibilità del codice</p>.
 * <p>Può essere utilizzato in futuro per gestire parametri di layout</p>.
 *
 * @param controller controller logico del layout
 */
public record LayoutHandle(
    LayoutController controller) {}