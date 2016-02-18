package com.truecar.mleap.core.util

import org.scalatest.FunSpec

/**
  * Created by hwilkins on 1/20/16.
  */
class AtomSpec extends FunSpec {
  describe("#get") {
    describe("when empty") {
      it("throws a NoSuchElementException when trying to access the value") {
        intercept[NoSuchElementException] {
          Atom[String]().get
        }
      }
    }

    describe("when it has a value") {
      it("returns the value") {
        assert(Atom[String]("hello").get == "hello")
      }
    }
  }

  describe("#set") {
    it("sets the value of the atom") {
      assert(Atom[String]("yah").set("hello").get == "hello")
    }
  }

  describe("#transform") {
    it("transforms the value of the atom") {
      val atom = Atom[Int](43)
      atom.transform(_ - 1)
      assert(atom.get == 42)
    }
  }
}
