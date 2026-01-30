package bookrecommenderdev.client.auth;

/**
 * Definizione delle policy di accesso alle rotte definite.
 */
public enum AccessPolicy {
  /**
   * Percorso ad accesso libero.
   */
  PUBLIC,

  /**
   * Percorso ad accesso riservato agli utenti autenticati.
   */
  AUTH_ONLY,

  /**
   * Percorso riservato agli utenti non autenticati.
   */
  GUEST_ONLY
}
