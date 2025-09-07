# ADB Deep Link Tests

Examples:

- App scheme:
  adb shell am start -a android.intent.action.VIEW -d "githubusers://user/octocat" com.example.githubusers

- Web URL (app links):
  adb shell am start -a android.intent.action.VIEW -d "https://githubusers.example.com/user/octocat" com.example.githubusers

Validate:

- Landed screen matches route
- Back stack shape as expected
- After process kill, restore parity
