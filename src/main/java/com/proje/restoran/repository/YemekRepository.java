package com.proje.restoran.repository;

import com.proje.restoran.entity.Yemek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YemekRepository extends JpaRepository<Yemek, Long> {

    List<Yemek> findByKategori(String kategori);

    Yemek findByYemekAdi(String yemekAdi);
}