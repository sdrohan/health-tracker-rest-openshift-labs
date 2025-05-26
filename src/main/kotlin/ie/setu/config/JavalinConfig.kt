package ie.setu.config

import ie.setu.controllers.HealthTrackerController
import ie.setu.utils.jsonObjectMapper
import io.javalin.Javalin
import io.javalin.json.JavalinJackson
import io.javalin.vue.VueComponent

class JavalinConfig {


    val app = Javalin.create(
        { config ->
            config.jsonMapper(JavalinJackson(jsonObjectMapper()))
            config.staticFiles.enableWebjars()
            config.vue.vueInstanceNameInJs = "app"
        }
    ).apply {
        exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
        error(404) { ctx -> ctx.json("404 - Not Found") }
    }

    fun startJavalinService(): Javalin {
        app.start(getRemoteAssignedPort())
        registerRoutes(app)
        return app
    }

    private fun registerRoutes(app: Javalin) {
        app.get("/api/users", HealthTrackerController::getAllUsers)
        app.get("/api/users/{user-id}", HealthTrackerController::getUserByUserId)
        app.post("/api/users", HealthTrackerController::addUser)
        app.delete("/api/users/{user-id}", HealthTrackerController::deleteUser)
        app.patch("/api/users/{user-id}", HealthTrackerController::updateUser)
        app.get("/api/users/email/{email}", HealthTrackerController::getUserByEmail)
        app.get("/api/activities", HealthTrackerController::getAllActivities)
        app.post("/api/activities", HealthTrackerController::addActivity)
        app.get("/api/users/{user-id}/activities", HealthTrackerController::getActivitiesByUserId)

        // The @routeComponent that we added in layout.html earlier will be replaced
        // by the String inside the VueComponent. This means a call to / will load
        // the layout and display our <home-page> component.
        app.get("/", VueComponent("<home-page></home-page>"))
        app.get("/users", VueComponent("<user-overview></user-overview>"))
        app.get("/users/{user-id}", VueComponent("<user-profile></user-profile>"))
        app.get("/users/{user-id}/activities", VueComponent("<user-activity-overview></user-activity-overview>"))
    }

    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else 8080
    }

    fun getJavalinService(): Javalin {
        registerRoutes(app)
        return app
    }

}