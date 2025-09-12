package com.example.githubusers.core.ui.permissions

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Pure Jetpack Compose permission handling without Accompanist.
 * Uses standard Android APIs with Compose integration.
 */
@Composable
fun rememberPermissionState(permission: String, onPermissionResult: (Boolean) -> Unit = {}): PermissionState {
    val context = LocalContext.current
    val permissionGranted =
        remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED
            )
        }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            permissionGranted.value = isGranted
            onPermissionResult(isGranted)
        }

    return remember(permission) {
        PermissionState(
            permission = permission,
            isGranted = permissionGranted.value,
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
    val permissionsStatus =
        remember {
            mutableStateOf(
                permissions.associateWith { permission ->
                    ContextCompat.checkSelfPermission(context, permission) ==
                        PackageManager.PERMISSION_GRANTED
                }
            )
        }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            permissionsStatus.value = results
            onPermissionsResult(results)
        }

    return remember(permissions) {
        MultiplePermissionsState(
            permissions = permissions,
            permissionsStatus = permissionsStatus.value,
            allGranted = permissionsStatus.value.values.all { it },
            requestPermissions = { launcher.launch(permissions.toTypedArray()) }
        )
    }
}

data class PermissionState(val permission: String, val isGranted: Boolean, val requestPermission: () -> Unit)

data class MultiplePermissionsState(
    val permissions: List<String>,
    val permissionsStatus: Map<String, Boolean>,
    val allGranted: Boolean,
    val requestPermissions: () -> Unit
)
