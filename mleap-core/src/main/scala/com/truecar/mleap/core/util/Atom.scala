package com.truecar.mleap.core.util

/**
  * Created by hwilkins on 11/20/15.
  */
object Atom {
  def apply[T](): Atom[T] = new Atom[T]()
  def apply[T](initial: T): Atom[T] = new Atom[T]().set(initial)
  def unapply[T](atom: Atom[T]): Option[T] = Some(atom.get)
}

class Atom[T] {
  private var value: Option[T] = None

  def set(t: T): Atom[T] = {
    this.synchronized(value = Some(t))
    this
  }
  def get: T = this.synchronized(value.get)

  def transform(f: (T) => T): T = {
    this.synchronized {
      value = Some(f(value.get))
      value.get
    }
  }

  def copy(): Atom[T] = value match {
    case Some(v) => Atom(v)
    case None => Atom()
  }
}
