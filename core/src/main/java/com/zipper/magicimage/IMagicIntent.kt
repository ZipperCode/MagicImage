package com.zipper.magicimage

sealed interface IMagicIntent

data object OnLoadStartedIntent : IMagicIntent

data object OnLoadFailedIntent : IMagicIntent
data object OnLoadClearedIntent : IMagicIntent

data object OnStartIntent : IMagicIntent
data object OnStopIntent : IMagicIntent

data object PauseHideIntent : IMagicIntent

data class RepeatCount(val count: Int) : IMagicIntent