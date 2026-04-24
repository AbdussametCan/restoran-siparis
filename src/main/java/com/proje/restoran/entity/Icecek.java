package com.proje.restoran.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ICECEK")
public class Icecek extends Urun {

    @Override
    public String getKategori() {
        return "İçecek";
    }
}