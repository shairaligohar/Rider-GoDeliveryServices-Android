package com.godeliveryservices.rider.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.godeliveryservices.rider.MainActivity
import com.godeliveryservices.rider.R
import com.godeliveryservices.rider.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class SplashActivity : AppCompatActivity() {

//    private val viewModel: SplashViewModel by obtainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val isUserLoggedIn: Boolean = PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("LoggedIn", false)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            if(isUserLoggedIn)
                startActivity(Intent(this, MainActivity::class.java))
            else
                startActivity(Intent(this, LoginActivity::class.java))

            // close this activity
            finish()
        }, 3000)
//        setupViews()
//        setupObservers()
//        viewModel.start()
    }

    private fun setupViews() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            app_logo.transitionName = getString(R.string.transition_appLogo)
//        }
    }

    private fun setupObservers() {
//        viewModel.navigate.observe(this, EventObserver { event ->
//            when (event) {
//                SplashViewModel.Events.NAVIGATE_TO_LOGIN -> {
//                    val intent: Intent = LoginActivity.newIntent(context = this)
//                    val options: ActivityOptions = ActivityOptions.makeCustomAnimation(
//                        this,
//                        R.anim.slide_in_left,
//                        R.anim.slide_out_left
//                    )
//                    startActivity(intent, options.toBundle())
//                    supportFinishAfterTransition()
//                }
//            }
//        })
    }
}
