package com.jenshen.smartmirror.manager.widget.factory

import com.jenshen.smartmirror.data.entity.widget.info.WidgetData
import com.jenshen.smartmirror.data.model.widget.WidgetKey
import com.jenshen.smartmirror.data.entity.widget.updater.WidgetUpdater
import com.jenshen.smartmirror.ui.view.widget.Widget
import com.jenshensoft.widgetview.WidgetView

interface IWidgetFactoryManager {
    fun getUpdaterForWidget(widgetKey: WidgetKey): WidgetUpdater<*>
    fun updateWidget(infoData: WidgetData, widget: Widget<*>)
}