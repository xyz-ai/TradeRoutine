package com.traderoutine.ui.strings

import com.traderoutine.model.UiLanguage
import kotlin.random.Random

object CompletionBlessings {
    private val englishMessages = listOf(
        "Congratulations, today's tasks are all complete.",
        "Today's routine has been wrapped up nicely.",
        "Everything planned for today has been finished.",
        "Well done. You can rest now.",
        "Today is complete."
    )

    private val chineseMessages = listOf(
        "今天的任务已经全部完成了。",
        "今天的例行安排已经安稳收尾。",
        "今天计划中的内容都完成了。",
        "做得很好，现在可以休息了。",
        "今天已经完整结束了。"
    )

    fun random(language: UiLanguage): String {
        val messages = when (language) {
            UiLanguage.ENGLISH -> englishMessages
            UiLanguage.CHINESE -> chineseMessages
        }
        return messages[Random.nextInt(messages.size)]
    }
}
