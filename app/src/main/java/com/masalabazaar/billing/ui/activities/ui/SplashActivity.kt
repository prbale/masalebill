package com.masalabazaar.billing.ui.activities.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.masalabazaar.billing.databinding.ActivitySplashBinding


/**
 * SplashActivity serves as the initial screen of the app, shown when the app is launched.
 * It shows a splash screen for 1.5 seconds ( Check OnCreate Method ) and then redirects to either
 * the Home screen or the Login screen depending on the user's login status.
 */
class SplashActivity : AppCompatActivity() {

    // View binding for accessing views in the splash layout without findViewById()
    private lateinit var binding: ActivitySplashBinding

    /**
     * This method is called when the activity is first created.
     * It sets up the splash screen and initiates a delay before navigating to the next screen.
     *
     * @param savedInstanceState The saved instance state bundle for restoring the activity state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge display, possibly making the splash full-screen
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Delay for 1.5 seconds before transitioning to the next screen
        Handler(Looper.getMainLooper()).postDelayed({
            handleLoggedInAccount()
        }, 1500)

        // Call the improved animation method on the container or image view you want to animate
        setSmoothAnimation(binding.idRLContainer)  // Replace idRLContainer with the actual view ID if needed

    }

    /**
     * Checks the user's login status and navigates to the appropriate activity.
     * If the user is logged in, it redirects to HomeActivity. Otherwise, it redirects to LoginActivity.
     */
    private fun handleLoggedInAccount() {

        // Close SplashActivity so that the user cannot return to it by pressing back
        finish()

        // Launch Next Screen
        TODO("Go to next screen.. Not Implemented")
    }

    /**
     * This method applies a smooth fade-in and scale-up animation to the splash screen's view.
     * The view grows from 0.7x to 1.0x in size while fading in.
     *
     * @param view The view (e.g., splash image) to which the animation will be applied.
     */
    private fun setSmoothAnimation(view: View) {
        // Scale the view from 0.7x to 1.0x in both X and Y directions
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1.5f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.7f, 1.5f)

        // Fade the view in (alpha from 0 to 1)
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        // Create an AnimatorSet to play all animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, fadeIn)

        // Set the duration of the animation to 1.5 seconds
        animatorSet.duration = 1500

        // Set a smooth decelerating interpolator for more natural animation
        animatorSet.interpolator = DecelerateInterpolator()

        // Start the animation on the specified view
        animatorSet.start()
    }
}