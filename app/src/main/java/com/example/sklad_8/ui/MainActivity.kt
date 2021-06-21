package com.example.sklad_8

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sklad_8.databinding.ActivityMainBinding
import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.ModoRender
import com.github.terrakok.modo.android.init
import com.github.terrakok.modo.android.multi.MultiStackFragmentImpl
import com.github.terrakok.modo.android.saveState
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.materialdrawer.iconics.iconicsIcon
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.util.addItems
import android.view.animation.DecelerateInterpolator

import android.view.animation.AccelerateInterpolator

import android.widget.FrameLayout
import org.koin.core.component.getScopeName


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by viewBinding(CreateMethod.INFLATE)

    private val modo = App.modo
    private val modoRender by lazy {
        object : ModoRender(this@MainActivity, R.id.container) {
            override fun createMultiStackFragment() = DrawMultiStackFragment()

            //only for sample
            override fun invoke(state: NavigationState) {
                super.invoke(state)
                findViewById<TextView>(R.id.title).text =
                    state.chain.joinToString(" - ") { screen ->
                        if (screen is MultiScreen) {
                            val chain = screen.stacks[screen.selectedStack].chain
                                .joinToString(" - ") { it.id }
                            "${screen.id}[$chain]"
                        } else {
                            screen.id
                        }
                    }
                val lockedMode = binding.root.getDrawerLockMode(GravityCompat.START)
                val currentScreen = state.chain.lastOrNull()
                if (currentScreen is MultiScreen) {
                    if (lockedMode != DrawerLayout.LOCK_MODE_UNLOCKED) {
                        binding.root.setDrawerLockMode(
                            DrawerLayout.LOCK_MODE_UNLOCKED,
                            GravityCompat.START
                        )
                        binding.slider.setSelectionAtPosition(currentScreen.selectedStack)
                    }

                } else {
//                    binding.root.closeDrawer(binding.slider, true)
                    binding.root.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                        GravityCompat.START
                    )
                }
            }
        }
    }

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(false)
//        supportActionBar?.title = "Складской учет"

        initSlider()
        binding.slider.setSavedInstance(savedInstanceState)

        if (modo.state.chain.isEmpty()) {
            modo.init(savedInstanceState, Screens.MultiStack())
        }
    }

    private fun initSlider() {

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.root,
            binding.toolbar,
            com.mikepenz.materialdrawer.R.string.material_drawer_open,
            com.mikepenz.materialdrawer.R.string.material_drawer_close
        )
        binding.root.addDrawerListener(actionBarDrawerToggle)
        binding.slider.apply {
            addItems(
                PrimaryDrawerItem().apply {
                    nameText = context.getString(R.string.title_tab_goods)
                    iconicsIcon = GoogleMaterial.Icon.gmd_subtitles
                    identifier = 0
                },
                PrimaryDrawerItem().apply {
                    nameText = context.getString(R.string.title_tab_sync)
                    iconicsIcon = GoogleMaterial.Icon.gmd_sync
                    identifier = 1
                },
                PrimaryDrawerItem().apply {
                    nameText = context.getString(R.string.title_tab_settings)
                    iconicsIcon = GoogleMaterial.Icon.gmd_settings
                    identifier = 2
                }
            )
            onDrawerItemClickListener = { _, drawerItem, _ ->
                val tab = drawerItem.identifier.toInt()
                when (tab) {
                    0 -> {
                        binding.toolbar.title = "Номенклатура"
                        showToolbar()
                        modo.selectStack(tab)
                    }
                    1 -> {
                        binding.toolbar.title = "Синхронизация данных"
                        showToolbar()
                        modo.selectStack(tab)
                    }
                    2 -> {
                        binding.toolbar.title = "Настройки"
                        hideToolbar()
                        modo.externalForward(Screens.SettingScreen())
                    }
                }
                false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        modo.render = modoRender
        actionBarDrawerToggle.syncState()
    }

    override fun onPause() {
        modo.render = null
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        modo.saveState(outState)
        binding.slider.saveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (binding.root.isDrawerOpen(binding.slider)) {
            binding.root.closeDrawer(binding.slider)
        } else modo.back()
    }

    private fun hideToolbar() {
        binding.toolbar.animate().translationY((-binding.toolbar.height).toFloat()).setListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    binding.toolbar.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationRepeat(animation: Animator?) {}
            }
        )
            .interpolator = AccelerateInterpolator(2F)
//        val lp = mFabButton.getLayoutParams() as FrameLayout.LayoutParams
//        val fabBottomMargin = lp.bottomMargin
//        mFabButton.animate().translationY(mFabButton.getHeight() + fabBottomMargin)
//            .setInterpolator(AccelerateInterpolator(2)).start()
    }

    private fun showToolbar() {

        if (binding.toolbar.visibility == View.VISIBLE) return
        binding.toolbar.animate().translationY(0F).setListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    binding.toolbar.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationRepeat(animation: Animator?) {}
            }
        ).interpolator = DecelerateInterpolator(2F)
//        mFabButton.animate().translationY(0).setInterpolator(DecelerateInterpolator(2)).start()
    }

}

class DrawMultiStackFragment : MultiStackFragmentImpl() {
    private val modo = App.modo
    override fun createTabView(index: Int, parent: LinearLayout): View =
        LayoutInflater.from(context).inflate(R.layout.layout_tab, parent, false).apply {
            setOnClickListener {
                val currentScreen = modo.state.chain.lastOrNull()
                if (currentScreen is MultiScreen) {
                    if (currentScreen.selectedStack != index) {
                        modo.selectStack(index)
                    } else {
                        modo.backToTabRoot()
                    }
                }
            }
        }
}