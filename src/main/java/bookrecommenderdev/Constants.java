package bookrecommenderdev;

public final class Constants {


  /** Numero di libri risultanti per pagina */
  public static final int PAGE_SIZE = 50;
  /** Numero di valutazioni risultanti per pagina */
  public static final int REVIEWS_PAGE_SIZE = 20;
  /** Numero di librerie risultanti per pagina */
  public static final int LIBRARIES_PAGE_SIZE = 24;
  /** Numero di consigli risultanti per pagina */
  public static final int RECOMMENDATIONS_PAGE_SIZE = 4;

  /** Dimensione massima recensioni testuali */
  public static final int MAX_REVIEW_LENGTH = 256;
  /** Dimensione massima nome */
  public static final int MAX_NAME_LENGTH = 64;
  /** Dimensione massima email */
  public static final int MAX_EMAIL_LENGTH = 256;
  /** Dimensione massima password */
  public static final int MAX_PASSWORD_LENGTH = 256;
  /** Dimensione massima input di ricerca */
  public static final int MAX_SEARCH_LENGTH = 300;
  /** Dimensione massima userid */
  public static final int MAX_USERID_LENGTH = 64;
  /** Dimensione minima password */
  public static final int MIN_PASSWORD_LENGTH = 8;
  /** Dimensione minima userid */
  public static final int MIN_USERID_LENGTH = 8;
  /** Dimensione massima nome di libreria */
  public static final int MAX_LIBRARY_NAME_LENGTH = 64;
}
