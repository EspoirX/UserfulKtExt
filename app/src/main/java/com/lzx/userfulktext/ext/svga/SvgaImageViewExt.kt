package com.lzx.userfulktext.ext.svga

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.LruCache
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lzx.userfulktext.App
import com.lzx.userfulktext.ext.display.dp
import com.opensource.svgaplayer.*
import com.opensource.svgaplayer.glideplugin.asSVGA
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URL

/**
 * 开始帧动画
 */
@SuppressLint("CheckResult")
fun SVGAImageView.startPlaySVGA(assetsResName: String?) {
    if (assetsResName.isNullOrEmpty()) {
        return
    }
    val data = AnimationCache.mCache.get(assetsResName)
    if (data != null) {
        playSVGA(data)
        return
    }
    Maybe.create<SVGAVideoEntity> {
        Glide.with(context).asSVGA().load(assetsResName)
            .into(object : SimpleTarget<SVGAVideoEntity>() {
                override fun onResourceReady(
                    svgaVideoEntity: SVGAVideoEntity,
                    transition: Transition<in SVGAVideoEntity>?
                ) {
                    AnimationCache.mCache.put(assetsResName, svgaVideoEntity)
                    it.onSuccess(svgaVideoEntity)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    it.onError(Throwable("SVGAImageView $assetsResName 解析错误"))
                }
            })
    }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            playSVGA(it)
        }, {})
}

/**
 * 播放动画
 */
private fun SVGAImageView.playSVGA(it: SVGAVideoEntity) {
    if (!this.isAnimating) {
        SVGADrawable(it).let {
            setImageDrawable(it)
            if (!isAnimating) {
                startAnimation()
            }
        }
    }
}

object AnimationCache {
    //缓存池避免重复解析
    const val TAG = "SvgaImageViewExt"
    val mCache = LruCache<String, SVGAVideoEntity>(15)
}


fun svgaSet(creation: SvgaPlayer.() -> Unit) =
    SvgaPlayer().apply { creation() }.also { it.addListener() }

class SvgaPlayer {

    var target: SVGAImageView? = null
    var needTarget: Boolean = true
    var loadByGlide: Boolean = true
    var svgaUrl: String? = null
    var context: Context = App.context
    var playWhenSuccess: Boolean = true
    private var svgaText: SvgaText? = null
    private var svgaImage: SvgaImage? = null

    var onFinished: (() -> Unit)? = null
    var onPause: (() -> Unit)? = null
    var onRepeat: (() -> Unit)? = null
    var onStep: (() -> Unit)? = null
    var onBeforePlay: (() -> Unit)? = null
    var onBeforeSuccess: ((drawable: SVGADrawable, videoHeight: Int, videoWidth: Int) -> Unit)? =
        null
    var onLoadSvgaSuccess: (() -> Unit)? = null
    var onLoadSvgaFailed: (() -> Unit)? = null

    internal fun addListener(view: SVGAImageView? = null) {
        if (onFinished == null && onPause == null && onRepeat == null && onStep == null) {
            return
        }
        val svgaView = view ?: target
        svgaView?.callback = object : SVGACallback {
            override fun onFinished() {
                onFinished?.invoke()
            }

            override fun onPause() {
                onPause?.invoke()
            }

            override fun onRepeat() {
                onRepeat?.invoke()
            }

            override fun onStep(frame: Int, percentage: Double) {
                onStep?.invoke()
            }
        }
    }

    fun svgaImage(imageCreation: SvgaImage.() -> Unit): SvgaImage = SvgaImage()
        .apply(imageCreation)
        .also { svgaImage = it }

    fun svgaText(textCreation: SvgaText.() -> Unit): SvgaText = SvgaText()
        .apply(textCreation)
        .also { svgaText = it }

    fun clearSvga() {
        target?.callback = null
        onFinished = null
        onPause = null
        onRepeat = null
        onStep = null
        onBeforePlay = null
        onBeforeSuccess = null
        onLoadSvgaSuccess = null
        onLoadSvgaFailed = null
    }

    fun playSvga() {
        if (svgaUrl.isNullOrEmpty()) {
            onLoadSvgaFailed?.invoke()
            return
        }
        if (needTarget && target == null) {
            onLoadSvgaFailed?.invoke()
            return
        }
        var dynamicItem: SVGADynamicEntity? = null
        if (svgaImage != null || svgaText != null) {
            dynamicItem = SVGADynamicEntity()
            if (svgaImage != null) {
                dynamicItem.setDynamicImage(svgaImage!!.url, svgaImage!!.key)
            }
            if (svgaText != null) {
                dynamicItem.setDynamicText(svgaText!!.text, svgaText!!.build(), svgaText!!.key)
            }
        }
        onBeforePlay?.invoke()
        if (loadByGlide) {
            Glide.with(context).asSVGA().load(svgaUrl)
                .into(object : SimpleTarget<SVGAVideoEntity?>() {

                    override fun onResourceReady(
                        resource: SVGAVideoEntity,
                        transition: Transition<in SVGAVideoEntity?>?
                    ) {
                        val drawable = if (dynamicItem == null) SVGADrawable(resource)
                        else SVGADrawable(resource, dynamicItem)
                        val videoHeight = resource.videoSize.height.toInt().dp
                        val videoWidth = resource.videoSize.width.toInt().dp
                        onBeforeSuccess?.invoke(drawable, videoHeight, videoWidth)
                        if (playWhenSuccess) {
                            target?.setImageDrawable(drawable)
                            target?.startAnimation()
                        }
                        onLoadSvgaSuccess?.invoke()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        onLoadSvgaFailed?.invoke()
                        clearSvga()
                    }
                })
        } else {
            //当svga有声音的时候，需要用原生方式播放
            try {
                val parser = SVGAParser(context)
                parser.decodeFromURL(URL(svgaUrl), object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        val drawable = if (dynamicItem == null) SVGADrawable(videoItem)
                        else SVGADrawable(videoItem, dynamicItem)
                        val videoHeight = videoItem.videoSize.height.toInt().dp
                        val videoWidth = videoItem.videoSize.width.toInt().dp
                        onBeforeSuccess?.invoke(drawable, videoHeight, videoWidth)
                        if (playWhenSuccess) {
                            target?.setImageDrawable(drawable)
                            target?.startAnimation()
                        }
                        onLoadSvgaSuccess?.invoke()
                    }

                    override fun onError() {
                        onLoadSvgaFailed?.invoke()
                        clearSvga()
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                onLoadSvgaFailed?.invoke()
                clearSvga()
            }
        }
    }
}

class SvgaText {
    var colorA: Int = 255
    var colorR: Int = 255
    var colorG: Int = 255
    var colorB: Int = 255
    var textSize: Float = 20f
    var textAlign: Paint.Align = Paint.Align.LEFT
    var text: String = ""
    var key: String = ""

    fun build(): TextPaint {
        val textPaint = TextPaint()
        textPaint.setARGB(colorA, colorR, colorG, colorB)
        textPaint.textSize = textSize
        textPaint.textAlign = textAlign
        return textPaint
    }
}

class SvgaImage {
    var url: String = ""
    var key: String = ""
}