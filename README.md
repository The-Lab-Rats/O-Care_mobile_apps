# O-Care Mobile Apps

Aplikasi ini menggunakan beberapa API untuk beberapa fitur seperti tips, news (berita) kesehatan, dan artikel rekomendasi seputar hasil diagnosa.

Berikut tampilan untuk beberapa fitur yang ada di aplikasi :

* Berita Kesehatan
* Deteksi kesehatan mulut berdasarkan gambar gigi dan lidah
* Artikel rekomendasi seputar hasil diagnosa

# Tata cara menjalankan aplikasi

* Klon Repositori berikut
  
  - git clone https://github.com/The-Lab-Rats/O-Care_mobile_apps

* Sesuaikan paket, gradle, nama file atau folder agar sesuai dengan yang ada di repositori
* cek API apakah sudah sesuai
* Build dan jalankan di IDE yang sesuai (Android) / perangkat android

# Spesifikasi minimum untuk menjalankan aplikasi
* Android version 8.0 - 8.1 Oreo 
* Ram minimum 2gb
* Storage minimum 4gb

# Cara menggunakan aplikasi

Pastikan kalian sudah memberikan izin kepada aplikasi untuk mengakses kamera dan penyimpanan pada perangkat yang digunakan.

- Berita
- Artikel rekomendasi

- Kamera
  - pastikan kamera perangkat yang digunakan jernih / jelas.
- Deteksi Objek
  - Arahkan kamera ke objek (gigi atau lidah) dengan baik dan secara jelas yang agar AI dapat mendeteksi dengan baik objek yang dideteksi.
  - Jika menggunakan gambar yang diambil dari galeri, pastikan gambar yang digunakan jelas terutama pada objek yang ingin dideteksi.
- Setelah gambar dipilih, AI akan secara otomatis melakukan klasifikasi dan akan memberikan hasil diagnosa, penyebab umum mengenai hasil diagnosa, dan rekomendasi mengenai hasil diagnosa.

<img src="gif/full.gif" width="256"/> 
<img src="gif/deteksi.gif" width="256"/>
<img src="gif/berita.gif" width="256"/>
