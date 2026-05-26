package com.proje.restoran.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("KIZARTMA")
public class Kizartma extends Urun {

    @Override
    public String getKategori() {
        return "Kızartma";
    }
}