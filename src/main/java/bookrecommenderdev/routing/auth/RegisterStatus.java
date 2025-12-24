package bookrecommenderdev.routing.auth;

import java.io.Serializable;

public enum RegisterStatus implements Serializable {
  SUCCESS,
  FISCAL_CODE_ALREADY_USED,
  DB_ERROR
}
