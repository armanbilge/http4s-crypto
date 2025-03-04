/*
 * Copyright 2021 http4s.org
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

package org.http4s.crypto.facade.browser

import scala.annotation.nowarn
import scala.scalajs.js

@js.native
@nowarn("cat=unused")
private[crypto] trait SubtleCrypto extends js.Any {

  def digest(
      algorithm: String,
      data: js.typedarray.ArrayBuffer): js.Promise[js.typedarray.ArrayBuffer] = js.native

  def exportKey(format: String, key: CryptoKey): js.Promise[js.typedarray.ArrayBuffer] =
    js.native

  def generateKey(
      algorithm: HmacKeyGenParams,
      extractable: Boolean,
      keyUsages: js.Array[String]): js.Promise[HmacCryptoKey] = js.native

  def importKey(
      format: String,
      keyData: js.typedarray.Uint8Array,
      algorithm: HmacImportParams,
      extractable: Boolean,
      keyUsages: js.Array[String]
  ): js.Promise[HmacCryptoKey] = js.native

  def sign(
      algorithm: String,
      key: CryptoKey,
      data: js.typedarray.ArrayBuffer): js.Promise[js.typedarray.ArrayBuffer] = js.native

}
