package com.solvevolve.gallerypicker.view

import com.solvevolve.gallerypicker.model.GalleryAlbums
import kotlin.collections.ArrayList

interface OnPhoneImagesObtained {
    fun onComplete(albums: ArrayList<GalleryAlbums>)
    fun onError()
}
