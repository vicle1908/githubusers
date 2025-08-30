# Predictive Back Policy

- Only top-of-stack destinations may intercept back.
- Use Navigation3Host(canInterceptBack = { entry -> /* destination logic */ }, onInterceptBack = { entry -> /* custom back */ })
- Verify gesture preview remains intact via UI tests.

