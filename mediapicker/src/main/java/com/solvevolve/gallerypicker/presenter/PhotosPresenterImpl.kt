package com.solvevolve.gallerypicker.presenter

import com.solvevolve.gallerypicker.model.interactor.PhotosInteractorImpl
import com.solvevolve.gallerypicker.view.PhotosFragment


class PhotosPresenterImpl(var photosFragment: PhotosFragment): PhotosPresenter {
    val interactor: PhotosInteractorImpl = PhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}