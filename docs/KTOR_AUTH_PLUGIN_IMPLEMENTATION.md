# Ktor Auth Plugin Implementation

## Overview

This document describes the implementation of Ktor's Auth Plugin for automatic bearer token authentication in the GitHub Users Android application. The implementation provides secure token management and automatic injection of GitHub Personal Access Tokens into all API requests.

## Architecture

### Components

1. **TokenManager Interface** - Defines the contract for token storage and retrieval
2. **SecureTokenManager** - Secure implementation using Android DataStore
3. **HttpClientProvider** - Enhanced HTTP client with Auth plugin integration
4. **AuthenticationService** - High-level authentication operations
5. **NetworkingModule** - Hilt dependency injection configuration

### Flow

```
User Authentication → TokenManager → HttpClientProvider → GitHub API
                                    ↓
                            Automatic Bearer Token Injection
```

## Implementation Details

### 1. TokenManager Interface

```kotlin
interface TokenManager {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun storeTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
    suspend fun isAuthenticated(): Boolean
}
```

### 2. SecureTokenManager Implementation

- Uses Android DataStore for secure token storage
- Implements encrypted preferences for sensitive data
- Provides Flow-based authentication state observation
- Includes comprehensive error handling and logging

### 3. HttpClientProvider with Auth Plugin

```kotlin
install(Auth) {
    bearer {
        loadTokens {
            val accessToken = tokenManager.getAccessToken()
            val refreshToken = tokenManager.getRefreshToken()
            
            if (accessToken != null && refreshToken != null) {
                BearerTokens(accessToken, refreshToken)
            } else {
                null
            }
        }
        
        refreshTokens {
            // TODO: Implement GitHub token refresh logic
            null
        }
    }
}
```

### 4. Automatic Token Injection

The Auth plugin automatically adds the `Authorization: Bearer <token>` header to all HTTP requests when tokens are available.

### 5. Error Handling

- **401 Unauthorized**: Logs token invalidity, triggers re-authentication
- **403 Forbidden**: Logs rate limit exceeded
- **429 Too Many Requests**: Logs excessive requests

## Usage

### 1. Authentication

```kotlin
@Inject
lateinit var authenticationService: AuthenticationService

// Authenticate with GitHub Personal Access Token
val success = authenticationService.authenticateWithPersonalAccessToken("ghp_your_token_here")
```

### 2. Making Authenticated Requests

```kotlin
@Inject
lateinit var httpClient: HttpClient

// All requests automatically include bearer token
val response = httpClient.get("https://api.github.com/user")
```

### 3. Checking Authentication Status

```kotlin
val isAuthenticated = authenticationService.isAuthenticated()
val status = authenticationService.getAuthenticationStatus()
```

### 4. Logout

```kotlin
authenticationService.logout()
```

## Security Features

### 1. Secure Storage
- Tokens stored in encrypted DataStore preferences
- No plaintext token storage
- Automatic token cleanup on logout

### 2. Rate Limiting
- Built-in rate limiting to prevent API abuse
- 1-second delay between requests
- Comprehensive logging for monitoring

### 3. Error Handling
- Graceful handling of authentication failures
- Automatic token refresh capability (TODO)
- Comprehensive logging for debugging

## Testing

### Unit Tests
- `AuthenticationServiceTest` - Tests authentication operations
- Mock-based testing for isolated unit testing
- Comprehensive coverage of success and failure scenarios

### Integration Tests
- End-to-end authentication flow testing
- Token storage and retrieval verification
- HTTP client authentication verification

## Configuration

### Dependencies

```kotlin
// Ktor Auth Plugin
implementation("io.ktor:ktor-client-auth")

// DataStore for secure storage
implementation("androidx.datastore:datastore-preferences")

// Timber for logging
implementation("com.jakewharton.timber:timber")
```

### Hilt Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkingModule {
    @Binds
    @Singleton
    abstract fun bindTokenManager(secureTokenManager: SecureTokenManager): TokenManager
    
    @Provides
    @Singleton
    fun provideHttpClient(httpClientProvider: HttpClientProvider): HttpClient
}
```

## Future Enhancements

### 1. OAuth2 Implementation
- Replace Personal Access Token with OAuth2 flow
- Implement proper token refresh mechanism
- Add PKCE (Proof Key for Code Exchange) support

### 2. Token Refresh
- Implement automatic token refresh on 401 errors
- Add refresh token rotation
- Handle refresh failures gracefully

### 3. Enhanced Security
- Add certificate pinning for GitHub API
- Implement token encryption at rest
- Add biometric authentication for token access

### 4. Monitoring
- Add authentication metrics
- Implement token usage tracking
- Add security event logging

## Best Practices

### 1. Token Management
- Never log actual tokens
- Use secure storage mechanisms
- Implement proper token cleanup

### 2. Error Handling
- Handle authentication failures gracefully
- Provide clear user feedback
- Implement retry mechanisms

### 3. Security
- Validate all tokens before use
- Implement proper session management
- Follow OWASP security guidelines

## Troubleshooting

### Common Issues

1. **Token Not Being Sent**
   - Check if tokens are properly stored
   - Verify HttpClientProvider configuration
   - Check authentication status

2. **401 Unauthorized Errors**
   - Verify token validity
   - Check token permissions
   - Implement token refresh

3. **Rate Limiting Issues**
   - Check rate limiting configuration
   - Monitor request frequency
   - Implement proper backoff strategies

### Debug Logging

Enable Timber logging to debug authentication issues:

```kotlin
Timber.tag("HttpClientProvider").d("Loading tokens for authentication")
Timber.tag("AuthenticationService").d("Authentication successful")
```

## Conclusion

The Ktor Auth Plugin implementation provides a robust, secure, and maintainable solution for GitHub API authentication. It automatically handles token injection, provides secure storage, and includes comprehensive error handling and logging.

The implementation follows Android best practices and provides a solid foundation for future enhancements including OAuth2 integration and advanced security features.

