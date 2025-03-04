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

package org.http4s.crypto

import cats.effect.kernel.Async
import cats.effect.kernel.Sync
import cats.syntax.all._
import scodec.bits.ByteVector

import scala.scalajs.js

private[crypto] trait HmacKeyGenCompanionPlatform {
  implicit def forAsyncOrSync[F[_]](implicit F0: Priority[Async[F], Sync[F]]): HmacKeyGen[F] =
    if (facade.isNodeJSRuntime)
      new UnsealedHmacKeyGen[F] {
        import facade.node._

        override def generateKey[A <: HmacAlgorithm](algorithm: A): F[SecretKey[A]] =
          F0.fold { F =>
            F.async_[SecretKey[A]] { cb =>
              crypto.generateKey(
                "hmac",
                GenerateKeyOptions(algorithm.minimumKeyLength),
                (err, key) =>
                  cb(
                    Option(err)
                      .map(js.JavaScriptException)
                      .toLeft(SecretKeySpec(ByteVector.view(key.`export`()), algorithm)))
              )
            }
          } { F =>
            F.delay {
              val key =
                crypto.generateKeySync("hmac", GenerateKeyOptions(algorithm.minimumKeyLength))
              SecretKeySpec(ByteVector.view(key.`export`()), algorithm)
            }
          }

      }
    else
      F0.getPreferred
        .map { implicit F: Async[F] =>
          new UnsealedHmacKeyGen[F] {
            import facade.browser._
            override def generateKey[A <: HmacAlgorithm](algorithm: A): F[SecretKey[A]] =
              for {
                key <- F.fromPromise(
                  F.delay(
                    crypto
                      .subtle
                      .generateKey(
                        HmacKeyGenParams(algorithm.toStringWebCrypto),
                        true,
                        js.Array("sign"))))
                exported <- F.fromPromise(F.delay(crypto.subtle.exportKey("raw", key)))
              } yield SecretKeySpec(ByteVector.view(exported), algorithm)
          }
        }
        .getOrElse(throw new UnsupportedOperationException(
          "HmacKeyGen[F] on browsers requires Async[F]"))

}
