package com.jenshen.smartmirror.data.firebase.model.configuration

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.jenshen.smartmirror.data.firebase.FirebaseConstant
import java.util.*

@IgnoreExtraProperties
data class MirrorConfiguration(
        @set:PropertyName(FirebaseConstant.MirrorConfiguration.MIRROR_KEY)
        @get:PropertyName(FirebaseConstant.MirrorConfiguration.MIRROR_KEY)
        var mirrorKey: String,
        @set:PropertyName(FirebaseConstant.MirrorConfiguration.TITLE)
        @get:PropertyName(FirebaseConstant.MirrorConfiguration.TITLE)
        var title: String,
        @set:PropertyName(FirebaseConstant.MirrorConfiguration.CONTAINER_SIZE)
        @get:PropertyName(FirebaseConstant.MirrorConfiguration.CONTAINER_SIZE)
        var containerSize: ContainerSize) {

    @set:PropertyName(FirebaseConstant.MirrorConfiguration.WIDGETS)
    @get:PropertyName(FirebaseConstant.MirrorConfiguration.WIDGETS)
    var widgets: HashMap<String, WidgetConfiguration>? = null

    constructor() : this("", "", ContainerSize()) {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
}