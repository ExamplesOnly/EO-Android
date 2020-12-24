package com.examplesonly.gallerypicker.presenter

import com.examplesonly.gallerypicker.model.interactor.VideosInteractorImpl
import com.examplesonly.gallerypicker.view.VideosFragment

class VideosPresenterImpl(var videosFragment: VideosFragment): VideosPresenter {
    var interactor = VideosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}