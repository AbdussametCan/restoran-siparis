package com.proje.restoran.repository;

import com.proje.restoran.entity.Masa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasaRepository extends JpaRepository<Masa, Long> {
    Masa findByMasaNo(int masaNo);
}