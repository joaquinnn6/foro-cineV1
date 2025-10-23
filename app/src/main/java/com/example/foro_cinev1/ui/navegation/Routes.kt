package com.example.foro_cinev1.ui.navegation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object NewsList : Screen("news_list")
    object NewsDetail : Screen("news_detail/{newsId}") {
        fun createRoute(newsId: Int) = "news_detail/$newsId"
    }
    object Forum : Screen("forum")
    object CreatePost : Screen("create_post")
    object Profile : Screen("profile")
}