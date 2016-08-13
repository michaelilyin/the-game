package the.game.api.objects

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import java.util.concurrent.atomic.AtomicLong

/**
 * TODO: javadoc
 * Created by Michael Ilyin on 02.07.2016.
 */
open class WorldObject {

    companion object {
        private val _idGenerator = AtomicLong()
    }

    val id: Long = _idGenerator.incrementAndGet()

    private lateinit var _body: Body
    private lateinit var _fixture: Fixture

    open fun update(delta: Float): Unit {

    }

    open fun init(world: World): Unit {

    }

    var body: Body
        protected set(value) {
            _body = value
        }
        get() = _body

    var fixture: Fixture
        protected set(value) {
            _fixture = value
        }
        get() = _fixture

}