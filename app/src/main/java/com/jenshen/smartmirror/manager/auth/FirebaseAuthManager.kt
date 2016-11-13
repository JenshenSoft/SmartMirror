package com.jenshen.smartmirror.manager.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import javax.inject.Inject

class FirebaseAuthManager @Inject constructor(private val fireBaseAuth: FirebaseAuth) : AuthManager {

    override val isUserExists: Boolean
        get() = fireBaseAuth.currentUser != null

    override fun fetchFirebaseUser(): Observable<FirebaseUser> {
        return Observable.create { source ->
            val authStateListener = FirebaseAuth.AuthStateListener {
                val currentUser = it.currentUser
                source.onNext(currentUser)
            }
            fireBaseAuth.addAuthStateListener(authStateListener)
            source.setCancellable { fireBaseAuth.removeAuthStateListener(authStateListener) }
        }
    }

    override fun makeRequestFirebaseUser(): Maybe<FirebaseUser> {
        fetchFirebaseUser().subscribe(Consumer {  })
        return Maybe.create { source ->
            val authStateListener = FirebaseAuth.AuthStateListener {
                val currentUser = it.currentUser
                if (currentUser != null) {
                    source.onSuccess(currentUser)
                } else {
                    source.onComplete()
                }
            }
            fireBaseAuth.addAuthStateListener(authStateListener)
            source.setCancellable { fireBaseAuth.removeAuthStateListener(authStateListener) }
        }
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Completable {
        return Completable.create { emitter ->
            fireBaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.onComplete()
                        } else {
                            emitter.onError(it.exception)
                        }
                    }
        }
    }

    override fun createNewUser(email: String, password: String): Completable {
        return Completable.create { emitter ->
            fireBaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.onComplete()
                        } else {
                            emitter.onError(it.exception)
                        }
                    }
        }
    }
}