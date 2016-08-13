package the.game.api.objects.space

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef
import org.slf4j.LoggerFactory

/**
 * TODO: javadoc
 * Created by Michael Ilyin on 02.07.2016.
 */
data class PlanetOptions(val initialPosition: Vector2,
                    val size: Float,
                    val speed: Float);

class Planet(private val options: PlanetOptions) : SpaceObject() {

    companion object {
        private val log = LoggerFactory.getLogger(Planet::class.java)
    }

    override fun init(world: World) {
        log.debug("Planet initialisation [{}] {}", id, options)
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(options.initialPosition)

        body = world.createBody(bodyDef)

        val shape = CircleShape()
        shape.radius = options.size

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.friction = 1f
        fixtureDef.density = 0.1f
        fixtureDef.isSensor = true
        fixtureDef.restitution = 0.1f
        fixture = body.createFixture(fixtureDef)

        shape.dispose()

        val anchorPoint = createBox(world, 0.1f, 0.1f, 0f, 0f)
        anchorPoint.type = BodyDef.BodyType.StaticBody

        val rotator = createBox(world, 1f, 1f, 0f, 0f)
        rotator.type = BodyDef.BodyType.DynamicBody

        val revoluteJointDef = RevoluteJointDef()
        revoluteJointDef.initialize(anchorPoint, rotator, anchorPoint.worldCenter)
        revoluteJointDef.enableMotor = true
        revoluteJointDef.motorSpeed = options.speed * options.size
        revoluteJointDef.maxMotorTorque = options.speed * options.size

        val weldJointDef = WeldJointDef()
        weldJointDef.initialize(rotator, body, rotator.worldCenter)

        world.createJoint(revoluteJointDef)
        world.createJoint(weldJointDef)
        log.debug("Planet initialized [{}]", id)
    }

    override fun update(delta: Float) {

    }

    private fun createBox(world: World, w: Float, h: Float, x: Float, y: Float): Body {
        val nodeBodyDefinition = BodyDef()
        nodeBodyDefinition.type = BodyDef.BodyType.DynamicBody
        nodeBodyDefinition.position.set(0f, 0f)

        val shape = PolygonShape()
        val density = 1.0f
        shape.setAsBox(w / 2.0f, h / 2.0f)

        val body = world.createBody(nodeBodyDefinition)
        body.setUserData(this)
        body.setTransform(x, y, 0f)
        val nodeFixtureDefinition = createFixtureDefinition(shape, density)

        body.createFixture(nodeFixtureDefinition)
        shape.dispose()

        return body
    }

    private fun createFixtureDefinition(shape: Shape, density: Float): FixtureDef {
        val nodeFixtureDefinition = FixtureDef()
        nodeFixtureDefinition.shape = shape
        nodeFixtureDefinition.isSensor = true
        nodeFixtureDefinition.friction = 1f
        nodeFixtureDefinition.density = density
        nodeFixtureDefinition.restitution = 0.1f
        return nodeFixtureDefinition
    }
}