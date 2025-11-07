package bookrecommenderdev.animations;

import java.util.LinkedList;
import java.util.List;

/**
 * Classe utilitaria per la definizione di un keyframe.
 * Keyframe: coppia (istante di tempo, valore)
 */
class Keyframe {
  double time;   // milliseconds
  double value;

  Keyframe(double time, double value) {
    this.time = time;
    this.value = value;
  }
}

/**
 * Classe utilitaria per la gestione di una proprietà animabile tramite {@link Keyframe keyframe}
 */
class AnimatedProperty {
  private double baseValue;
  private final List<Keyframe> keyframes = new LinkedList<>();

  /**
   * Definisce una proprietà animabile via uso di {@link Keyframe keyframe}.
   * @param v valore di base.
   */
  public AnimatedProperty(double v) {
    this.baseValue = v;
  }

  public double getBaseValue() { return baseValue; }
  public void setBaseValue(double v) { baseValue = v; }

  /**
   * Aggiunge un keyframe {@link Keyframe} alla lista di keyframe definiti per la proprietà.
   * @param time istante di tempo.
   * @param value valore acquisito dalla proprietà all'istante di tempo.
   */
  public void addKeyframe(double time, double value) {
    keyframes.add(new Keyframe(time, value));
  }

  /**
   * Verifica la presenza di keyframe definiti per la proprietà.
   * @return presenza di animazioni definite per l'oggetto.
   */
  public boolean isAnimated() {
    return !keyframes.isEmpty();
  }

  /**
   * Restituisce il valore acquisito dalla proprietà in un determinato istante di tempo,
   * calcolato secondo la funzione di easing.
   * @param currentTime istante di tempo.
   * @return valore della proprietà.
   */
  public double getValue(double currentTime) {
    if (keyframes.isEmpty()) return baseValue;
    if (currentTime < keyframes.getFirst().time || currentTime > keyframes.getLast().time) return baseValue;

    // find the surrounding keyframes
    for (int i = 0; i < keyframes.size() - 1; i++) {
      Keyframe k1 = keyframes.get(i);
      Keyframe k2 = keyframes.get(i + 1);
      if (currentTime >= k1.time && currentTime <= k2.time) {
        double t = (currentTime - k1.time) / (k2.time - k1.time);
        // linear interpolation
        return easing(k1.value, k2.value, t);
      }
    }
    return baseValue; // fallback
  }

  private double easing(double a, double b, double t) {
    // linear interpolation (can be replaced with easing later)
    return a + (b - a) * t;
  }
}
