package org.infinispan.scala.hotrod.impl

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import org.infinispan.scala.hotrod._

private[impl] trait Marshaller {
  def toBytes(o: Any): Bytes
  def fromBytes(b: Bytes): Any
}

private[impl] class JavaMarshaller extends Marshaller {
  override def toBytes(o: Any): Bytes = {
    val bos = new ByteArrayOutputStream
    val out = new ObjectOutputStream(bos)
    out.writeObject(o)
    out.close()
    bos.toByteArray
  }

  override def fromBytes(b: Bytes): Any = {
    val bis = new ByteArrayInputStream(b)
    val in = new ObjectInputStream(bis)
    val obj = in.readObject()
    in.close()
    obj
  }
}