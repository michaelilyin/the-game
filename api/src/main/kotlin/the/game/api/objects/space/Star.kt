package the.game.api.objects.space

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.Sphere
import com.badlogic.gdx.physics.box2d.*
import org.slf4j.LoggerFactory
import the.game.api.objects.WorldObject

/**
 * TODO: javadoc
 * Created by Michael Ilyin on 02.07.2016.
 */
class Star(private val initialSize: Float) : SpaceObject() {

    companion object {
        private val log = LoggerFactory.getLogger(Star::class.java)
    }

    override fun init(world: World) {
        log.debug("Star initialisation [{}]", id)
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.KinematicBody
        bodyDef.gravityScale = 0f
        bodyDef.position.set(Vector2.Zero)

        body = world.createBody(bodyDef)

        val shape = CircleShape()
        shape.radius = initialSize

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixture = body.createFixture(fixtureDef)

        shape.dispose()
        log.debug("Star initialized [{}]", id)
    }

}