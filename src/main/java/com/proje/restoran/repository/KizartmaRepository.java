package com.proje.restoran.repository;

import com.proje.restoran.entity.Kizartma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KizartmaRepository extends JpaRepository<Kizartma, Long> {
    Kizartma findByUrunAdi(String urunAdi);
}