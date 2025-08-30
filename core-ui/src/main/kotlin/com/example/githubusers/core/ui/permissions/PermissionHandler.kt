package com.example.githubusers.core.ui.permissions

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Pure Jetpack Compose permission handling without Accompanist.
 * Uses standard Android APIs with Compose integration.
 */
@Composable
fun rememberPermissionState(
    permission: String,
    onPermissionResult: (Boolean) -> Unit = {}
): PermissionState {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == 
            PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        onPermissionResult(isGranted)
    }
    
    return remember(permission) {
        PermissionState(
            permission = permission,
            isGranted = permissionGranted,
            requestPermission = { launcher.launch(permission) }
        )
    }
}

/**
 * Handle multiple permissions without Accompanist
 */
@Composable
fun rememberMultiplePermissionsState(
    permissions: List<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {}
): MultiplePermissionsState {
    val context = LocalContext.current
    var permissionsStatus by remember {
        mutableStateOf(
            permissions.associateWith { permission ->
                ContextCompat.checkSelfPermission(context, permission) == 
                PackageManager.PERMISSION_GRANTED
            }
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionsStatus = results
        onPermissionsResult(results)
    }
    
    return remember(permissions) {
        MultiplePermissionsState(
            permissions = permissions,
            permissionsStatus = permissionsStatus,
            allGranted = permissionsStatus.values.all { it },
            requestPermissions = { launcher.launch(permissions.toTypedArray()) }
        )
    }
}

data class PermissionState(
    val permission: String,
    val isGranted: Boolean,
    val requestPermission: () -> Unit
)

data class MultiplePermissionsState(
    val permissions: List<String>,
    val permissionsStatus: Map<String, Boolean>,
    val allGranted: Boolean,
    val requestPermissions: () -> Unit
)
