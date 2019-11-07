package com.sumanth.testapplication.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.sumanth.testapplication.R
import com.sumanth.testapplication.interfaces.DataListener
import com.sumanth.testapplication.model.User
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*


class GitHubActivity : AppCompatActivity(), DataListener {
    override fun onSelected(users: User?) {
        val fragment =
            DataDetailsFragment.newInstance(users)
        pushFragment(fragment, "details")
    }

    private lateinit var alertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setSupportActionBar(toolbar)
    }

    private val compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pushFragment(DataFragment(), "home")
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment == DataFragment()) {
            val frag = fragment as DataFragment
            frag.setDataSelectListener(this)
        }
    }

    private fun pushFragment(fragment: Fragment, tag: String) {
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.container, fragment, tag)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(tag)
            ft.commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            displayAlertDialog()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun displayAlertDialog() {

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(getString(R.string.exit))
        //set message for alert dialog
        builder.setMessage(getString(R.string.alert_exit))
        builder.setIcon(android.R.drawable.ic_dialog_alert)


        //performing positive action
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            finish()
        }
        //performing negative action
        builder.setNegativeButton(getString(R.string.no)) { _, _ ->
            alertDialog.dismiss()
        }
        // Create the AlertDialog
        alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
