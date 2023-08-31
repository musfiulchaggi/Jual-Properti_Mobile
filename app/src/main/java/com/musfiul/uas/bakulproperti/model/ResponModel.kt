package com.musfiul.uas.bakulproperti.model


class ResponModel {
    var success = 0
    lateinit var message:String
    //    var user bisa langsung diisi dengan class

    var user = User()
    var propertis:ArrayList<Properti> = ArrayList()
    var properti = Properti()

    var provinsi:ArrayList<ModelAlamat> = ArrayList()
    var kota_kabupaten:ArrayList<ModelAlamat> = ArrayList()
    var kecamatan:ArrayList<ModelAlamat> = ArrayList()

    var jumlahGambar = 0

    //    var transaksis:ArrayList<Transaksi> = ArrayList()
    //
    //    var rajaongkir = ModelAlamat()
    //    var transaksi= Transaksi()
    //
    //    var provinsi:ArrayList<ModelAlamat> = ArrayList()
    //    var kota_kabupaten:ArrayList<ModelAlamat> = ArrayList()
    //    var kecamatan:ArrayList<ModelAlamat> = ArrayList()
}