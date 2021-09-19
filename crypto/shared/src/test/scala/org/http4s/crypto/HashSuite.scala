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

import munit.CatsEffectSuite
import cats.effect.IO
import scodec.bits.ByteVector

class HashSuite extends CatsEffectSuite {

  import HashAlgorithm._

  val data = ByteVector.encodeAscii("The quick brown fox jumps over the lazy dog").toOption.get

  def testHash(algorithm: HashAlgorithm, expect: String) =
    test(algorithm.toString) {
      assertIO(
        Hash[IO].digest(algorithm, data),
        ByteVector.fromHex(expect).get
      )
    }

  if (Set("JVM", "NodeJS").contains(BuildInfo.runtime))
    testHash(MD5, "9e107d9d372bb6826bd81d3542a419d6")
  testHash(SHA1, "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12")
  testHash(SHA256, "d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592")
  testHash(
    SHA512,
    "07e547d9586f6a73f73fbac0435ed76951218fb7d0c8d788a309d785436bbb642e93a252a954f23912547d1e8a3b5ed6e1bfd7097821233fa0538f3db854fee6")

}
