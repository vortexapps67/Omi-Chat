package com.popchat.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.popchat.ui.theme.OmiChatTheme

/**
 * Receives the Supabase auth redirect.
 *
 * Supabase's Android SDK installs its own callback activity for the deep link;
 * this one exists so the redirect resolves back into the app's own theme and
 * shows the shared [AuthCallbackScreen] while the session is exchanged, instead
 * of dropping the user on a blank system window.
 */
class AuthCallbackActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmiChatTheme {
                AuthCallbackScreen()
            }
        }
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        // The Supabase client picks the redirect up from its own activity; here
        // we only need to make sure this window is not left on screen. Finishing
        // hands control back to MainActivity, which observes the session.
        if (intent.data != null) {
            finish()
        }
    }
}