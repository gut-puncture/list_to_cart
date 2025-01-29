package com.example.groceryapp.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun ErrorHandler(
    errorFlow: SharedFlow<String>,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
        errorFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
}