# Game Macro (Android / Kotlin)

Game Space esintili, yerel Android başlangıç projesi. Ana ekranda oyun seçici, overlay izni ve erişilebilirlik ayarına yönlendirme bulunur. Yüzen panelde 10 makro satırı ve 1–50 CPS kaydırıcısı vardır. Makro satırındaki düğmeye basılı tutulduğu sürece merkez ekrana dokunma hareketi gönderir; bırakınca durur.

## Açma
1. Android Studio'da klasörü aç.
2. Gradle senkronizasyonunu tamamla.
3. Android 8.0+ cihazda çalıştır.
4. Uygulamada önce "Üstte gösterme izni"ni ver.
5. "Dokunma hizmetini etkinleştir" ile Android Erişilebilirlik ayarlarına git ve Game Macro hizmetini kendin etkinleştir.
6. Oyun seç, ardından makro panelini başlat.

## Önemli sınırlar
- Bu bir başlangıç/MVP projesidir; gerçek cihazda test ve iyileştirme gerekir.
- Şu an tüm 10 makro aynı hedefe (ekran merkezi) tıklar. Konum seçme/sürükle-bırak hedefleme sonraki geliştirme adımıdır.
- Android, oyunlar ve üreticiler bazı erişilebilirlik hareketlerini engelleyebilir. Bazı oyunların hizmet şartları otomatik tıklamayı yasaklayabilir.
- Erişilebilirlik hizmeti güçlü bir izindir; yalnızca kendi cihazında ve güvendiğin kaynaklardan yüklediğin uygulamalarda etkinleştir.
- Google Play'de `QUERY_ALL_PACKAGES` kullanımı politika kısıtlarına tabidir; bu proje tüm uygulamaları listelemek yerine başlatıcı etkinliği olan uygulamaları gösterir.
- Üretim sürümünden önce CPS zamanlaması, foreground service/notification gereksinimleri, ekran yönü ve güvenli durdurma davranışları cihazlarda test edilmelidir.
