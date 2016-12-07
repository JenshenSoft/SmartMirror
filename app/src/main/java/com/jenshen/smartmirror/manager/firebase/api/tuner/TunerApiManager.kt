package com.jenshen.smartmirror.manager.firebase.api.tuner

import com.jenshen.smartmirror.data.firebase.FirebaseChildEvent
import com.jenshen.smartmirror.data.firebase.model.configuration.MirrorConfiguration
import com.jenshen.smartmirror.data.firebase.model.configuration.WidgetConfiguration
import com.jenshen.smartmirror.data.firebase.model.mirror.Mirror
import com.jenshen.smartmirror.data.firebase.model.mirror.MirrorConfigurationInfo
import com.jenshen.smartmirror.data.firebase.model.widget.Widget
import com.jenshen.smartmirror.data.model.EditMirrorModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.*


interface TunerApiManager {

    /* mirror */
    fun addSubscriberToMirror(tunerId: String, mirrorId: String): Completable
    fun removeSubscriberFromMirror(tunerId: String, mirrorId: String): Completable
    fun setFlagForWaitingSubscribersOnMirror(isWaiting: Boolean, mirrorId: String): Completable
    fun getMirrorConfigurationsInfo(mirrorId: String): Maybe<HashMap<String, MirrorConfigurationInfo>>
    fun setConfigurationIdForMirror(configurationId: String, mirrorId: String): Completable
    fun deleteConfigurationForMirror(configurationId: String): Completable

    /* tuner */
    fun addSubscriptionToTuner(tunerId: String, mirrorId: String, mirror: Mirror): Completable
    fun removeSubscriptionFromTuner(tunerId: String, mirrorId: String): Completable
    fun observeTunerSubscriptions(tunerId: String): Flowable<FirebaseChildEvent>

    /* widget */
    fun observeWidgets(): Flowable<FirebaseChildEvent>
    fun addWidget(widget: Widget): Single<String>

    /* mirror configurations */
    fun createMirrorConfiguration(mirrorConfiguration: MirrorConfiguration): Single<String>
    fun editMirrorConfiguration(configurationsKey: String, mirrorConfiguration: MirrorConfiguration): Completable
    fun createWidgetToConfiguration(configurationsKey: String, widgetConfiguration: WidgetConfiguration): Single<String>
    fun editWidgetInConfiguration(configurationsKey: String, keyWidget: String, widgetConfiguration: WidgetConfiguration): Completable
    fun createOrEditConfigurationForMirror(mirrorKey: String, configurationKey: String, configurationInfo: MirrorConfigurationInfo): Completable
}