package com.proje.restoran.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TATLI")
public class Tatli extends Urun {

    @Override
    public String getKategori() {
        return "Tatlı";
    }
}