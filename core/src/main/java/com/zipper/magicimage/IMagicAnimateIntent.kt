package com.zipper.magicimage

sealed interface IMagicAnimateIntent

data object AnimateStartIntent : IMagicAnimateIntent
data object AnimateCancelIntent : IMagicAnimateIntent
data object AnimateEndIntent : IMagicAnimateIntent
data object AnimateRepeatIntent : IMagicAnimateIntent

data object AnimateUpdateIntent : IMagicAnimateIntent
