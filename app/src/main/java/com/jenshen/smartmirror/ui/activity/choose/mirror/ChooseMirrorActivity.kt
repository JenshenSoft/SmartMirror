package com.jenshen.smartmirror.ui.activity.choose.mirror

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import com.jenshen.compat.base.view.impl.mvp.lce.component.lce.BaseDiLceMvpActivity
import com.jenshen.smartmirror.R
import com.jenshen.smartmirror.app.SmartMirrorApp
import com.jenshen.smartmirror.data.model.mirror.MirrorModel
import com.jenshen.smartmirror.di.component.activity.choose.mirror.ChooseMirrorComponent
import com.jenshen.smartmirror.ui.activity.edit.mirror.EditMirrorActivity
import com.jenshen.smartmirror.ui.activity.qrScan.QRCodeScanActivity
import com.jenshen.smartmirror.ui.activity.settings.app.SettingsActivity
import com.jenshen.smartmirror.ui.adapter.SwipeToDeleteAdapter
import com.jenshen.smartmirror.ui.adapter.mirrors.MirrorsAdapter
import com.jenshen.smartmirror.ui.adapter.touch.SimpleItemTouchHelperCallback
import com.jenshen.smartmirror.ui.mvp.presenter.choose.mirror.ChooseMirrorPresenter
import com.jenshen.smartmirror.ui.mvp.view.choose.mirror.ChooseMirrorView
import kotlinx.android.synthetic.main.partial_toolbar.*

class ChooseMirrorActivity : BaseDiLceMvpActivity<ChooseMirrorComponent,
        RecyclerView,
        MirrorModel,
        ChooseMirrorView,
        ChooseMirrorPresenter>(),
        ChooseMirrorView, SwipeToDeleteAdapter.OnDeleteItemListener<MirrorModel> {


    private lateinit var adapter: MirrorsAdapter

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChooseMirrorActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    /* inject */

    override fun createComponent(): ChooseMirrorComponent {
        return SmartMirrorApp
                .sessionActivityComponentBuilders!![ChooseMirrorActivity::class.java]!!
                .build() as ChooseMirrorComponent
    }

    override fun injectMembers(instance: ChooseMirrorComponent) {
        instance.injectMembers(this)
    }

    /* lifecycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_mirror)
        setSupportActionBar(toolbar)
        adapter = MirrorsAdapter(context,
                //item click
                { /*todo edit */ },
                // qr code clicked
                { presenter.setMirrorIsWaitingForSubscriber(it.key) },
                //add new configuration
                { EditMirrorActivity.start(context, it.key) },
                //edit configuration
                { configurationKey: String, mirrorModel: MirrorModel -> EditMirrorActivity.start(context, mirrorModel.key, configurationKey) },
                //delete configuration
                { configurationKey: String, mirrorModel: MirrorModel -> presenter.deleteConfigurationForMirror(configurationKey, mirrorModel.key) },
                //set configuration
                { configurationKey: String?, mirrorModel: MirrorModel -> presenter.setSelectedConfigurationKeyForMirror(configurationKey, mirrorModel.key) },
                this)
        contentView.adapter = adapter

        val callback = SimpleItemTouchHelperCallback(adapter, context, ItemTouchHelper.LEFT)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(contentView)

        loadData(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == QRCodeScanActivity.RESULT_KEY_QR_CODE && resultCode != Activity.RESULT_OK && data == null) {
            return
        }
        presenter.subscribeOnMirror(data!!.getStringExtra(QRCodeScanActivity.RESULT_EXTRA_MIRROR_ID))
    }

    /* menu */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tuner_dashboard, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_item_menu -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.scan_QrCode_item_menu -> {
                QRCodeScanActivity.startForResult(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /* lce */

    override fun loadData(pullToRefresh: Boolean) {
        presenter.fetchMirrors(pullToRefresh)
    }

    override fun setData(data: MirrorModel) {
        adapter.addModel(data)
    }

    /* callbacks */

    override fun onDeleteItem(position: Int, item: MirrorModel) {
        adapter.deleteModel(position)
        presenter.deleteSubscription(item.key)
    }
}
