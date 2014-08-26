package org.infinispan.scala.hotrod

import java.io.{ObjectInputStream, ByteArrayInputStream, ObjectOutputStream, ByteArrayOutputStream}

private[hotrod] trait Marshaller {
  def toBytes(o: Any): Bytes
  def fromBytes(b: Bytes): Any
}

private[hotrod] class JavaMarshaller extends Marshaller {
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