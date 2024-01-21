/*
 * Copyright ${YEAR} Shreyas Patil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.shreyaspatil.ai.client.generativeai.type

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.asPlatformImage() =
    dev.shreyaspatil.ai.client.generativeai.type.PlatformImage(
        dev.shreyaspatil.ai.client.generativeai.type.platformImageFromBitmap(this),
    )

fun ByteArray.asBitmap(): Bitmap? =
    dev.shreyaspatil.ai.client.generativeai.type.bitmapFromBytes(this)

private fun platformImageFromBitmap(bitmap: Bitmap): ByteArray {
    return ByteArrayOutputStream().let {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
        it.toByteArray()
    }
}

private fun bitmapFromBytes(data: ByteArray): Bitmap? {
    return android.graphics.BitmapFactory.decodeByteArray(data, 0, data.size)
}
