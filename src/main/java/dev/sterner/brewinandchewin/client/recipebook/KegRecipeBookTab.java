package dev.sterner.brewinandchewin.client.recipebook;


public enum KegRecipeBookTab {
    DRINKS("drinks"),
    MISC("misc");

    public final String name;

    KegRecipeBookTab(String name) {
        this.name = name;
    }

    public static KegRecipeBookTab findByName(String name) {
        for (KegRecipeBookTab value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }

        return null;
    }

    public String toString() {
        return this.name;
    }
}