package dev.sterner.brewinandchewin.client.recipebook;


public enum KegRecipeBookTab {
    DRINKS("drinks"),
    MISC("misc");

    public final String name;

    private KegRecipeBookTab(String name) {
        this.name = name;
    }

    public static KegRecipeBookTab findByName(String name) {
        KegRecipeBookTab[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            KegRecipeBookTab value = var1[var3];
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