package com.proje.restoran.repository;

import com.proje.restoran.entity.SepetUrun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SepetUrunRepository extends JpaRepository<SepetUrun, Long> {

    List<SepetUrun> findByMasaNo(int masaNo);

    SepetUrun findByMasaNoAndUrunAdi(int masaNo, String urunAdi);

    @Query("SELECT COALESCE(SUM(s.adet * s.fiyat), 0) FROM SepetUrun s WHERE s.masaNo = :masaNo")
    double masaToplamFiyat(int masaNo);
}