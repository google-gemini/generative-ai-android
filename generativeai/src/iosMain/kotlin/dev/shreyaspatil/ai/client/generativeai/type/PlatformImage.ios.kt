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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

@OptIn(ExperimentalForeignApi::class)
fun UIImage.asPlatformImage(): PlatformImage {
    val data = UIImagePNGRepresentation(this)
    val bytes = data?.bytes ?: throw IllegalStateException("Unable to convert UIImage to PNG data")

    return PlatformImage(bytes.readBytes(this.size.size))
}

fun ByteArray.asUIImage(): UIImage? = uiImageFromBytes(this)

private fun uiImageFromBytes(data: ByteArray): UIImage? {
    return UIImage.imageWithData(data.toImageBytes())
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toImageBytes(): NSData = memScoped {
    NSData.create(bytes = allocArrayOf(this@toImageBytes), length = this@toImageBytes.size.toULong())
}
