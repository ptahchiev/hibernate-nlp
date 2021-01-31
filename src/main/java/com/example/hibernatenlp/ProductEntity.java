package com.example.hibernatenlp;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Petar Tahchiev
 * @since 2.2.2
 */
@Entity(name = "product")
public class ProductEntity {

    public static final String NAME = "product";

    @Id
    private Long id;

    @Column(name = "code")
    private String code;

    @ElementCollection(targetClass = LocalizedValue.class, fetch = FetchType.LAZY)
    @CollectionTable(name = (ProductEntity.NAME +"_name_lv"), joinColumns = {
                    @JoinColumn(name = (ProductEntity.NAME +"_id"))
    }, indexes = {
                    @Index(name = (("idx_"+ ProductEntity.NAME)+"_name_lv"), columnList = (ProductEntity.NAME +"_id"))
    }, foreignKey = @ForeignKey(name = (("fk_"+ ProductEntity.NAME)+"_name_lv")))
    @MapKeyColumn(name = "locale")
    private Map<Locale, LocalizedValue> name = new HashMap<>();

    @ElementCollection(targetClass = LocalizedLobValue.class)
    @CollectionTable(name = (ProductEntity.NAME +"_description_lv"), joinColumns = {
                    @JoinColumn(name = (ProductEntity.NAME +"_id"))
    }, indexes = {
                    @Index(name = (("idx_"+ ProductEntity.NAME)+"_description_lv"), columnList = (ProductEntity.NAME +"_id"))
    }, foreignKey = @ForeignKey(name = (("fk_"+ ProductEntity.NAME)+"_description_lv")))
    @MapKeyColumn(name = "locale")
    private Map<Locale, LocalizedLobValue> description = new HashMap<>();


    @Column(name = "price")
    private Double price;

    /* getters/setters */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<Locale, LocalizedValue> getName() {
        return name;
    }

    public String getName(Locale locale) {
        return getLocalizedValue(locale, name);
    }

    public void setName(Map<Locale, LocalizedValue> name) {
        this.name = name;
    }

    public Map<Locale, LocalizedLobValue> getDescription() {
        return description;
    }

    public void setDescription(Map<Locale, LocalizedLobValue> description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    /* protected helpers */
    protected synchronized <V extends Localized> String getLocalizedValue(Locale locale, Map<Locale, V> lvMap) {
        V lv = lvMap.get(locale);
        if (lv == null && locale != null) {
            Locale fallback = new Locale.Builder().setLanguage(locale.getLanguage()).build();
            lv = lvMap.get(fallback);
        }
        return lv == null ? "" : lv.getValue();
    }
}
