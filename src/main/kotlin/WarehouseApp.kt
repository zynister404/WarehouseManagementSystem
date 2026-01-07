package warehouse.app

import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.*
import java.text.SimpleDateFormat
import java.util.*
import java.sql.*


class WarehouseApp : JFrame("Warehouse Management System") {

    private lateinit var conn: Connection


    private val tabbedPane = JTabbedPane()


    private val barangPanel = JPanel(BorderLayout())
    private val barangTableModel = DefaultTableModel()
    private val barangTable = JTable(barangTableModel)
    private val barangScrollPane = JScrollPane(barangTable)

    private val barangFormPanel = JPanel(GridBagLayout())
    private val txtKodeBarang = JTextField(20)
    private val txtNamaBarang = JTextField(20)
    private val txtKategori = JTextField(20)
    private val txtStok = JTextField(20)
    private val txtSatuan = JTextField(20)

    private val transaksiPanel = JPanel(BorderLayout())
    private val transaksiTableModel = DefaultTableModel()
    private val transaksiTable = JTable(transaksiTableModel)
    private val transaksiScrollPane = JScrollPane(transaksiTable)

    private val transaksiFormPanel = JPanel(GridBagLayout())
    private val txtNoDokumen = JTextField(20)
    private val cmbJenis = JComboBox(arrayOf("MASUK", "KELUAR"))
    private val cmbBarang = JComboBox<String>()
    private val txtJumlah = JTextField(20)
    private val txtKeterangan = JTextField(20)
    private val txtOperator = JTextField(20)


    private val laporanPanel = JPanel(BorderLayout())
    private val stokTableModel = DefaultTableModel()
    private val stokTable = JTable(stokTableModel)
    private val stokScrollPane = JScrollPane(stokTable)


    private val btnTambahBarang = JButton("Tambah")
    private val btnUbahBarang = JButton("Ubah")
    private val btnHapusBarang = JButton("Hapus")
    private val btnResetBarang = JButton("Reset")

    private val btnTambahTransaksi = JButton("Tambah")
    private val btnUbahTransaksi = JButton("Ubah")
    private val btnHapusTransaksi = JButton("Hapus")
    private val btnResetTransaksi = JButton("Reset")

    private val btnRefreshLaporan = JButton("Refresh Laporan")


    private val barangMap = mutableMapOf<String, Int>()

    init {
        setupDatabase()
        initUI()
        loadBarangData()
        loadTransaksiData()
        loadLaporanData()
        setupListeners()
    }

    private fun setupDatabase() {
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this,
                "Gagal terkoneksi ke database!\nPastikan MySQL/XAMPP berjalan.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE)
            JOptionPane.showMessageDialog(this,
                "Gagal terkoneksi ke database!\nPastikan MySQL/XAMPP berjalan.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE)
            System.exit(1)
        }
        conn = DatabaseConnection.getConnection()
    }

    private fun initUI() {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(1200, 700)
        setLocationRelativeTo(null)

        // Setup Menu Bar
        setupMenuBar()

        // Setup Tabbed Pane
        setupBarangPanel()
        setupTransaksiPanel()
        setupLaporanPanel()

        tabbedPane.addTab("Master Barang", barangPanel)
        tabbedPane.addTab("Transaksi", transaksiPanel)
        tabbedPane.addTab("Laporan Stok", laporanPanel)

        add(tabbedPane, BorderLayout.CENTER)
    }

    private fun setupMenuBar() {
        val menuBar = JMenuBar()

        val fileMenu = JMenu("File")
        val exitItem = JMenuItem("Exit")
        exitItem.addActionListener { System.exit(0) }
        fileMenu.add(exitItem)

        val helpMenu = JMenu("Help")
        val aboutItem = JMenuItem("About")
        aboutItem.addActionListener {
            JOptionPane.showMessageDialog(this,
                "Warehouse Management System v1.0\nDibuat Oleh Ahmad Afandy",
                "About",
                JOptionPane.INFORMATION_MESSAGE)
        }
        helpMenu.add(aboutItem)

        menuBar.add(fileMenu)
        menuBar.add(helpMenu)
        jMenuBar = menuBar
    }

    private fun setupBarangPanel() {
        // Setup Table
        barangTableModel.setColumnIdentifiers(arrayOf(
            "ID", "Kode Barang", "Nama Barang", "Kategori", "Stok", "Satuan"
        ))

        // Setup Form
        val gbc = GridBagConstraints()
        gbc.insets = Insets(5, 5, 5, 5)
        gbc.fill = GridBagConstraints.HORIZONTAL

        gbc.gridx = 0; gbc.gridy = 0
        barangFormPanel.add(JLabel("Kode Barang:"), gbc)
        gbc.gridx = 1
        barangFormPanel.add(txtKodeBarang, gbc)

        gbc.gridx = 0; gbc.gridy = 1
        barangFormPanel.add(JLabel("Nama Barang:"), gbc)
        gbc.gridx = 1
        barangFormPanel.add(txtNamaBarang, gbc)

        gbc.gridx = 0; gbc.gridy = 2
        barangFormPanel.add(JLabel("Kategori:"), gbc)
        gbc.gridx = 1
        barangFormPanel.add(txtKategori, gbc)

        gbc.gridx = 0; gbc.gridy = 3
        barangFormPanel.add(JLabel("Stok:"), gbc)
        gbc.gridx = 1
        barangFormPanel.add(txtStok, gbc)

        gbc.gridx = 0; gbc.gridy = 4
        barangFormPanel.add(JLabel("Satuan:"), gbc)
        gbc.gridx = 1
        barangFormPanel.add(txtSatuan, gbc)

        // Button Panel
        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 10))
        buttonPanel.add(btnTambahBarang)
        buttonPanel.add(btnUbahBarang)
        buttonPanel.add(btnHapusBarang)
        buttonPanel.add(btnResetBarang)

        val formContainer = JPanel(BorderLayout())
        formContainer.add(barangFormPanel, BorderLayout.CENTER)
        formContainer.add(buttonPanel, BorderLayout.SOUTH)

        barangPanel.add(barangScrollPane, BorderLayout.CENTER)
        barangPanel.add(formContainer, BorderLayout.SOUTH)
    }

    private fun setupTransaksiPanel() {
        // Setup Table
        transaksiTableModel.setColumnIdentifiers(arrayOf(
            "ID", "No Dokumen", "Tanggal", "Jenis", "Barang", "Jumlah", "Keterangan", "Operator"
        ))

        // Setup Form
        val gbc = GridBagConstraints()
        gbc.insets = Insets(5, 5, 5, 5)
        gbc.fill = GridBagConstraints.HORIZONTAL

        gbc.gridx = 0; gbc.gridy = 0
        transaksiFormPanel.add(JLabel("No Dokumen:"), gbc)
        gbc.gridx = 1
        transaksiFormPanel.add(txtNoDokumen, gbc)

        gbc.gridx = 0; gbc.gridy = 1
        transaksiFormPanel.add(JLabel("Jenis:"), gbc)
        gbc.gridx = 1
        transaksiFormPanel.add(cmbJenis, gbc)

        gbc.gridx = 0; gbc.gridy = 2
        transaksiFormPanel.add(JLabel("Barang:"), gbc)
        gbc.gridx = 1
        transaksiFormPanel.add(cmbBarang, gbc)

        gbc.gridx = 0; gbc.gridy = 3
        transaksiFormPanel.add(JLabel("Jumlah:"), gbc)
        gbc.gridx = 1
        transaksiFormPanel.add(txtJumlah, gbc)

        gbc.gridx = 0; gbc.gridy = 4
        transaksiFormPanel.add(JLabel("Keterangan:"), gbc)
        gbc.gridx = 1
        transaksiFormPanel.add(txtKeterangan, gbc)

        gbc.gridx = 0; gbc.gridy = 5
        transaksiFormPanel.add(JLabel("Operator:"), gbc)
        gbc.gridx = 1
        transaksiFormPanel.add(txtOperator, gbc)

        // Button Panel
        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 10))
        buttonPanel.add(btnTambahTransaksi)
        buttonPanel.add(btnUbahTransaksi)
        buttonPanel.add(btnHapusTransaksi)
        buttonPanel.add(btnResetTransaksi)

        val formContainer = JPanel(BorderLayout())
        formContainer.add(transaksiFormPanel, BorderLayout.CENTER)
        formContainer.add(buttonPanel, BorderLayout.SOUTH)

        transaksiPanel.add(transaksiScrollPane, BorderLayout.CENTER)
        transaksiPanel.add(formContainer, BorderLayout.SOUTH)
    }

    private fun setupLaporanPanel() {
        stokTableModel.setColumnIdentifiers(arrayOf(
            "Kode Barang", "Nama Barang", "Kategori", "Stok Akhir", "Satuan"
        ))

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        buttonPanel.add(btnRefreshLaporan)

        laporanPanel.add(stokScrollPane, BorderLayout.CENTER)
        laporanPanel.add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun loadBarangData() {
        barangMap.clear()
        cmbBarang.removeAllItems()

        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            barangTableModel.rowCount = 0

            stmt = conn.prepareStatement("SELECT * FROM barang ORDER BY kode_barang")
            rs = stmt.executeQuery()

            while (rs.next()) {
                barangTableModel.addRow(arrayOf<Any>(
                    rs.getInt("id"),
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("kategori"),
                    rs.getInt("stok"),
                    rs.getString("satuan")
                ))

                // Add to combobox
                val display = "${rs.getString("kode_barang")} - ${rs.getString("nama_barang")} (Stok: ${rs.getInt("stok")})"
                cmbBarang.addItem(display)
                barangMap[display] = rs.getInt("id")
            }
        } catch (e: SQLException) {
            showError("Error loading barang: ${e.message}")
        } finally {
            DatabaseConnection.closeConnection(null, stmt, rs)
        }
    }

    private fun loadTransaksiData() {
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            transaksiTableModel.rowCount = 0

            val query = """
                SELECT t.*, b.kode_barang, b.nama_barang 
                FROM transaksi t 
                JOIN barang b ON t.id_barang = b.id 
                ORDER BY t.tanggal DESC
            """

            stmt = conn.prepareStatement(query)
            rs = stmt.executeQuery()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

            while (rs.next()) {
                transaksiTableModel.addRow(arrayOf<Any>(
                    rs.getInt("id"),
                    rs.getString("no_dokumen"),
                    dateFormat.format(rs.getTimestamp("tanggal")),
                    rs.getString("jenis"),
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah"),
                    rs.getString("keterangan") ?: "",
                    rs.getString("operator") ?: ""
                ))
            }
        } catch (e: SQLException) {
            showError("Error loading transaksi: ${e.message}")
        } finally {
            DatabaseConnection.closeConnection(null, stmt, rs)
        }
    }

    private fun loadLaporanData() {
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            stokTableModel.rowCount = 0

            stmt = conn.prepareStatement("""
                SELECT kode_barang, nama_barang, kategori, stok, satuan 
                FROM barang 
                ORDER BY kategori, nama_barang
            """)
            rs = stmt.executeQuery()

            while (rs.next()) {
                stokTableModel.addRow(arrayOf<Any>(
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("kategori"),
                    rs.getInt("stok"),
                    rs.getString("satuan")
                ))
            }
        } catch (e: SQLException) {
            showError("Error loading laporan: ${e.message}")
        } finally {
            DatabaseConnection.closeConnection(null, stmt, rs)
        }
    }

    private fun setupListeners() {
        // Barang Listeners
        btnTambahBarang.addActionListener { tambahBarang() }
        btnUbahBarang.addActionListener { ubahBarang() }
        btnHapusBarang.addActionListener { hapusBarang() }
        btnResetBarang.addActionListener { resetFormBarang() }

        // Transaksi Listeners
        btnTambahTransaksi.addActionListener { tambahTransaksi() }
        btnUbahTransaksi.addActionListener { ubahTransaksi() }
        btnHapusTransaksi.addActionListener { hapusTransaksi() }
        btnResetTransaksi.addActionListener { resetFormTransaksi() }

        // Laporan Listener
        btnRefreshLaporan.addActionListener { loadLaporanData() }

        // Table Selection Listener
        barangTable.selectionModel.addListSelectionListener {
            if (!it.valueIsAdjusting && barangTable.selectedRow != -1) {
                val selectedRow = barangTable.selectedRow
                txtKodeBarang.text = barangTableModel.getValueAt(selectedRow, 1).toString()
                txtNamaBarang.text = barangTableModel.getValueAt(selectedRow, 2).toString()
                txtKategori.text = barangTableModel.getValueAt(selectedRow, 3).toString()
                txtStok.text = barangTableModel.getValueAt(selectedRow, 4).toString()
                txtSatuan.text = barangTableModel.getValueAt(selectedRow, 5).toString()
            }
        }

        transaksiTable.selectionModel.addListSelectionListener {
            if (!it.valueIsAdjusting && transaksiTable.selectedRow != -1) {
                val selectedRow = transaksiTable.selectedRow
                txtNoDokumen.text = transaksiTableModel.getValueAt(selectedRow, 1).toString()
                cmbJenis.selectedItem = transaksiTableModel.getValueAt(selectedRow, 3).toString()

                // Find barang index
                val barangName = transaksiTableModel.getValueAt(selectedRow, 4).toString()
                for (i in 0 until cmbBarang.itemCount) {
                    if (cmbBarang.getItemAt(i).contains(barangName)) {
                        cmbBarang.selectedIndex = i
                        break
                    }
                }

                txtJumlah.text = transaksiTableModel.getValueAt(selectedRow, 5).toString()
                txtKeterangan.text = transaksiTableModel.getValueAt(selectedRow, 6).toString()
                txtOperator.text = transaksiTableModel.getValueAt(selectedRow, 7).toString()
            }
        }
    }

    private fun tambahBarang() {
        if (validateBarangForm()) {
            var stmt: PreparedStatement? = null

            try {
                stmt = conn.prepareStatement("""
                    INSERT INTO barang (kode_barang, nama_barang, kategori, stok, satuan) 
                    VALUES (?, ?, ?, ?, ?)
                """)

                stmt.setString(1, txtKodeBarang.text.trim())
                stmt.setString(2, txtNamaBarang.text.trim())
                stmt.setString(3, txtKategori.text.trim())
                stmt.setInt(4, txtStok.text.trim().toInt())
                stmt.setString(5, txtSatuan.text.trim())

                stmt.executeUpdate()

                JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan!")
                resetFormBarang()
                loadBarangData()
                loadLaporanData()

            } catch (e: SQLException) {
                if (e.errorCode == 1062) {
                    showError("Kode barang sudah ada!")
                } else {
                    showError("Error: ${e.message}")
                }
            } finally {
                DatabaseConnection.closeConnection(null, stmt, null)
            }
        }
    }

    private fun ubahBarang() {
        val selectedRow = barangTable.selectedRow
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang akan diubah!")
            return
        }

        if (validateBarangForm()) {
            val id = barangTableModel.getValueAt(selectedRow, 0).toString()
            var stmt: PreparedStatement? = null

            try {
                stmt = conn.prepareStatement("""
                    UPDATE barang 
                    SET kode_barang = ?, nama_barang = ?, kategori = ?, stok = ?, satuan = ? 
                    WHERE id = ?
                """)

                stmt.setString(1, txtKodeBarang.text.trim())
                stmt.setString(2, txtNamaBarang.text.trim())
                stmt.setString(3, txtKategori.text.trim())
                stmt.setInt(4, txtStok.text.trim().toInt())
                stmt.setString(5, txtSatuan.text.trim())
                stmt.setInt(6, id.toInt())

                stmt.executeUpdate()

                JOptionPane.showMessageDialog(this, "Barang berhasil diubah!")
                resetFormBarang()
                loadBarangData()
                loadLaporanData()

            } catch (e: SQLException) {
                showError("Error: ${e.message}")
            } finally {
                DatabaseConnection.closeConnection(null, stmt, null)
            }
        }
    }

    private fun hapusBarang() {
        val selectedRow = barangTable.selectedRow
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang akan dihapus!")
            return
        }

        val confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus barang ini?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION)

        if (confirm == JOptionPane.YES_OPTION) {
            val id = barangTableModel.getValueAt(selectedRow, 0).toString()
            var stmt: PreparedStatement? = null

            try {
                // Check if barang has transactions
                stmt = conn.prepareStatement("SELECT COUNT(*) FROM transaksi WHERE id_barang = ?")
                stmt.setInt(1, id.toInt())
                val rs = stmt.executeQuery()
                rs.next()

                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Barang tidak dapat dihapus karena memiliki transaksi!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE)
                    return
                }

                stmt.close()

                // Delete barang
                stmt = conn.prepareStatement("DELETE FROM barang WHERE id = ?")
                stmt.setInt(1, id.toInt())
                stmt.executeUpdate()

                JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!")
                resetFormBarang()
                loadBarangData()
                loadLaporanData()

            } catch (e: SQLException) {
                showError("Error: ${e.message}")
            } finally {
                DatabaseConnection.closeConnection(null, stmt, null)
            }
        }
    }

    private fun tambahTransaksi() {
        if (validateTransaksiForm()) {
            val selectedBarang = cmbBarang.selectedItem.toString()
            val idBarang = barangMap[selectedBarang] ?: -1
            var conn: Connection? = null
            var stmt: PreparedStatement? = null

            try {
                conn = DatabaseConnection.getConnection()
                conn.autoCommit = false

                // Insert transaksi
                stmt = conn.prepareStatement("""
                    INSERT INTO transaksi (no_dokumen, tanggal, jenis, id_barang, jumlah, keterangan, operator) 
                    VALUES (?, NOW(), ?, ?, ?, ?, ?)
                """)

                stmt.setString(1, txtNoDokumen.text.trim())
                stmt.setString(2, cmbJenis.selectedItem.toString())
                stmt.setInt(3, idBarang)
                stmt.setInt(4, txtJumlah.text.trim().toInt())
                stmt.setString(5, txtKeterangan.text.trim())
                stmt.setString(6, txtOperator.text.trim())

                stmt.executeUpdate()
                stmt.close()

                // Update stok barang
                val multiplier = if (cmbJenis.selectedItem == "MASUK") 1 else -1
                stmt = conn.prepareStatement("""
                    UPDATE barang 
                    SET stok = stok + ? 
                    WHERE id = ?
                """)

                stmt.setInt(1, txtJumlah.text.trim().toInt() * multiplier)
                stmt.setInt(2, idBarang)

                stmt.executeUpdate()

                conn.commit()

                JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan!")
                resetFormTransaksi()
                loadBarangData()
                loadTransaksiData()
                loadLaporanData()

            } catch (e: SQLException) {
                conn?.rollback()
                showError("Error: ${e.message}")
            } finally {
                conn?.autoCommit = true
                DatabaseConnection.closeConnection(conn, stmt, null)
            }
        }
    }

    private fun ubahTransaksi() {
        val selectedRow = transaksiTable.selectedRow
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan diubah!")
            return
        }

        if (validateTransaksiForm()) {
            val id = transaksiTableModel.getValueAt(selectedRow, 0).toString()
            val selectedBarang = cmbBarang.selectedItem.toString()
            val idBarang = barangMap[selectedBarang] ?: -1
            var conn: Connection? = null
            var stmt: PreparedStatement? = null
            var rs: ResultSet? = null

            try {
                conn = DatabaseConnection.getConnection()
                conn.autoCommit = false

                // Get old transaction data
                stmt = conn.prepareStatement("SELECT * FROM transaksi WHERE id = ?")
                stmt.setInt(1, id.toInt())
                rs = stmt.executeQuery()

                if (!rs.next()) {
                    throw SQLException("Transaksi tidak ditemukan")
                }

                val oldIdBarang = rs.getInt("id_barang")
                val oldJumlah = rs.getInt("jumlah")
                val oldJenis = rs.getString("jenis")

                rs.close()
                stmt.close()

                // Revert old stock
                val oldMultiplier = if (oldJenis == "MASUK") -1 else 1
                stmt = conn.prepareStatement("""
                    UPDATE barang 
                    SET stok = stok + ? 
                    WHERE id = ?
                """)
                stmt.setInt(1, oldJumlah * oldMultiplier)
                stmt.setInt(2, oldIdBarang)
                stmt.executeUpdate()
                stmt.close()

                // Update transaksi
                stmt = conn.prepareStatement("""
                    UPDATE transaksi 
                    SET no_dokumen = ?, jenis = ?, id_barang = ?, jumlah = ?, keterangan = ?, operator = ? 
                    WHERE id = ?
                """)

                stmt.setString(1, txtNoDokumen.text.trim())
                stmt.setString(2, cmbJenis.selectedItem.toString())
                stmt.setInt(3, idBarang)
                stmt.setInt(4, txtJumlah.text.trim().toInt())
                stmt.setString(5, txtKeterangan.text.trim())
                stmt.setString(6, txtOperator.text.trim())
                stmt.setInt(7, id.toInt())

                stmt.executeUpdate()
                stmt.close()

                // Apply new stock
                val newMultiplier = if (cmbJenis.selectedItem == "MASUK") 1 else -1
                stmt = conn.prepareStatement("""
                    UPDATE barang 
                    SET stok = stok + ? 
                    WHERE id = ?
                """)
                stmt.setInt(1, txtJumlah.text.trim().toInt() * newMultiplier)
                stmt.setInt(2, idBarang)
                stmt.executeUpdate()

                conn.commit()

                JOptionPane.showMessageDialog(this, "Transaksi berhasil diubah!")
                resetFormTransaksi()
                loadBarangData()
                loadTransaksiData()
                loadLaporanData()

            } catch (e: SQLException) {
                conn?.rollback()
                showError("Error: ${e.message}")
            } finally {
                conn?.autoCommit = true
                DatabaseConnection.closeConnection(conn, stmt, rs)
            }
        }
    }

    private fun hapusTransaksi() {
        val selectedRow = transaksiTable.selectedRow
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan dihapus!")
            return
        }

        val confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus transaksi ini?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION)

        if (confirm == JOptionPane.YES_OPTION) {
            val id = transaksiTableModel.getValueAt(selectedRow, 0).toString()
            var conn: Connection? = null
            var stmt: PreparedStatement? = null
            var rs: ResultSet? = null

            try {
                conn = DatabaseConnection.getConnection()
                conn.autoCommit = false

                // Get transaction data
                stmt = conn.prepareStatement("SELECT * FROM transaksi WHERE id = ?")
                stmt.setInt(1, id.toInt())
                rs = stmt.executeQuery()

                if (!rs.next()) {
                    throw SQLException("Transaksi tidak ditemukan")
                }

                val idBarang = rs.getInt("id_barang")
                val jumlah = rs.getInt("jumlah")
                val jenis = rs.getString("jenis")

                rs.close()
                stmt.close()

                // Revert stock
                val multiplier = if (jenis == "MASUK") -1 else 1
                stmt = conn.prepareStatement("""
                    UPDATE barang 
                    SET stok = stok + ? 
                    WHERE id = ?
                """)
                stmt.setInt(1, jumlah * multiplier)
                stmt.setInt(2, idBarang)
                stmt.executeUpdate()
                stmt.close()

                // Delete transaksi
                stmt = conn.prepareStatement("DELETE FROM transaksi WHERE id = ?")
                stmt.setInt(1, id.toInt())
                stmt.executeUpdate()

                conn.commit()

                JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus!")
                resetFormTransaksi()
                loadBarangData()
                loadTransaksiData()
                loadLaporanData()

            } catch (e: SQLException) {
                conn?.rollback()
                showError("Error: ${e.message}")
            } finally {
                conn?.autoCommit = true
                DatabaseConnection.closeConnection(conn, stmt, rs)
            }
        }
    }

    private fun validateBarangForm(): Boolean {
        if (txtKodeBarang.text.trim().isEmpty() ||
            txtNamaBarang.text.trim().isEmpty() ||
            txtStok.text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Kode Barang, Nama Barang, dan Stok harus diisi!",
                "Validasi Error",
                JOptionPane.ERROR_MESSAGE)
            return false
        }

        try {
            txtStok.text.trim().toInt()
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(this,
                "Stok harus berupa angka!",
                "Validasi Error",
                JOptionPane.ERROR_MESSAGE)
            return false
        }

        return true
    }

    private fun validateTransaksiForm(): Boolean {
        if (txtNoDokumen.text.trim().isEmpty() ||
            txtJumlah.text.trim().isEmpty() ||
            txtOperator.text.trim().isEmpty() ||
            cmbBarang.selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "No Dokumen, Barang, Jumlah, dan Operator harus diisi!",
                "Validasi Error",
                JOptionPane.ERROR_MESSAGE)
            return false
        }

        try {
            val jumlah = txtJumlah.text.trim().toInt()
            if (jumlah <= 0) {
                throw NumberFormatException()
            }
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(this,
                "Jumlah harus berupa angka positif!",
                "Validasi Error",
                JOptionPane.ERROR_MESSAGE)
            return false
        }

        return true
    }

    private fun resetFormBarang() {
        txtKodeBarang.text = ""
        txtNamaBarang.text = ""
        txtKategori.text = ""
        txtStok.text = ""
        txtSatuan.text = ""
        barangTable.clearSelection()
    }

    private fun resetFormTransaksi() {
        txtNoDokumen.text = ""
        cmbJenis.selectedIndex = 0
        cmbBarang.selectedIndex = -1
        txtJumlah.text = ""
        txtKeterangan.text = ""
        txtOperator.text = ""
        transaksiTable.clearSelection()
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
    }
}