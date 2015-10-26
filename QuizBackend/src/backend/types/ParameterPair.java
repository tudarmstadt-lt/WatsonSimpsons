package backend.types;

/**
 * Helper-Class for Parameter Key-Value Pair
 *
 * @author dath
 */
public class ParameterPair {

    String key;
    String value;

    public ParameterPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key+"="+value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
