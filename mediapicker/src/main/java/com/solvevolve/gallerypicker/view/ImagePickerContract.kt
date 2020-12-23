package com.solvevolve.gallerypicker.view

import android.content.Context
import com.solvevolve.gallerypicker.model.GalleryAlbums
import com.solvevolve.gallerypicker.model.GalleryData

interface ImagePickerContract {
    fun initRecyclerViews()
    fun galleryOperation()
    fun toggleDropdown()
    fun getPhoneAlbums(context: Context, listener: OnPhoneImagesObtained)
    fun updateTitle(galleryAlbums: GalleryAlbums = GalleryAlbums())
    fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData> = ArrayList())
}