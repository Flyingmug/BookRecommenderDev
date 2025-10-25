package bookrecommenderdev.Auth;

public class AuthService {
//  private final Map<String, Session> activeSessions = new ConcurrentHashMap<>();
//  private final UtenteDao userDAO = new UtenteDao();
//
//  public Pair<Utente, String> login(String email, String password) {
//    Utente user = userDAO.findByEmail(email);
//    if (user == null) return new Pair<>(null, "no-such-user");
//
//    if (!PasswordUtils.verify(password, user.getPasswordHash()))
//      return new Pair<>(null, "wrong-password");
//
//    String token = UUID.randomUUID().toString();
//    activeSessions.put(token, new Session(token, user));
//    return new Pair<>(user, token);
//  }
//
//  public boolean validateSession(String token) {
//    Session session = activeSessions.get(token);
//    return session != null && !session.isExpired();
//  }
//
//  public Utente getUserFromToken(String token) {
//    Session session = activeSessions.get(token);
//    return (session != null && !session.isExpired()) ? session.getUser() : null;
//  }
}
