package org.littlewings.config

import com.typesafe.config.{Config, ConfigFactory}

import org.scalatest.FunSpec
import org.scalatest.Matchers._

class TypeSafeConfigSpec extends FunSpec {
  describe("typesafe-config spec") {
    it("default configration load") {
      val config: Config = ConfigFactory.load()

      config.getString("app.name") should be ("ConfigTest")
      config.getString("multibyte.value") should be ("こんにちは 世界")
      config.getInt("int.value") should be (10)
      config.getStringList("strings") should contain theSameElementsInOrderAs Array("foo", "bar", "hoge")
    }

    it("json configuration load") {
      // ファイル名を指定
      val config = ConfigFactory.load("configtest.json")

      config.getString("config.string") should be ("こんにちは、世界")
      config.getIntList("config.array") should contain theSameElementsInOrderAs Array(1, 2, 3)
      config.getInt("config.more-nested.numeric") should be (10)

      // ネストした部分を、Configとしても取得できる
      val c = config.getConfig("config")
      c.getString("string") should be ("こんにちは、世界")
      c.getIntList("array") should contain theSameElementsInOrderAs Array(1, 2, 3)

      // Config#getListした場合は、Listを実装したConfigListが返却される
      // ConfigListの中身は、ConfigValueでunrappedで戻せる
      config.getList("config.array").unwrapped should contain theSameElementsInOrderAs Array(1, 2, 3)
      config.getList("config.array").get(0).unwrapped should be (1)
      config.getList("config.array").get(1).unwrapped should be (2)
      config.getList("config.array").get(2).unwrapped should be (3)
    }

    it("property like configuration load") {
      // Property形式でも、ネストした構造を扱える
      val config = ConfigFactory.load("propertylike1.conf")

      val c = config.getConfig("ns")
      c.getString("string.value") should be ("Hello World")
      c.getInt("intvalue") should be (20)

      val nested = config.getConfig("ns.nested")
      nested.getInt("intvalue") should be (10)
      nested.getString("stringvalue") should be ("string")

      // 設定項目の展開も可能
      nested.getString("substitutions") should be ("Hello World 20")
    }

    it("HOCON configuration load") {
      // HOCON（Human-Optimized Config Object Notation）という形式でも書けるらしい
      val config = ConfigFactory.load("hocon.conf")

      config.getNumber("hocon-config.nested.numeric") should be (10.5)

      val hc = config.getConfig("hocon-config")
      hc.getString("name") should be ("HOCON Presentation Configuration")
      hc.getInt("int-value") should be (1)

      hc.getStringList("nested.list-values") should contain theSameElementsInOrderAs Array("foo", "bar", "fuga")

      val nested = hc.getConfig("nested")
      nested.getNumber("numeric") should be (10.5)
    }
  }
}
