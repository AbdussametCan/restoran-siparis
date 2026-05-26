package com.proje.restoran.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SOS")
public class Sos extends Urun {

    @Override
    public String getKategori() {
        return "Sos";
    }
}