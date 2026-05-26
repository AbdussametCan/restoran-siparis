package com.proje.restoran.repository;

import com.proje.restoran.entity.Icecek;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IcecekRepository extends JpaRepository<Icecek, Long> {
    Icecek findByUrunAdi(String urunAdi);
}