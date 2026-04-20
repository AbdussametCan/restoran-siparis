package com.proje.restoran.repository;

import com.proje.restoran.entity.Yemek;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YemekRepository extends JpaRepository<Yemek, Long> {
}