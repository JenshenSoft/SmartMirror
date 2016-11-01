package com.jenshen.smartmirror.manager.preference

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.jenshen.smartmirror.R
import com.jenshen.smartmirror.model.User
import io.reactivex.Completable
import io.reactivex.Single
import ua.regin.pocket.manager.preference.PreferencesManager

class SharedPreferencesManager : PreferencesManager {

    private val mSharedPreferences: SharedPreferences
    private val mGson: Gson
    private val mContext: Context

    constructor(context: Context, sharePreferences: SharedPreferences, gson: Gson) {
        mContext = context
        mSharedPreferences = sharePreferences
        mGson = gson
    }

    override fun logout(): Completable = Completable.fromCallable {
        val editor = mSharedPreferences.edit()
        editor.remove(mContext.getString(R.string.preference_key_user))
        editor.remove(mContext.getString(R.string.preference_key_session))
        editor.apply()
    }

    override fun getUser(): User {
        val json = mSharedPreferences.getString(mContext.getString(R.string.preference_key_user), null)
        return mGson.fromJson(json, User::class.java)
    }

    override fun login(user: User, token: String) {
        saveUser(user)
        saveSession(token)
    }

    override fun isSessionExist(): Single<Boolean> = Single.fromCallable {
        getSession() != null
    }

    private fun saveUser(user: User) {
        val stringValue = mGson.toJson(user)
        mSharedPreferences.edit().putString(mContext.getString(R.string.preference_key_user), stringValue).apply()
    }

    private fun saveSession(session: String) {
        mSharedPreferences.edit().putString(mContext.getString(R.string.preference_key_session), session).apply()
    }

    override fun getSession(): String? {
        return mSharedPreferences.getString(mContext.getString(R.string.preference_key_session), null)
    }
}
