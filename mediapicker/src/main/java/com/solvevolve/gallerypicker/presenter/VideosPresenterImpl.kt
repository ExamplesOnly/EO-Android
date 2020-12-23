package com.solvevolve.gallerypicker.presenter

import com.solvevolve.gallerypicker.model.interactor.VideosInteractorImpl
import com.solvevolve.gallerypicker.view.VideosFragment

class VideosPresenterImpl(var videosFragment: VideosFragment): VideosPresenter {
    var interactor = VideosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}