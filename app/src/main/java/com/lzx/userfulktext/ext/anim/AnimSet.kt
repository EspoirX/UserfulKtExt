package com.lzx.userfulktext.ext.anim

import android.animation.Animator
import android.animation.AnimatorSet

/**
 * 用法
 *
 * animSet {
 *      anim {
 *          values = floatArrayOf(1.0f, 1.4f)
 *          action = { value -> tv.scaleX = (value as Float) }
 *      } with anim {
 *          values = floatArrayOf(0f, -200f)
 *          action = { value -> tv.translationY = (value as Float) }
 *      }
 *      duration = 200L
 * }
 *
 *
 * animSet {
 *      objectAnim {
 *          target = tvTitle
 *          translationX = floatArrayOf(0f, 200f)
 *          alpha = floatArrayOf(1.0f, 0.3f)
 *          scaleX = floatArrayOf(1.0f, 1.3f)
 *      }
 *      duration = 100L
 * }
 *
 */
class AnimSet : Anim() {
    override var animator: Animator = AnimatorSet()

    private val animatorSet
        get() = animator as AnimatorSet

    private val anims by lazy { mutableListOf<Anim>() }

    /**
     * 动画是否在起点
     */
    var isAtStartPoint: Boolean = true

    /**
     * 动画值是否反转
     */
    private var hasReverse: Boolean = false

    fun anim(animCreation: ValueAnim.() -> Unit): Anim = ValueAnim()
        .apply(animCreation)
        .also { it.addListener() }
        .also { anims.add(it) }

    fun objectAnim(action: ObjectAnim.() -> Unit): Anim = ObjectAnim()
        .apply(action)
        .also { it.setPropertyValueHolder() }
        .also { it.addListener() }
        .also { anims.add(it) }

    fun isRunning(): Boolean = animatorSet.isRunning

    fun start() {
        if (animatorSet.isRunning) return
        anims.takeIf { hasReverse }?.forEach { anim -> anim.reverse() }.also { hasReverse = false }
        if (anims.size == 1) animatorSet.play(anims.first().animator)
        if (hasReverse && isAtStartPoint) {
            animatorSet.start()
            isAtStartPoint = false
        } else {
            animatorSet.start()
        }
    }

    override fun reverse() {
        if (animatorSet.isRunning) return
        anims.takeIf { !hasReverse }?.forEach { anim -> anim.reverse() }.also { hasReverse = true }
        if (hasReverse && !isAtStartPoint) {
            animatorSet.start()
            isAtStartPoint = true
        }
    }

    override fun toBeginning() {
        anims.forEach { it.toBeginning() }
    }

    fun getAnim(index: Int) = anims.takeIf { index in 0 until anims.size }?.let { it[index] }

    fun cancel() {
        animatorSet.cancel()
    }

    /**
     * 如果想一个接一个的播放动画，可以用 before
     * animSet {
     *      anim {
     *          value = floatArrayOf(1.0f, 1.4f)
     *          action = { value -> tv.scaleX = (value as Float) }
     *      } before anim {
     *          values = floatArrayOf(0f, -200f)
     *          action = { value -> btn.translationY = (value as Float) }
     *      }
     *      duration = 200L
     * }
     *
     */
    infix fun Anim.before(anim: Anim): Anim {
        animatorSet.play(animator).before(anim.animator).let { this.builder = it }
        return anim
    }

    /**
     * 如果想同时播放动画，用 with
     *
     * animSet {
     *      play {
     *          value = floatArrayOf(1.0f, 1.4f)
     *          action = { value -> tv.scaleX = (value as Float) }
     *      } with anim {
     *          values = floatArrayOf(0f, -200f)
     *          action = { value -> btn.translationY = (value as Float) }
     *      }
     *      duration = 200L
     * }
     *
     * 如果一个调用链中同时具有[with]和[before]，则[with]具有更高的优先级，例如：`a在b之前与c`意味着b和c将同时播放，并且在它们之前播放
     *
     */
    infix fun Anim.with(anim: Anim): Anim {
        if (builder == null) builder = animatorSet.play(animator).with(anim.animator)
        else builder?.with(anim.animator)
        return anim
    }
}

fun animSet(creation: AnimSet.() -> Unit) = AnimSet().apply { creation() }.also { it.addListener() }
