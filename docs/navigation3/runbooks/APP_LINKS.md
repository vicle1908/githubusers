# App Links Setup & Verification

1. Host .well-known/assetlinks.json on https://githubusers.example.com
2. Ensure app manifest has android:autoVerify="true" for https host paths
3. Verify on device:
   adb shell am start -a android.intent.action.VIEW -d "https://githubusers.example.com/user/octocat" com.example.githubusers
4. Check logcat for verification and routing.

