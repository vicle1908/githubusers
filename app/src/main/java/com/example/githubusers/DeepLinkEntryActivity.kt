package com.example.githubusers

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * Exported entry activity for deep links. Normalizes and forwards to MainActivity.
 */
class DeepLinkEntryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val incoming = intent?.data
        val forward =
            Intent(this, com.example.githubusers.presentation.MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                this.data = incoming
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        startActivity(forward)
        finish()
    }
}
