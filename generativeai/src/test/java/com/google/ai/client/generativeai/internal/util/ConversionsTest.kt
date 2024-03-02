package com.google.ai.client.generativeai.internal.util

import com.google.ai.client.generativeai.internal.api.shared.Content
import com.google.ai.client.generativeai.internal.api.shared.TextPart
import com.google.ai.client.generativeai.type.content
import io.kotest.matchers.shouldBe
import org.junit.Test

class ConversionsTest {

  @Test
  fun `test content conversion toInternal (role not mentioned)`() {
    val content = content { text("test") }.toInternal()
    content.run {
      // default role should be a "user"
      role shouldBe "user"

      // only one part should be present
      parts.size shouldBe 1
      parts[0].run { (this as TextPart).text shouldBe "test" }
    }
  }

  @Test
  fun `test content conversion toInternal (role mentioned)`() {
    val content = content(role = "model") { text("test") }.toInternal()
    content.run {
      // Role should be a "model"
      role shouldBe "model"

      // only one part should be present
      parts.size shouldBe 1
      parts[0].run { (this as TextPart).text shouldBe "test" }
    }
  }

  @Test
  fun `test content conversion toPublic (role not mentioned)`() {
    val content = Content(parts = listOf(TextPart("test"))).toPublic()
    content.role shouldBe "user"
  }

  @Test
  fun `test content conversion toPublic (role mentioned)`() {
    val content = Content(role = "model", parts = listOf(TextPart("test"))).toPublic()
    content.role shouldBe "model"
  }
}
