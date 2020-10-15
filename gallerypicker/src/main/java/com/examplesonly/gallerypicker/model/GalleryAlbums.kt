package com.examplesonly.gallerypicker.model

import kotlin.collections.ArrayList

public data class GalleryAlbums(var id: Int = 0, var name: String = "", var coverUri: String = "", var albumPhotos: ArrayList<GalleryData> = ArrayList())