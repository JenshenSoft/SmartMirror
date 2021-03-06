package com.jenshen.smartmirror.interactor.firebase.auth

import android.content.Context
import android.net.Uri
import android.provider.Settings
import com.google.firebase.auth.FirebaseUser
import com.jenshen.smartmirror.data.entity.session.MirrorSession
import com.jenshen.smartmirror.data.entity.session.Session
import com.jenshen.smartmirror.data.entity.session.TunerSession
import com.jenshen.smartmirror.manager.firebase.auth.AuthManager
import com.jenshen.smartmirror.manager.preference.PreferencesManager
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject


class FirebaseAuthInteractor @Inject constructor(private val context: Context,
                                                 private val authManager: AuthManager,
                                                 private val preferencesManager: PreferencesManager) : AuthInteractor {

    override fun fetchAuth(): Observable<FirebaseUser> {
        return authManager.fetchFirebaseUser()
                .doOnNext {
                    val isMirror = it.isAnonymous
                    val session: Session
                    if (isMirror) {
                        //todo val iid = InstanceID.getInstance(context).getId()
                        session = MirrorSession(getDeviceUniqueID(context))
                    } else {
                        session = TunerSession(it.uid, it.email!!, it.displayName, it.photoUrl.let(Uri?::toString))
                    }
                    preferencesManager.sighIn(session, it.isAnonymous)
                }
    }

    override fun signInTuner(email: String, password: String): Completable {
        return authManager.signInWithEmailAndPassword(email, password)
    }

    override fun signInMirror(): Completable {
        return authManager.signInAnonymously()
    }

    override fun editUserInfo(name: String, uri: Uri): Completable {
        return authManager.updateProfile(name, uri)
    }

    override fun createNewTuner(email: String, password: String): Completable {
        return authManager.createNewUser(email, password)
    }

    override fun resetPassword(email: String) :Completable {
        return authManager.resetPassword(email)
    }

    private fun getDeviceUniqueID(activity: Context) = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
}