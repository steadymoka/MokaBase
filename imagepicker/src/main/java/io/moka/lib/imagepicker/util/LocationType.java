package io.moka.lib.imagepicker.util;


public enum LocationType {

    INNER(0), EXTERNAL(1), ETC(2), EXTERNAL_NO_MEDIA(3);

    private final int index;

    LocationType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static LocationType get(int index) {
        for (LocationType locationType : LocationType.values())
            if (locationType.getIndex() == index)
                return locationType;
        return EXTERNAL;
    }

}
