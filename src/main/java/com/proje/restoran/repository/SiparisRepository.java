package com.proje.restoran.repository;

import com.proje.restoran.entity.Siparis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiparisRepository extends JpaRepository<Siparis, Long> {
    List<Siparis> findByMasaNo(int masaNo);

    Siparis findByMasaNoAndYemekAdi(int masaNo, String yemekAdi);
}