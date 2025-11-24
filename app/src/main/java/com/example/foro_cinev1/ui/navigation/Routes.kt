package com.example.foro_cinev1.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object NewsList : Screen("news_list")

    // ahora acepta String
    object NewsDetail : Screen("news_detail/{newsId}") {
        fun createRoute(newsId: String) = "news_detail/$newsId"
    }

    object Forum : Screen("forum")
    object CreatePost : Screen("create_post")
    object Profile : Screen("profile")
}