package com.example.hibernatenlp;

import java.io.Serializable;

/**
 * @author Petar Tahchiev
 * @since 2.2.2
 */
public interface Localized extends Serializable {

    String getValue();

    void setValue(String value);
}
