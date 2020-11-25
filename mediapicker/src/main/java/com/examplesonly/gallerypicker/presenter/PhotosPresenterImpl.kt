package com.examplesonly.gallerypicker.presenter

import com.examplesonly.gallerypicker.model.interactor.PhotosInteractorImpl
import com.examplesonly.gallerypicker.view.PhotosFragment


class PhotosPresenterImpl(var photosFragment: PhotosFragment): PhotosPresenter {
    val interactor: PhotosInteractorImpl = PhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}