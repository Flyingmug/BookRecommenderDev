package bookrecommenderdev.routing.history;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;


/** Memoria di elementi precedenti e successivi con limite totale di elementi. Sincronizzazione non gestita. */
public class HistoryManager<T> {

  private final int capacity;
  private final Deque<T> backStack = new ArrayDeque<>();
  private final Deque<T> forwardStack = new ArrayDeque<>();
  private T current;

  /**
   * Crea una memoria di elementi precedenti e successivi avente un limite di elementi totali.
   * La capacità deve essere superiore a 1.
   * @param capacity Capacità della memoria
   */
  public HistoryManager(int capacity) {
    if (capacity < 1) throw new IllegalArgumentException("capacity must be >= 1");
    this.capacity = capacity;
  }

  /** Visita ad un nuovo elemento/pagina. Elimina la memoria successiva. */
  public void visit(T newPage) {
    Objects.requireNonNull(newPage, "newPage");
    if (current != null) {
      backStack.addLast(current);
      ensureCapacity(backStack);
    }
    current = newPage;
    forwardStack.clear();
  }

  /** Torna indietro. Restituisce una istanza {@link Optional} contenente il nuovo elemento corrente, alternativamente vuota se non è possibile ritornare indietro. */
  public Optional<T> back() {
    if (backStack.isEmpty()) return Optional.empty();
    forwardStack.addLast(current);
    current = backStack.removeLast();
    // ensureCapacity(forwardStack);
    return Optional.of(current);
  }

  /** Va avanti. Restituisce una istanza {@link Optional} contenente il nuovo elemento corrente, alternativamente vuota se non è possibile andare avanti. */
  public Optional<T> forward() {
    if (forwardStack.isEmpty()) return Optional.empty();
    backStack.addLast(current);
    ensureCapacity(backStack);
    current = forwardStack.removeLast();
    return Optional.of(current);
  }

  /** Rimpiazza l'elemento corrente. */
  public void replace(T newCurrent) {
    Objects.requireNonNull(newCurrent, "newCurrent");
    current = newCurrent;
  }

  /** Restituisce l'elemento corrente.
   * @return {@link Optional} contenente l'elemento corrente */
  public Optional<T> getCurrent() {
    return Optional.ofNullable(current);
  }

  public boolean canBack() { return !backStack.isEmpty(); }
  public boolean canForward() { return !forwardStack.isEmpty(); }

  /** Elimina gli elementi della cronologia. */
  public void clear() {
    backStack.clear();
    forwardStack.clear();
    current = null;
  }

  /**
   * Rimuove gli elementi iniziali oltre la capacità predeterminata.
   * @param dq Lista riferita
   */
  private void ensureCapacity(Deque<T> dq) {
    while (dq.size() > capacity) {
      dq.removeFirst();
    }
  }
}
