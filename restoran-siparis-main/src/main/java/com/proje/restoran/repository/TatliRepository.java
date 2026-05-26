package com.proje.restoran.repository;

import com.proje.restoran.entity.Tatli;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TatliRepository extends JpaRepository<Tatli, Long> {
    Tatli findByUrunAdi(String urunAdi);
}