package com.lzx.userfulktext.ext.anim

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View

class ObjectAnim : Anim() {
    companion object {
        private const val TRANSLATION_X = "translationX"
        private const val TRANSLATION_Y = "translationY"
        private const val SCALE_X = "scaleX"
        private const val SCALE_Y = "scaleY"
        private const val ALPHA = "alpha"
        private const val ROTATION = "rotation"
        private const val ROTATION_X = "rotationX"
        private const val ROTATION_Y = "rotationY"
    }

    override var animator: Animator = ObjectAnimator()

    private val objectAnimator
        get() = animator as ObjectAnimator

    var translationX: FloatArray? = null
        set(value) {
            field = value
            translationX?.let { PropertyValuesHolder.ofFloat(TRANSLATION_X, *it) }?.let { property ->
                valuesHolder[TRANSLATION_X] = property
                objectAnimator.setValues(*valuesHolder.values.toTypedArray())
            }
        }
    var translationY: FloatArray? = null
        set(value) {
            field = value
            translationY?.let { PropertyValuesHolder.ofFloat(TRANSLATION_Y, *it) }?.let { property ->
                valuesHolder[TRANSLATION_Y] = property
                objectAnimator.setValues(*valuesHolder.values.toTypedArray())
            }
        }
    var scaleX: FloatArray? = null
        set(value) {
            field = value
            scaleX?.let { PropertyValuesHolder.ofFloat(SCALE_X, *it) }?.let { property ->
                valuesHolder[SCALE_X] = property
                objectAnimator.setValues(*valuesHolder.values.toTypedArray())
            }
        }
    var scaleY: FloatArray? = null
        set(value) {
            field = value
            scaleY?.let { PropertyValuesHolder.ofFloat(SCALE_Y, *it) }?.let { property ->
                valuesHolder[SCALE_Y] = property
                objectAnimator.setValues(*valuesHolder.values.toTypedArray())
            }
        }
    var alpha: FloatArray? = null
        set(value) {
            field = value
            alpha?.let { PropertyValuesHolder.ofFloat(ALPHA, *it) }?.let { property ->
                valuesHolder[ALPHA] = property
                objectAnimator.setValues(*valuesHolder.values.toTypedArray())
            }
        }
    var rotation: FloatArray? = null
        set(value) {
            field = value
            rotation?.let { PropertyValuesHolder.ofFloat(ROTATION, *it) }?.let { property ->
                valuesHolder[ROTATION] = property
                objectAnimator.setValues(*valuesHolder.values.toTypedArray())
            }
        }
    var rotationX: FloatArray? = null
        set(value) {
            field = value
            rotationX?.let { PropertyValuesHolder.ofFloat(ROTATION_X, *it) }?.let { property ->
                valuesHolder[ROTATION_X] = property
                objectAnimator.setValues(*valuesHolder.values.toTypedArray())
            }
        }
    var rotationY: FloatArray? = null
        set(value) {
            field = value
            rotationY?.let { PropertyValuesHolder.ofFloat(ROTATION_Y, *it) }?.let { property ->
                valuesHolder[ROTATION_Y] = property
                objectAnimator.setValues(*valuesHolder.values.toTypedArray())
            }
        }

    var target: Any? = null
        set(value) {
            field = value
            (animator as ObjectAnimator).target = value
        }

    var repeatCount
        get() = 0
        set(value) {
            objectAnimator.repeatCount = value
        }

    var repeatMode
        get() = ValueAnimator.RESTART
        set(value) {
            objectAnimator.repeatMode = value
        }

    private val valuesHolder = mutableMapOf<String, PropertyValuesHolder>()

    override fun reverse() {
        valuesHolder.forEach { valuesHolder ->
            when (valuesHolder.key) {
                TRANSLATION_X -> translationX?.let {
                    it.reverse()
                    this.valuesHolder[TRANSLATION_X]?.setFloatValues(*it)
                }
                TRANSLATION_Y -> translationY?.let {
                    it.reverse()
                    this.valuesHolder[TRANSLATION_Y]?.setFloatValues(*it)
                }
                SCALE_X -> scaleX?.let {
                    it.reverse()
                    this.valuesHolder[SCALE_X]?.setFloatValues(*it)
                }
                SCALE_Y -> scaleY?.let {
                    it.reverse()
                    this.valuesHolder[SCALE_Y]?.setFloatValues(*it)
                }
                ALPHA -> alpha?.let {
                    it.reverse()
                    this.valuesHolder[ALPHA]?.setFloatValues(*it)
                }
                ROTATION -> rotation?.let {
                    it.reverse()
                    this.valuesHolder[ROTATION]?.setFloatValues(*it)
                }
                ROTATION_X -> rotationX?.let {
                    it.reverse()
                    this.valuesHolder[ROTATION_X]?.setFloatValues(*it)
                }
                ROTATION_Y -> rotationY?.let {
                    it.reverse()
                    this.valuesHolder[ROTATION_Y]?.setFloatValues(*it)
                }
            }
        }
    }

    override fun toBeginning() {
        valuesHolder.forEach { valuesHolder ->
            when (valuesHolder.key) {
                TRANSLATION_X -> translationX?.let {
                    (target as? View)?.translationX = it.first()
                }
                TRANSLATION_Y -> translationY?.let {
                    (target as? View)?.translationY = it.first()
                }
                SCALE_X -> scaleX?.let {
                    (target as? View)?.scaleX = it.first()
                }
                SCALE_Y -> scaleY?.let {
                    (target as? View)?.scaleY = it.first()
                }
                ALPHA -> alpha?.let {
                    (target as? View)?.alpha = it.first()
                }
                ROTATION -> rotation?.let {
                    (target as? View)?.rotation = it.first()
                }
                ROTATION_X -> rotationX?.let {
                    (target as? View)?.rotationX = it.first()
                }
                ROTATION_Y -> rotationY?.let {
                    (target as? View)?.rotationY = it.first()
                }
            }
        }
    }

    fun setPropertyValueHolder() {
        translationX?.let { PropertyValuesHolder.ofFloat(TRANSLATION_X, *it) }?.let { valuesHolder[TRANSLATION_X] = it }
        translationY?.let { PropertyValuesHolder.ofFloat(TRANSLATION_Y, *it) }?.let { valuesHolder[TRANSLATION_Y] = it }
        scaleX?.let { PropertyValuesHolder.ofFloat(SCALE_X, *it) }?.let { valuesHolder[SCALE_X] = it }
        scaleY?.let { PropertyValuesHolder.ofFloat(SCALE_Y, *it) }?.let { valuesHolder[SCALE_Y] = it }
        alpha?.let { PropertyValuesHolder.ofFloat(ALPHA, *it) }?.let { valuesHolder[ALPHA] = it }
        rotation?.let { PropertyValuesHolder.ofFloat(ROTATION, *it) }?.let { valuesHolder[ROTATION] = it }
        rotationX?.let { PropertyValuesHolder.ofFloat(ROTATION_X, *it) }?.let { valuesHolder[ROTATION_X] = it }
        rotationY?.let { PropertyValuesHolder.ofFloat(ROTATION_Y, *it) }?.let { valuesHolder[ROTATION_Y] = it }
        objectAnimator.setValues(*valuesHolder.values.toTypedArray())
    }
}