package com.proje.restoran.controller;

import com.proje.restoran.entity.Masa;
import com.proje.restoran.entity.SepetUrun;
import com.proje.restoran.entity.Siparis;
import com.proje.restoran.entity.Urun;
import com.proje.restoran.repository.IcecekRepository;
import com.proje.restoran.repository.MasaRepository;
import com.proje.restoran.repository.SepetUrunRepository;
import com.proje.restoran.repository.SiparisRepository;
import com.proje.restoran.repository.SosRepository;
import com.proje.restoran.repository.TatliRepository;
import com.proje.restoran.repository.YemekRepository;
import com.proje.restoran.repository.KizartmaRepository;
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
    private IcecekRepository icecekRepository;

    @Autowired
    private SosRepository sosRepository;

    @Autowired
    private TatliRepository tatliRepository;

    @Autowired
    private SepetUrunRepository sepetUrunRepository;

    @Autowired
    private KizartmaRepository kizartmaRepository;

    @Autowired
    private SiparisRepository siparisRepository;

    @Autowired
    private MasaRepository masaRepository;

    @GetMapping("/menu")
    public String menuGoster(@RequestParam int masaNo, Model model) {
        model.addAttribute("masaNo", masaNo);
        model.addAttribute("anaYemekler", yemekRepository.findAll());
        model.addAttribute("icecekler", icecekRepository.findAll());
        model.addAttribute("soslar", sosRepository.findAll());
        model.addAttribute("tatlilar", tatliRepository.findAll());
        model.addAttribute("kizartmalar", kizartmaRepository.findAll());

        return "menu";
    }

    @GetMapping("/sepete-ekle")
    public String sepeteEkle(@RequestParam int masaNo,
                             @RequestParam String yemekAdi) {

        Urun urun = urunBul(yemekAdi);

        if (urun != null) {
            SepetUrun mevcutUrun =
                    sepetUrunRepository.findByMasaNoAndUrunAdi(masaNo, urun.getUrunAdi());

            if (mevcutUrun != null) {
                mevcutUrun.setAdet(mevcutUrun.getAdet() + 1);
                sepetUrunRepository.save(mevcutUrun);
            } else {
                SepetUrun sepetUrun = new SepetUrun();
                sepetUrun.setMasaNo(masaNo);
                sepetUrun.setUrunAdi(urun.getUrunAdi());
                sepetUrun.setAdet(1);
                sepetUrun.setFiyat(urun.getFiyat());
                sepetUrunRepository.save(sepetUrun);
            }
        }

        return "redirect:/menu?masaNo=" + masaNo;
    }

    @GetMapping("/sepet")
    public String sepetGoster(@RequestParam int masaNo, Model model) {
        model.addAttribute("masaNo", masaNo);
        model.addAttribute("sepetUrunleri", sepetUrunRepository.findByMasaNo(masaNo));
        model.addAttribute("toplamTutar", sepetUrunRepository.masaToplamFiyat(masaNo));
        model.addAttribute("oncekiSiparisler", siparisRepository.findByMasaNo(masaNo));

        return "sepet";
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

    @GetMapping("/siparisi-onayla")
    public String siparisiOnayla(@RequestParam int masaNo) {
        List<SepetUrun> sepetUrunleri = sepetUrunRepository.findByMasaNo(masaNo);

        if (sepetUrunleri.isEmpty()) {
            return "redirect:/sepet?masaNo=" + masaNo + "&hata=bos";
        }

        for (SepetUrun urun : sepetUrunleri) {
            Siparis mevcutSiparis =
                    siparisRepository.findByMasaNoAndUrunAdi(masaNo, urun.getUrunAdi());

            if (mevcutSiparis != null) {
                mevcutSiparis.setAdet(mevcutSiparis.getAdet() + urun.getAdet());
                siparisRepository.save(mevcutSiparis);
            } else {
                Siparis siparis = new Siparis();
                siparis.setMasaNo(masaNo);
                siparis.setUrunAdi(urun.getUrunAdi());
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

    @GetMapping("/siparisler")
    public String siparisleriGoster(Model model) {
        List<Masa> masalar = masaRepository.findAll();
        List<MasaSiparisView> masaSiparislari = new ArrayList<>();

        for (Masa masa : masalar) {
            List<Siparis> siparisler = siparisRepository.findByMasaNo(masa.getMasaNo());

            double toplamTutar = 0;
            for (Siparis siparis : siparisler) {
                toplamTutar += siparis.toplamFiyat();
            }

            masaSiparislari.add(new MasaSiparisView(masa.getMasaNo(), siparisler, toplamTutar));
        }

        model.addAttribute("masaSiparislari", masaSiparislari);
        return "siparisler";
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

    @GetMapping("/odeme-alindi")
    public String odemeAlindi(@RequestParam int masaNo) {
        List<Siparis> siparisler = siparisRepository.findByMasaNo(masaNo);
        siparisRepository.deleteAll(siparisler);

        return "redirect:/siparisler";
    }

    private Urun urunBul(String urunAdi) {
        Urun urun = yemekRepository.findByUrunAdi(urunAdi);

        if (urun == null) {
            urun = icecekRepository.findByUrunAdi(urunAdi);
        }

        if (urun == null) {
            urun = sosRepository.findByUrunAdi(urunAdi);
        }

        if (urun == null) {
            urun = kizartmaRepository.findByUrunAdi(urunAdi);
        }

        if (urun == null) {
            urun = tatliRepository.findByUrunAdi(urunAdi);
        }

        return urun;
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