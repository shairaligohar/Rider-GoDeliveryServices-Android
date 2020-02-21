package com.godeliveryservices.rider.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.godeliveryservices.rider.R
import com.godeliveryservices.rider.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

class SplashActivity : AppCompatActivity() {

//    private val viewModel: SplashViewModel by obtainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Firebase Token", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d("Firebase Token", msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

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
