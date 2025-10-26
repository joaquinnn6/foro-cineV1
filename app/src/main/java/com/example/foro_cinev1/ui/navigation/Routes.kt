package com.example.foro_cinev1.ui.navigation

sealed class Screen(val route: String) {

    // 🔹 Splash: punto inicial que decide si hay sesión activa
    object Splash : Screen("splash")

    // 🔹 Autenticación
    object Login : Screen("login")
    object Register : Screen("register")

    // 🔹 Navegación principal
    object Home : Screen("home")

    // 🔹 Noticias
    object NewsList : Screen("news_list")
    object NewsDetail : Screen("news_detail/{newsId}") {
        fun createRoute(newsId: Int) = "news_detail/$newsId"
    }

    // 🔹 Foro
    object Forum : Screen("forum")
    object CreatePost : Screen("create_post")

    // 🔹 Perfil
    object Profile : Screen("profile")
}
