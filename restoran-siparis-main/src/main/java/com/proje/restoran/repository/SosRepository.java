package com.proje.restoran.repository;

import com.proje.restoran.entity.Sos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SosRepository extends JpaRepository<Sos, Long> {
    Sos findByUrunAdi(String urunAdi);
}