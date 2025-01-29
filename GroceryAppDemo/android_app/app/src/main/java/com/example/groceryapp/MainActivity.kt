package com.example.groceryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.groceryapp.ui.screens.CameraScreen
import com.example.groceryapp.ui.screens.CartScreen
import com.example.groceryapp.ui.screens.RecommendationsScreen
import com.example.groceryapp.ui.screens.UploadScreen
import com.example.groceryapp.ui.theme.GroceryAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.groceryapp.ui.viewmodel.GroceryViewModel
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroceryAppTheme {
                GroceryAppContent()
            }
        }
    }
}

@Composable
fun GroceryAppContent() {
    val navController = rememberNavController()
    
    // Create *one* GroceryViewModel for the entire nav flow
    val mainViewModel: GroceryViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "camera") {
        
        composable("camera") {
            CameraScreen(
                onImageCaptured = { file: File ->
                    // Use the *same* mainViewModel here
                    mainViewModel.processImage(file)
                    navController.navigate("recommendations") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToUpload = {
                    navController.navigate("upload") { launchSingleTop = true }
                }
            )
        }
        
        composable("recommendations") {
            RecommendationsScreen(
                onBackPressed = { navController.navigateUp() },
                onCartClicked = { 
                    navController.navigate("cart") { launchSingleTop = true }
                },
                // PASS the same mainViewModel
                viewModel = mainViewModel
            )
        }
        
        composable("upload") {
            UploadScreen(
                onGroceryListProcessed = { file: File ->
                    mainViewModel.processImage(file)
                    navController.navigate("recommendations") {
                        popUpTo("camera") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        
        composable("cart") {
            CartScreen(
                onBackPressed = { navController.navigateUp() },
                // If CartScreen also needs the same data, pass mainViewModel
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GroceryAppTheme {
        GroceryAppContent()
    }
}
