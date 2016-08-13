package the.game.embedded

import com.badlogic.gdx.*
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import org.lwjgl.opengl.GL40
import org.slf4j.LoggerFactory
import the.game.api.objects.WorldObject
import the.game.api.objects.space.Planet
import the.game.api.objects.space.PlanetOptions
import the.game.api.objects.space.Star

val log = LoggerFactory.getLogger("the.game.embedded.Bootstrap")
val objects = mutableListOf<WorldObject>()

fun main(args: Array<String>): Unit {
    log.info("Starting with arguments: {}", args)
    val config = LwjglApplicationConfiguration()
    val world = World(Vector2.Zero, true)

    val star = Star(15f)
    star.init(world)
    objects += star

    val planet1 = Planet(PlanetOptions(Vector2(30f, 30f), 5f, 120f))
    planet1.init(world)
    objects += planet1

    val planet2 = Planet(PlanetOptions(Vector2(50f, 50f), 8f, 120f))
    planet2.init(world)
    objects += planet2

    val planet3 = Planet(PlanetOptions(Vector2(70f, 70f), 14f, 90f))
    planet3.init(world)
    objects += planet3

    val planet4 = Planet(PlanetOptions(Vector2(85f, 85f), 3f, 115f))
    planet4.init(world)
    objects += planet4

    LwjglApplication(Application(world), config)
    log.info("Started")
}

private fun createStatic(world: World) {
    // Create our body definition
    val groundBodyDef = BodyDef()
    // Set its world position
    groundBodyDef.position.set(Vector2(0f, 10f))

    // Create a body from the defintion and add it to the world
    val groundBody = world.createBody(groundBodyDef)

    // Create a polygon shape
    val groundBox = PolygonShape()
    // Set the polygon shape as a box which is twice the size of our view port and 20 high
    // (setAsBox takes half-width and half-height as arguments)
    groundBox.setAsBox(200f, 10.0f)
    // Create a fixture from our polygon shape and add it to our ground body
    groundBody.createFixture(groundBox, 0.0f)
}

private fun createDynamic(world: World) {
    // First we create a body definition
    val bodyDef = BodyDef()
    // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
    bodyDef.type = BodyDef.BodyType.DynamicBody
    // Set our body's starting position in the world
    bodyDef.position.set(100f, 300f)

    // Create our body in the world using our body definition
    val body = world.createBody(bodyDef)

    // Create a circle shape and set its radius to 6
    val circle = CircleShape()
    circle.radius = 6f

    // Create a fixture definition to apply our shape to
    val fixtureDef = FixtureDef()
    fixtureDef.shape = circle
    fixtureDef.density = 0.5f
    fixtureDef.friction = 0.4f
    fixtureDef.restitution = 0.6f // Make it bounce a little bit

    // Create our fixture and attach it to the body
    val fixture = body.createFixture(fixtureDef)
}

class Application(private val world: World): ApplicationAdapter() {
    private val renderer by lazy { Box2DDebugRenderer() }
    private val camera  by lazy { OrthographicCamera() }

    private val rotationSpeed = 0.5f

    override fun create() {
        renderer.isDrawAABBs = false
        renderer.isDrawVelocities = true

        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()
        camera.setToOrtho(false, w, h)
        camera.position.setZero()
        camera.update()
    }

    val sr by lazy { ShapeRenderer() }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        handleInput()
        camera.update()

        objects.forEach { it.update(Gdx.graphics.deltaTime) }
        world.step(Gdx.graphics.deltaTime, 6, 2)
        renderer.render(world, camera.combined);

//        sr.projectionMatrix = camera.combined
//        sr.setAutoShapeType(true)
//        sr.begin()
//        sr.circle(0f, 0f, Math.sqrt(Math.pow(30.0, 2.0) + Math.pow(30.0, 2.0)).toFloat())
//        sr.circle(0f, 0f, Math.sqrt(Math.pow(50.0, 2.0) + Math.pow(50.0, 2.0)).toFloat())
//        sr.circle(0f, 0f, Math.sqrt(Math.pow(70.0, 2.0) + Math.pow(70.0, 2.0)).toFloat())
//        sr.circle(0f, 0f, Math.sqrt(Math.pow(85.0, 2.0) + Math.pow(85.0, 2.0)).toFloat())
//        sr.end()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3f, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3f, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0f, -3f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0f, 3f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.rotate(-rotationSpeed, 0f, 0f, 1f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.rotate(rotationSpeed, 0f, 0f, 1f)
        }

        camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 1000 / camera.viewportWidth)

//        val effectiveViewportWidth = camera.viewportWidth * camera.zoom
//        val effectiveViewportHeight = camera.viewportHeight * camera.zoom
//
//        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f)
//        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f)
    }
}