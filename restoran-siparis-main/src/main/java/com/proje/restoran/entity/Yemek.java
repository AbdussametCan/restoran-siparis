package com.proje.restoran.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("YEMEK")
public class Yemek extends Urun {

    @Override
    public String getKategori() {
        return "Ana Yemek";
    }
}