package com.jenshen.smartmirror.di.component.activity.choose.widget

import com.jenshen.compat.base.component.activity.ActivityComponent
import com.jenshen.compat.base.component.activity.ActivityComponentBuilder
import com.jenshen.compat.base.component.presenter.PresenterLceComponent
import com.jenshen.smartmirror.data.model.WidgetModel
import com.jenshen.smartmirror.di.module.activity.choose.widget.ChooseWidgetModule
import com.jenshen.smartmirror.di.scope.SessionScope
import com.jenshen.smartmirror.ui.activity.choose.widget.ChooseWidgetActivity
import com.jenshen.smartmirror.ui.mvp.presenter.choose.widget.ChooseWidgetPresenter
import com.jenshen.smartmirror.ui.mvp.view.choose.widget.ChooseWidgetView
import dagger.Subcomponent

@SessionScope
@Subcomponent(modules = arrayOf(ChooseWidgetModule::class))
interface ChooseWidgetComponent : PresenterLceComponent<WidgetModel, ChooseWidgetView, ChooseWidgetPresenter>,
        ActivityComponent<ChooseWidgetActivity> {

    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<ChooseWidgetComponent>
}