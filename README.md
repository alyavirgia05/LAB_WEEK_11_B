**Nama:** Alya Virgia Aurelline  
**NIM:** 00000111025  

---

## 1. When a user takes a picture, that picture is stored in a path based on the given URI. In which part of the code handles this?

**Jawab:**

Bagian kode yang “menyimpan” foto ke path berdasarkan URI terjadi saat URI tersebut dilempar ke kontrak `TakePicture()` melalui `launch()`, hal itu ditangani di fungsi `openImageCapture()`:

```kotlin
private fun openImageCapture() {
    photoInfo =
        providerFileManager.generatePhotoUri(System.currentTimeMillis())
    takePictureLauncher.launch(photoInfo!!.uri)
}
```
Di sini generatePhotoUri() membuat objek File dan Uri (lewat FileProvider), lalu takePictureLauncher.launch(photoInfo!!.uri) memberi tahu aplikasi kamera: “tulis hasil foto ke lokasi yang diwakili oleh URI ini.” Sistem kamera yang kemudian benar-benar menyimpan file fisiknya ke path tersebut.

## 2. In your FileInfo.kt, there are 5 attributes. On the first attribute, what does the URI refer to? And on the fourth attribute, what does relativePath refer to?

**Jawab:**

Pada FileInfo, atribut pertama uri: Uri adalah URI yang diberikan ke kamera dan juga dipakai lagi untuk membaca file tersebut. URI ini adalah content://... yang dihasilkan oleh FileProvider.getUriForFile(), yang menunjuk ke file foto atau video di folder private aplikasi (Android/data/com.example.lab_week_11_b/files/Pictures atau Movies sesuai file_provider_paths.xml).

Sementara atribut keempat relativePath: String adalah path relatif di dalam MediaStore, misalnya Environment.DIRECTORY_PICTURES atau Environment.DIRECTORY_MOVIES, yang kemudian dipakai saat mengisi RELATIVE_PATH di ContentValues. Nilai inilah yang menentukan folder tujuan di penyimpanan bersama ketika file disalin ke MediaStore (misalnya muncul di folder Pictures atau Movies yang bisa dilihat di galeri).

## 3. [Bonus] Explain the chronological order from when a user takes a picture until the file is stored in the MediaStore.

**Jawab:**

Urutan kronologisnya dapat dijelaskan sebagai berikut. Ketika pengguna menekan tombol foto, onClickListener pada photo_button akan mengatur isCapturingVideo = false, lalu memanggil checkStoragePermission { openImageCapture() }. Jika izin sudah diberikan atau tidak dibutuhkan (Android 10 ke atas), fungsi openImageCapture() dipanggil; di dalam fungsi ini, providerFileManager.generatePhotoUri(System.currentTimeMillis()) dipanggil untuk membuat objek FileInfo yang berisi File, Uri, nama file, relativePath, dan mimeType.

Di dalam proses tersebut, File dibuat di folder private aplikasi (getExternalFilesDir(Environment.DIRECTORY_PICTURES)) dan Uri dibuat melalui FileHelper.getUriFromFile() yang menggunakan FileProvider. Setelah itu, takePictureLauncher.launch(photoInfo!!.uri) dipanggil sehingga aplikasi kamera terbuka dan hasil foto disimpan ke lokasi yang direpresentasikan oleh Uri tersebut.

Ketika pengguna selesai mengambil foto dan hasilnya sukses, callback takePictureLauncher dieksekusi dan providerFileManager.insertImageToStore(photoInfo) dipanggil. Di dalam fungsi ini, insertToStore() akan dijalankan: entri baru di MediaStore dibuat dengan contentResolver.insert() menggunakan contentUri dan ContentValues yang berisi DISPLAY_NAME, RELATIVE_PATH, dan MIME_TYPE. MediaStore mengembalikan insertedUri, kemudian InputStream dibuka dari fileInfo.uri (file di folder private aplikasi) dan OutputStream dibuka dari insertedUri (lokasi di MediaStore), lalu isi file disalin dengan IOUtils.copy(inputStream, outputStream).

Dengan demikian, foto yang awalnya disimpan di folder private aplikasi akhirnya tersalin dan tersimpan secara permanen di MediaStore pada folder publik sesuai nilai relativePath.
