package bookrecommenderdev.utils;

public enum Size {
  XS(14),
  SM(18),
  MD(24),
  LG(30),
  XL(36),
  DXL(42),
  TXL(50);

  private final int value;

  Size(int i) {
    this.value = i;
  }

  // 5. Getter Method to retrieve the value
  public int getValue() {
    return value;
  }
}
