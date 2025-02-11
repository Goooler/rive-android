package app.rive.runtime.kotlin.core

import android.graphics.RectF
import app.rive.runtime.kotlin.core.errors.AnimationException
import app.rive.runtime.kotlin.core.errors.RiveException
import app.rive.runtime.kotlin.core.errors.StateMachineException

/**
 * [Artboard]s as designed in the Rive animation editor.
 *
 * This object has a counterpart in c++, which implements a lot of functionality.
 * The [unsafeCppPointer] keeps track of this relationship.
 *
 * [Artboard]s provide access to available [Animation]s, and some basic properties.
 * You can [draw] artboards using a [Renderer] that is tied to a canvas.
 *
 * The constructor uses a [unsafeCppPointer] to point to its c++ counterpart object.
 */
class Artboard(unsafeCppPointer: Long) : NativeObject(unsafeCppPointer) {
    private external fun cppName(cppPointer: Long): String

    private external fun cppAnimationByIndex(cppPointer: Long, index: Int): Long
    private external fun cppAnimationByName(cppPointer: Long, name: String): Long
    private external fun cppAnimationCount(cppPointer: Long): Int
    private external fun cppAnimationNameByIndex(cppPointer: Long, index: Int): String

    private external fun cppStateMachineByIndex(cppPointer: Long, index: Int): Long
    private external fun cppStateMachineByName(cppPointer: Long, name: String): Long
    private external fun cppStateMachineCount(cppPointer: Long): Int
    private external fun cppStateMachineNameByIndex(cppPointer: Long, index: Int): String

    private external fun cppAdvance(cppPointer: Long, elapsedTime: Float): Boolean

    // TODO: this will be a cppDraw call after we remove our old renderer.
    private external fun cppDrawSkia(
        cppPointer: Long, rendererPointer: Long
    )

    private external fun cppDrawSkiaAligned(
        cppPointer: Long, rendererPointer: Long,
        fit: Fit, alignment: Alignment,
    )

    private external fun cppBounds(cppPointer: Long): RectF

    external override fun cppDelete(pointer: Long)


    /**
     * Get the [name] of the Artboard.
     */
    val name: String
        get() = cppName(cppPointer)

    /**
     * Get the first [Animation] of the [Artboard].
     *
     * If you use more than one animation, it is preferred to use the [animation] functions.
     */
    val firstAnimation: LinearAnimationInstance
        @Throws(RiveException::class)
        get() {
            return animation(0)
        }

    /**
     * Get the animation at a given [index] in the [Artboard].
     *
     * This starts at 0.
     */
    @Throws(RiveException::class)
    fun animation(index: Int): LinearAnimationInstance {
        val animationPointer = cppAnimationByIndex(cppPointer, index)
        if (animationPointer == NULL_POINTER) {
            throw AnimationException("No Animation found at index $index.")
        }
        val lai = LinearAnimationInstance(animationPointer)
        dependencies.add(lai)
        return lai
    }

    /**
     * Get the animation with a given [name] in the [Artboard].
     */
    @Throws(RiveException::class)
    fun animation(name: String): LinearAnimationInstance {
        val animationPointer = cppAnimationByName(cppPointer, name)
        if (animationPointer == NULL_POINTER) {
            throw AnimationException(
                "Animation \"$name\" not found. " +
                        "Available Animations: ${animationNames.map { "\"$it\"" }}\""
            )
        }
        val lai = LinearAnimationInstance(animationPointer)
        dependencies.add(lai)
        return lai
    }

    /**
     * Get the first [StateMachine] of the [Artboard].
     *
     * If you use more than one animation, it is preferred to use the [stateMachine] functions.
     */
    val firstStateMachine: StateMachineInstance
        @Throws(RiveException::class)
        get() {
            return stateMachine(0)
        }


    /**
     * Get the animation at a given [index] in the [Artboard].
     *
     * This starts at 0.
     */
    @Throws(RiveException::class)
    fun stateMachine(index: Int): StateMachineInstance {
        val stateMachinePointer = cppStateMachineByIndex(cppPointer, index)
        if (stateMachinePointer == NULL_POINTER) {
            throw StateMachineException("No StateMachine found at index $index.")
        }
        val smi = StateMachineInstance(stateMachinePointer)
        dependencies.add(smi)
        return smi
    }

    /**
     * Get the animation with a given [name] in the [Artboard].
     */
    @Throws(RiveException::class)
    fun stateMachine(name: String): StateMachineInstance {
        val stateMachinePointer = cppStateMachineByName(cppPointer, name)
        if (stateMachinePointer == NULL_POINTER) {
            throw StateMachineException("No StateMachine found with name $name.")
        }
        val smi = StateMachineInstance(stateMachinePointer)
        dependencies.add(smi)
        return smi
    }

    /**
     * Get the number of animations stored inside the [Artboard].
     */
    val animationCount: Int
        get() = cppAnimationCount(cppPointer)

    /**
     * Get the number of state machines stored inside the [Artboard].
     */
    val stateMachineCount: Int
        get() = cppStateMachineCount(cppPointer)

    /**
     * Advancing the artboard updates the layout for all dirty components contained in the [Artboard]
     * updates the positions forces all components in the [Artboard] to be laid out.
     *
     * Components are all the shapes, bones and groups of an [Artboard].
     * Whenever components are added to an artboard, for example when an artboard is first loaded, they are considered dirty.
     * Whenever animations change properties of components, move a shape or change a color, they are marked as dirty.
     *
     * Before any changes to components will be visible in the next rendered frame, the artbaord needs to be [advance]d.
     *
     * [elapsedTime] is currently not taken into account.
     */
    fun advance(elapsedTime: Float): Boolean {
        return cppAdvance(cppPointer, elapsedTime)
    }

    /**
     * Draw the the artboard to the [renderer].
     */
    fun drawSkia(rendererAddress: Long) {
        cppDrawSkia(cppPointer, rendererAddress)
    }

    /**
     * Draw the the artboard to the [renderer].
     * Also align the artboard to the render surface
     */
    fun drawSkia(rendererAddress: Long, fit: Fit, alignment: Alignment) {
        cppDrawSkiaAligned(cppPointer, rendererAddress, fit, alignment)
    }

    /**
     * Get the bounds of Artboard as defined in the rive editor.
     */
    val bounds: RectF
        get() = cppBounds(cppPointer)

    /**
     * Get the names of the animations in the artboard.
     */
    val animationNames: List<String>
        get() = (0 until animationCount).map { cppAnimationNameByIndex(cppPointer, it) }

    /**
     * Get the names of the stateMachines in the artboard.
     */
    val stateMachineNames: List<String>
        get() = (0 until stateMachineCount).map { cppStateMachineNameByIndex(cppPointer, it) }
}