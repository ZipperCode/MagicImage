# MagicImageView
[![](https://jitpack.io/v/ZipperCode/MagicImage.svg)](https://jitpack.io/#ZipperCode/MagicImage)
## 项目介绍
在开发流程中，需要加载不同的图片或者动画，比如`SVGA`和`PAG`。

加载的资源又可能来自resource、asset、网络、本地文件，同时资源的格式又比较多，比如SVGA、PAG等，需要增加许多额外的判断。

目前存在常见的处理方式是:

- 1、xml中手写布局，手动加载

```
- 加载普通图片
Glide##into、BitmapFactory#decode
- 加载SVGA
SVGAParse.parse
- 加载pag
PAGFile.Load
```

加载过程太过繁琐，每次使用需要判断 + 写一堆加载的逻辑

- 2、封装一层View，把用到的View都加入到这个View中，然后在这个View中同样封装一大堆加载和判断逻辑，一大堆逻辑写到这个View中变得非重

得益与Glide的扩展性，加载逻辑完全可以使用Glide进行处理，在`2`的基础上进行扩展，把其中的加载逻给 Glide 去加载，封装的View只做好展示逻辑就行

## 使用方法

### 添加依赖

```
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    val version = "1.0.0"
    // 核心库，可直接当做ImageView去处理
    implementation("com.github.ZipperCode.MagicImage:core:${version}")
    // pag扩展，可选。需要使用到pag时引入
    implementation("com.github.ZipperCode.MagicImage:pag:${version}")
    // svga扩展，可选。需要使用到svga时引入
    implementation("com.github.ZipperCode.MagicImage:svgap:${version}")
}
```

### 使用方式

Glide模块自动注册，App中需要实现AppGlideModule
如果没有实现AppGlideModule的话，需要手动注册

[PAG Glide注册](./lib_pag/src/main/java/com/zipper/magicimage/pag/MagicPagGlideModule.kt)

[SVGA Glide注册](./lib_pag/src/main/java/com/zipper/magicimage/svga/MagicSvgaGlideModule.kt)

布局
```
<com.zipper.magicimage.MagicImageView
    android:id="@+id/magicImageView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

Glide直接加载对应的文件，不需要做任何处理
```
Glide.with(context)
    .load("file:///android_asset/xxx")
    .load("http://xxx")
    .load("/xxx/xxx")
    .into(magicImageView)
```

监听动画,动画生命周期根据扩展View的支持情况而回调
```
imageView.addAnimateListener { intent ->
    when(intent) {
        is AnimateStartIntent -> {
            Log.d("MagicImage", "animateStart")
        }
        is AnimateEndIntent ->{
            Log.d("MagicImage", "animateEnd")
        }
        is AnimateRepeatIntent -> {
            Log.d("MagicImage", "animateRepeat")
        }
        is AnimateUpdateIntent -> {
            Log.d("MagicImage", "animateUpdate")
        }

        else -> Unit
    }
}
```

### 属性说明

| 属性名 | 属性类型 | 默认值 | 说明 |
| :----: | :---: | :---: | :----: |
magicCompatScaleType | ImageView.ScaleType | ImageView.ScaleType.FIT_CENTER | 扩展ImageView的ScaleType |
magicCompatSrc | Drawable | null | 扩展ImageView的src |
magicAutoAnim  | Boolean | false | 是否自动播放动画 |
magicCompatViewSize | Boolean | false | 是否自动设置View的宽高（有部分场景需要按照资源来适应View） |
magicAnimateListener | Boolean | true | 是否监听动画播放完成（针对需要监听不同资源动画播放的回调） |
magicAnimateRepeat | Int | -1 | 循环播放动画次数 |





