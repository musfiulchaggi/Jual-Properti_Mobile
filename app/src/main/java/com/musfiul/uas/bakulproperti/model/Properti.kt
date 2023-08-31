package com.musfiul.uas.bakulproperti.model

import java.io.Serializable

class Properti: Serializable {

    var id = 0
    var user_id: String? = null
    var nama_properti: String? = null
    var harga = 0
    var lattitude: String? = null
    var longitude: String? = null
    var deskripsi: String? = null
    var email: String? = null
    var provinsi: String? = null
    var kabupaten: String? = null
    var phone: String? = null
    var kecamatan: String? = null
    var created_at: String? = null
    var updated_at: String? = null

    var get_gambar = ArrayList<Gambar>()
}