package com.jenshen.smartmirror.app

import android.app.Activity
import android.content.Context
import android.support.multidex.MultiDex
import android.support.v7.app.AppCompatDelegate
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.jenshen.compat.base.app.BaseApp
import com.jenshen.compat.base.component.activity.ActivityComponentBuilder
import com.jenshen.smartmirror.BuildConfig
import com.jenshen.smartmirror.di.component.AppComponent
import com.jenshen.smartmirror.di.component.DaggerAppComponent
import com.jenshen.smartmirror.di.component.SessionComponent
import com.jenshen.smartmirror.di.module.AppModule
import com.jenshen.smartmirror.ui.activity.dashboard.mirror.MirrorDashboardActivity
import com.jenshen.smartmirror.util.delegate.lazyValue
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.R

open class SmartMirrorApp : BaseApp<SmartMirrorApp, AppComponent>() {

    companion object {

        @JvmStatic lateinit var rootComponent: AppComponent

        @JvmStatic lateinit var activityComponentBuilders: Map<Class<out Activity>, ActivityComponentBuilder<*>>

        @JvmStatic var sessionActivityComponentBuilders: Map<Class<out Activity>, ActivityComponentBuilder<*>>? by lazyValue {
            sessionActivityComponentBuilders = userComponent!!.provideMultiBuildersForActivities()
            return@lazyValue sessionActivityComponentBuilders
        }

        @JvmStatic val fabricManager by lazy {
            rootComponent.provideFabricManager()
        }

        @JvmStatic var userComponent: SessionComponent? by lazyValue {
            userComponent = rootComponent.userComponentBuilder().build()
            fabricManager.setLogUser(userComponent!!.provideUser())
            return@lazyValue userComponent
        }

        @JvmStatic fun releaseUserComponent() {
            userComponent = null
            sessionActivityComponentBuilders = null
            fabricManager.releaseLogUser()
        }
    }

    /* inject */

    override fun provideDaggerAppComponent(): AppComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()

    override fun injectMembers(instance: AppComponent) {
        rootComponent = instance
        activityComponentBuilders = rootComponent.provideMultiBuildersForActivities()
    }

    /* lifecycle */

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        LeakCanary.install(this)

        val lifecycleCallbacks = LifecycleCallbacks()
        registerActivityLifecycleCallbacks(lifecycleCallbacks)


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                //.setDefaultFontPath("fonts/dinpro-medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build())


        var defaultExceptionHandler: Thread.UncaughtExceptionHandler? = null
        val caughtExceptionHandler  = Thread.UncaughtExceptionHandler { thread, ex ->
            if (lifecycleCallbacks.isDashBoard) {
                MirrorDashboardActivity.startRoot(this, true)
            }
            defaultExceptionHandler!!.uncaughtException(thread, ex)
        }

        Fabric.with(this, Crashlytics.Builder()
                .core(CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build())
                .build())


        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(caughtExceptionHandler)
    }
}