package com.example.hibernatenlp;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.util.Objects;

/**
 * @author Petar Tahchiev
 * @since 2.2.2
 */
@Access(AccessType.FIELD)
@Embeddable
public class LocalizedLobValue implements Localized {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 42L;

    /**
     * The {@code value} of the {@link LocalizedLobValue}.
     */
    @Lob
    @Column(length = 50000, name = "val")
    private String value;

    /**
     * A getter method for the {@code value}.
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * A setter method for the {@code value}.
     *
     * @param value The value to set
     */
    @Override
    public void setValue(String value) {
        this.value = value == null ? "" : value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass())) { //NOPMD
            return false;
        }
        LocalizedLobValue that = (LocalizedLobValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public static LocalizedLobValue valueOf(String value) {
        LocalizedLobValue localized = new LocalizedLobValue();
        localized.setValue(value == null ? "" : value);
        return localized;
    }
}
