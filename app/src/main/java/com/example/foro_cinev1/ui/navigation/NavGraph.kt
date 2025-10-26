package com.example.foro_cinev1.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.foro_cinev1.ui.screens.auth.LoginScreen
import com.example.foro_cinev1.ui.screens.auth.RegisterScreen
import com.example.foro_cinev1.ui.screens.auth.SplashScreen
import com.example.foro_cinev1.ui.screens.home.HomeScreen
import com.example.foro_cinev1.ui.screens.news.NewsListScreen
import com.example.foro_cinev1.ui.screens.news.NewsDetailScreen
import com.example.foro_cinev1.ui.screens.forum.ForumScreen
import com.example.foro_cinev1.ui.screens.forum.CreatePostScreen
import com.example.foro_cinev1.ui.screens.profile.ProfileScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) +
                    fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) +
                    fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) +
                    fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) +
                    fadeOut(animationSpec = tween(300))
        }
    ) {

        // 🟣 SPLASH
        composable(Screen.Splash.route) {
            SplashScreen(
                onSesionActiva = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onIrLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 🟢 LOGIN
        composable(Screen.Login.route) {
            LoginScreen(
                alLoguearseExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                alIrARegistro = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // 🟡 REGISTRO
        composable(Screen.Register.route) {
            RegisterScreen(
                alVolverAtras = { navController.popBackStack() },
                alRegistrarseExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // 🔵 HOME
        composable(Screen.Home.route) {
            HomeScreen(
                alIrANoticias = { navController.navigate(Screen.NewsList.route) },
                alIrAForo = { navController.navigate(Screen.Forum.route) },
                alIrAPerfil = { navController.navigate(Screen.Profile.route) },
                alIrADetalleNoticia = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                }
            )
        }

        // 📰 LISTA DE NOTICIAS
        composable(Screen.NewsList.route) {
            NewsListScreen(
                alVolverAtras = { navController.popBackStack() },
                alIrADetalle = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                }
            )
        }

        // 🗞️ DETALLE DE NOTICIA
        composable(
            route = Screen.NewsDetail.route,
            arguments = listOf(navArgument("newsId") { type = NavType.IntType })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getInt("newsId") ?: 0
            NewsDetailScreen(
                newsId = newsId,
                alVolverAtras = { navController.popBackStack() }
            )
        }

        // 💬 FORO
        composable(Screen.Forum.route) {
            ForumScreen(
                alIrACrearPublicacion = { navController.navigate(Screen.CreatePost.route) },
                alVolverAtras = { navController.popBackStack() },
                navController = navController
            )
        }

        // 📝 CREAR PUBLICACIÓN
        composable(Screen.CreatePost.route) {
            CreatePostScreen(
                alVolverAtras = { navController.popBackStack() }
            )
        }

        // 👤 PERFIL
        composable(Screen.Profile.route) {
            ProfileScreen(
                alVolverAtras = { navController.popBackStack() },
                alCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
