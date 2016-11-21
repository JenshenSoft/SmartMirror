package com.jenshen.smartmirror.interactor.firebase.api.tuner

import android.content.Context
import com.jenshen.smartmirror.R
import com.jenshen.smartmirror.data.entity.session.TunerSession
import com.jenshen.smartmirror.data.firebase.FirebaseChildEvent
import com.jenshen.smartmirror.data.firebase.TunerSubscription
import com.jenshen.smartmirror.data.model.MirrorModel
import com.jenshen.smartmirror.manager.firebase.api.ApiManager
import com.jenshen.smartmirror.manager.firebase.api.tuner.TunerApiManager
import com.jenshen.smartmirror.manager.preference.PreferencesManager
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class TunerFirebaseApiInteractor @Inject constructor(private var context: Context,
                                                     private var apiManager: ApiManager,
                                                     private var preferencesManager: PreferencesManager,
                                                     private var tunerApiManager: TunerApiManager) : TunerApiInteractor {

    override fun fetchTunerSubscriptions(): Flowable<MirrorModel> {
        return Single.fromCallable { preferencesManager.getSession() }
                .cast(TunerSession::class.java)
                .flatMapPublisher { tunerApiManager.observeTunerSubscriptions(it.id) }
                .map { MirrorModel(it.dataSnapshot.getValue(TunerSubscription::class.java), it.eventType == FirebaseChildEvent.CHILD_REMOVED) }
    }

    override fun subscribeOnMirror(mirrorId: String): Completable {
        return apiManager.getMirror(mirrorId)
                .switchIfEmpty { throw RuntimeException(context.getString(R.string.error_cant_find_mirror)) }
                .flatMapCompletable {
                    Single.fromCallable { preferencesManager.getSession() }
                            .cast(TunerSession::class.java)
                            .flatMapCompletable { tunerApiManager.addSubscriptionToTuner(it.id, mirrorId) }
                            .andThen(tunerApiManager.addSubscriberToMirror(mirrorId))
                            .andThen(tunerApiManager.setFlagForWaitingSubscribersOnMirror(false, mirrorId))
                }
    }
}