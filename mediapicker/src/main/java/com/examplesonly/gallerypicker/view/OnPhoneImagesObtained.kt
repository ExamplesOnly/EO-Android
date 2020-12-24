package com.examplesonly.gallerypicker.view

import com.examplesonly.gallerypicker.model.GalleryAlbums
import kotlin.collections.ArrayList

interface OnPhoneImagesObtained {
    fun onComplete(albums: ArrayList<GalleryAlbums>)
    fun onError()
}
