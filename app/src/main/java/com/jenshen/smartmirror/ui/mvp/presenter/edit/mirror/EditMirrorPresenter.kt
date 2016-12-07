package com.jenshen.smartmirror.ui.mvp.presenter.edit.mirror


import com.jenshen.compat.base.presenter.MvpRxPresenter
import com.jenshen.smartmirror.data.model.EditMirrorModel
import com.jenshen.smartmirror.interactor.firebase.api.tuner.TunerApiInteractor
import com.jenshen.smartmirror.ui.mvp.view.edit.mirror.EditMirrorView
import com.jenshen.smartmirror.util.reactive.applyProgress
import com.jenshen.smartmirror.util.reactive.applySchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EditMirrorPresenter @Inject constructor(private val tunerApiInteractor: TunerApiInteractor) : MvpRxPresenter<EditMirrorView>() {

    fun saveConfiguration(editMirrorModel: EditMirrorModel) {
        tunerApiInteractor.saveMirrorConfiguration(editMirrorModel)
                .applySchedulers(Schedulers.io())
                .applyProgress(Consumer { view?.showProgress() }, Action { view?.hideProgress() })
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe({ view?.onSavedConfiguration() }, { view?.handleError(it) })
    }

    fun loadMirrorConfiguration(configurationKey: String) {
        tunerApiInteractor.getMirrorConfiguration(configurationKey)
                .applySchedulers(Schedulers.io())
                .applyProgress(Consumer { view?.showProgress() }, Action { view?.hideProgress() })
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe({ view?.onMirrorConfigurationLoaded(it) }, { view?.handleError(it) })
    }
}
