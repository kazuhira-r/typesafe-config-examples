package org.littlewings.config

import com.typesafe.config.{Config, ConfigFactory}

import net.ceedubs.ficus.FicusConfig._
import net.ceedubs.ficus.SimpleConfigKey
import net.ceedubs.ficus.readers.ValueReader

import org.scalatest.FunSpec
import org.scalatest.Matchers._

class FicusConfigSpec extends FunSpec {
  describe("typesafe-config spec") {
    it("default configration load") {
      val config: Config = ConfigFactory.load()

      // as、getAs、applyのいずれかを使う
      config.as[String]("app.name") should be ("ConfigTest")
      config.getAs[String]("app.name") should be (Some("ConfigTest"))  // Optionになる
      config.apply[String](SimpleConfigKey("app.name")) should be ("ConfigTest")

      // Optionとして受けとることができる
      config.as[Option[String]]("multibyte.value") should be (Some("こんにちは 世界"))
      config.as[Int]("int.value") should be (10)
      config.as[List[String]]("strings") should contain theSameElementsInOrderAs Array("foo", "bar", "hoge")

      // Optionが使用できるため、存在しない項目に対しても安全
      an [com.typesafe.config.ConfigException$Missing] should be thrownBy config.as[String]("missing.entry")
      config.as[Option[String]]("missing.entry") should be (None)

      // Setとして受け取ることも可能
      config.as[Set[String]]("strings") should contain only ("foo", "bar", "hoge")
    }

    it("json configuration load") {
      // ファイル名を指定
      val config = ConfigFactory.load("configtest.json")

      config.as[Option[String]]("config.string") should be (Some("こんにちは、世界"))
      // OptionのListとして受けることも可能
      config.as[Option[List[Int]]]("config.array") should be (Some(List(1, 2, 3)))
      config.as[Int]("config.more-nested.numeric") should be (10)

      // ネストした部分を、Configとしても取得できる
      val c = config.as[Config]("config")
      c.as[String]("string") should be ("こんにちは、世界")
      c.as[Set[Int]]("array") should contain only (1, 2, 3)

      // さすがに、ConfigListのサポートはなさそう…
      config.as[Array[Int]]("config.array") should contain theSameElementsInOrderAs Array(1, 2, 3)
      config.as[Array[Int]]("config.array").apply(0) should be (1)
      config.as[Array[Int]]("config.array").apply(1) should be (2)
      config.as[Array[Int]]("config.array").apply(2) should be (3)
    }

    it("property like configuration load") {
      // Property形式でも、ネストした構造を扱える
      val config = ConfigFactory.load("propertylike1.conf")

      val c = config.as[Config]("ns")
      c.as[String]("string.value") should be ("Hello World")
      c.as[Int]("intvalue") should be (20)

      val nested = config.as[Config]("ns.nested")
      nested.as[Int]("intvalue") should be (10)
      nested.as[String]("stringvalue") should be ("string")
      nested.as[String]("substitutions") should be ("Hello World 20")
    }

    it("HOCON configuration load") {
      // HOCON（Human-Optimized Config Object Notation）という形式でも書けるらしい
      val config = ConfigFactory.load("hocon.conf")

      // Doubleのような場合は、Numberではなくて具体的な型になるらしい
      config.as[Double]("hocon-config.nested.numeric") should be (10.5)

      val hc = config.as[Config]("hocon-config")
      hc.as[String]("name") should be ("HOCON Presentation Configuration")
      hc.as[Int]("int-value") should be (1)

      hc.as[Set[String]]("nested.list-values") should contain only ("foo", "bar", "fuga")

      val nested = hc.as[Config]("nested")
      nested.as[Double]("numeric") should be (10.5)
    }

    it("using Case Class") {
      // 設定ファイルの内容を、独自のCase Classで扱うパターン
      val config = ConfigFactory.load("configtest.json")

      case class MyConfig(string: String, array: Array[Int], numeric: Int)

      // Implicit Valとして、ValueReaderを定義すれば任意の型に変換可能
      implicit val myConfigReader: ValueReader[MyConfig] = ValueReader.relative { config =>
        MyConfig(config.as[String]("string"),
                 config.as[Array[Int]]("array"),
                 config.as[Int]("more-nested.numeric"))
      }

      val myConfig = config.as[MyConfig]("config")
      myConfig.string should be ("こんにちは、世界")
      myConfig.array should contain theSameElementsInOrderAs Array(1, 2, 3)
      myConfig.numeric should be (10)
    }
  }
}
