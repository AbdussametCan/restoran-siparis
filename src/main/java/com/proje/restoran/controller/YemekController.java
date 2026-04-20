package com.proje.restoran.controller;

import com.proje.restoran.entity.Masa;
import com.proje.restoran.entity.Siparis;
import com.proje.restoran.repository.MasaRepository;
import com.proje.restoran.repository.SiparisRepository;
import com.proje.restoran.repository.YemekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class YemekController {

    @Autowired
    private YemekRepository yemekRepository;

    @Autowired
    private SiparisRepository siparisRepository;

    @Autowired
    private MasaRepository masaRepository;

    @GetMapping("/menu")
    public String menuGoster(@RequestParam int masaNo, Model model) {
        model.addAttribute("masaNo", masaNo);
        model.addAttribute("yemekler", yemekRepository.findAll());
        return "menu";
    }

    @GetMapping("/siparis-ver")
    public String siparisVer(@RequestParam int masaNo,
                             @RequestParam String yemekAdi) {

        Siparis mevcutSiparis = siparisRepository.findByMasaNoAndYemekAdi(masaNo, yemekAdi);

        if (mevcutSiparis != null) {
            mevcutSiparis.setAdet(mevcutSiparis.getAdet() + 1);
            siparisRepository.save(mevcutSiparis);
        } else {
            Siparis siparis = new Siparis();
            siparis.setMasaNo(masaNo);
            siparis.setYemekAdi(yemekAdi);
            siparis.setAdet(1);
            siparisRepository.save(siparis);
        }

        return "redirect:/menu?masaNo=" + masaNo;
    }

    @GetMapping("/siparis-sil")
    public String siparisSil(@RequestParam Long id) {
        Siparis siparis = siparisRepository.findById(id).orElse(null);

        if (siparis != null) {
            if (siparis.getAdet() > 1) {
                siparis.setAdet(siparis.getAdet() - 1);
                siparisRepository.save(siparis);
            } else {
                siparisRepository.deleteById(id);
            }
        }

        return "redirect:/siparisler";
    }

    @GetMapping("/siparis-arttir")
    public String siparisArttir(@RequestParam Long id) {
        Siparis siparis = siparisRepository.findById(id).orElse(null);

        if (siparis != null) {
            siparis.setAdet(siparis.getAdet() + 1);
            siparisRepository.save(siparis);
        }

        return "redirect:/siparisler";
    }

    @GetMapping("/siparisler")
    public String siparisleriGoster(Model model) {
        List<Masa> masalar = masaRepository.findAll();
        List<MasaSiparisView> masaSiparislari = new ArrayList<>();

        for (Masa masa : masalar) {
            List<Siparis> siparisler = siparisRepository.findByMasaNo(masa.getMasaNo());
            masaSiparislari.add(new MasaSiparisView(masa.getMasaNo(), siparisler));
        }

        model.addAttribute("masaSiparislari", masaSiparislari);
        return "siparisler";
    }

    public static class MasaSiparisView {
        private int masaNo;
        private List<Siparis> siparisler;

        public MasaSiparisView(int masaNo, List<Siparis> siparisler) {
            this.masaNo = masaNo;
            this.siparisler = siparisler;
        }

        public int getMasaNo() {
            return masaNo;
        }

        public List<Siparis> getSiparisler() {
            return siparisler;
        }
    }
}