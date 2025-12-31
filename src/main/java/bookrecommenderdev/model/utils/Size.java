package bookrecommenderdev.model.utils;

public enum Size {
  DXS(12),
  XS(14),
  SM(16),
  MD(20),
  LG(24),
  XL(32),
  DXL(40),
  TXL(48);

  private final int value;

  Size(int i) {
    this.value = i;
  }

  // 5. Getter Method to retrieve the value
  public int getValue() {
    return value;
  }
}
