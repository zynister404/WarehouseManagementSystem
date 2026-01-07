# 🏭 Warehouse Management System

Aplikasi manajemen gudang dengan GUI menggunakan Kotlin Swing dan MySQL.

## ✨ Fitur Utama
- ✅ CRUD Data Barang
- ✅ Transaksi Barang Masuk/Keluar
- ✅ Laporan Stok Akhir
- ✅ Histori Transaksi

## 🛠️ Teknologi
- Kotlin 1.9.0
- Java Swing
- MySQL 8.0+
- Gradle Build System

## 🚀 Cara Menjalankan

### 1. Setup Database
```sql
-- Jalankan script SQL di phpMyAdmin

CREATE DATABASE IF NOT EXISTS warehouse_db;
USE warehouse_db;

-- Table for storing items (barang)
CREATE TABLE IF NOT EXISTS barang (
    id INT PRIMARY KEY AUTO_INCREMENT,
    kode_barang VARCHAR(50) UNIQUE NOT NULL,
    nama_barang VARCHAR(100) NOT NULL,
    kategori VARCHAR(50),
    stok INT DEFAULT 0,
    satuan VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for transactions (masuk/keluar)
CREATE TABLE IF NOT EXISTS transaksi (
    id INT PRIMARY KEY AUTO_INCREMENT,
    no_dokumen VARCHAR(100) NOT NULL,
    tanggal DATETIME NOT NULL,
    jenis ENUM('MASUK', 'KELUAR') NOT NULL,
    id_barang INT NOT NULL,
    jumlah INT NOT NULL,
    keterangan TEXT,
    operator VARCHAR(50),
    FOREIGN KEY (id_barang) REFERENCES barang(id)
);
