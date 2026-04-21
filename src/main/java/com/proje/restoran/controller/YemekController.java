package com.proje.restoran.controller;

import com.proje.restoran.entity.Masa;
import com.proje.restoran.entity.SepetUrun;
import com.proje.restoran.entity.Siparis;
import com.proje.restoran.entity.Yemek;
import com.proje.restoran.repository.MasaRepository;
import com.proje.restoran.repository.SepetUrunRepository;
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

    @Autowired
    private SepetUrunRepository sepetUrunRepository;

    @GetMapping("/menu")
    public String menuGoster(@RequestParam int masaNo, Model model) {
        model.addAttribute("masaNo", masaNo);
        model.addAttribute("anaYemekler", yemekRepository.findByKategori("Ana Yemek"));
        model.addAttribute("soslar", yemekRepository.findByKategori("Sos"));
        model.addAttribute("icecekler", yemekRepository.findByKategori("İçecek"));
        model.addAttribute("tatlilar", yemekRepository.findByKategori("Tatlı"));
        return "menu";
    }

    @GetMapping("/sepet")
    public String sepetGoster(@RequestParam int masaNo, Model model) {
        model.addAttribute("masaNo", masaNo);
        model.addAttribute("sepetUrunleri", sepetUrunRepository.findByMasaNo(masaNo));
        model.addAttribute("toplamTutar", sepetUrunRepository.masaToplamFiyat(masaNo));
        return "sepet";
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

    @GetMapping("/sepet-arttir")
    public String sepetArttir(@RequestParam Long id) {
        SepetUrun sepetUrun = sepetUrunRepository.findById(id).orElse(null);

        if (sepetUrun != null) {
            sepetUrun.setAdet(sepetUrun.getAdet() + 1);
            sepetUrunRepository.save(sepetUrun);
            return "redirect:/sepet?masaNo=" + sepetUrun.getMasaNo();
        }

        return "redirect:/menu?masaNo=1";
    }

    @GetMapping("/sepet-azalt")
    public String sepetAzalt(@RequestParam Long id) {
        SepetUrun sepetUrun = sepetUrunRepository.findById(id).orElse(null);

        if (sepetUrun != null) {
            int masaNo = sepetUrun.getMasaNo();

            if (sepetUrun.getAdet() > 1) {
                sepetUrun.setAdet(sepetUrun.getAdet() - 1);
                sepetUrunRepository.save(sepetUrun);
            } else {
                sepetUrunRepository.deleteById(id);
            }

            return "redirect:/sepet?masaNo=" + masaNo;
        }

        return "redirect:/menu?masaNo=1";
    }

    @GetMapping("/siparisler")
    public String siparisleriGoster(Model model) {
        List<Masa> masalar = masaRepository.findAll();
        List<MasaSiparisView> masaSiparislari = new ArrayList<>();

        for (Masa masa : masalar) {
            List<Siparis> siparisler = siparisRepository.findByMasaNo(masa.getMasaNo());
            double toplamTutar = 0;
            for (Siparis siparis : siparisler) {
                toplamTutar += siparis.getFiyat() * siparis.getAdet();
            }

            masaSiparislari.add(new MasaSiparisView(masa.getMasaNo(), siparisler, toplamTutar));
        }

        model.addAttribute("masaSiparislari", masaSiparislari);
        return "siparisler";
    }

    @GetMapping("/sepete-ekle")
    public String sepeteEkle(@RequestParam int masaNo,
                             @RequestParam String yemekAdi) {

        Yemek yemek = yemekRepository.findByYemekAdi(yemekAdi);

        if (yemek != null) {
            SepetUrun mevcutUrun = sepetUrunRepository.findByMasaNoAndYemekAdi(masaNo, yemekAdi);

            if (mevcutUrun != null) {
                mevcutUrun.setAdet(mevcutUrun.getAdet() + 1);
                sepetUrunRepository.save(mevcutUrun);
            } else {
                SepetUrun sepetUrun = new SepetUrun();
                sepetUrun.setMasaNo(masaNo);
                sepetUrun.setYemekAdi(yemekAdi);
                sepetUrun.setAdet(1);
                sepetUrun.setFiyat(yemek.getFiyat());
                sepetUrunRepository.save(sepetUrun);
            }
        }

        return "redirect:/menu?masaNo=" + masaNo;
    }

    @GetMapping("/siparisi-onayla")
    public String siparisiOnayla(@RequestParam int masaNo) {

        List<SepetUrun> sepetUrunleri = sepetUrunRepository.findByMasaNo(masaNo);

        // 🔴 SEPET BOŞ KONTROLÜ
        if (sepetUrunleri.isEmpty()) {
            return "redirect:/sepet?masaNo=" + masaNo + "&hata=bos";
        }

        for (SepetUrun urun : sepetUrunleri) {

            Siparis mevcutSiparis = siparisRepository.findByMasaNoAndYemekAdi(masaNo, urun.getYemekAdi());

            if (mevcutSiparis != null) {
                mevcutSiparis.setAdet(mevcutSiparis.getAdet() + urun.getAdet());
                siparisRepository.save(mevcutSiparis);
            } else {
                Siparis siparis = new Siparis();
                siparis.setMasaNo(masaNo);
                siparis.setYemekAdi(urun.getYemekAdi());
                siparis.setAdet(urun.getAdet());
                siparis.setFiyat(urun.getFiyat());
                siparisRepository.save(siparis);
            }
        }

        sepetUrunRepository.deleteAll(sepetUrunleri);

        return "redirect:/siparis-onay?masaNo=" + masaNo;
    }

    @GetMapping("/siparis-onay")
    public String siparisOnay(@RequestParam int masaNo, Model model) {
        model.addAttribute("masaNo", masaNo);
        return "siparis-onay";
    }

    @GetMapping("/odeme-alindi")
    public String odemeAlindi(@RequestParam int masaNo) {
        List<Siparis> siparisler = siparisRepository.findByMasaNo(masaNo);

        siparisRepository.deleteAll(siparisler);

        return "redirect:/siparisler";
    }

    public static class MasaSiparisView {
        private int masaNo;
        private List<Siparis> siparisler;
        private double toplamTutar;

        public MasaSiparisView(int masaNo, List<Siparis> siparisler, double toplamTutar) {
            this.masaNo = masaNo;
            this.siparisler = siparisler;
            this.toplamTutar = toplamTutar;
        }

        public int getMasaNo() {
            return masaNo;
        }

        public List<Siparis> getSiparisler() {
            return siparisler;
        }

        public double getToplamTutar() {
            return toplamTutar;
        }
    }
}