package TtattaBackend.ttatta.domain.enums;

public enum CategoryColor {
    RED, ORANGE, YELLOW, GREEN, SKYBLUE, BLUE, INDIGO, VIOLET;

    public static CategoryColor fromString(String color) {
        try {
            return CategoryColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category color: " + color);
        }
    }
}
