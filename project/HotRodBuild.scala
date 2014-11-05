import sbt._
import sbt.Keys._

object HotRodBuild extends Build {

  lazy val buildSettings = Seq(
    organization        := "infinispan-hotrod-scala",
    version             := "0.1-SNAPSHOT",
    scalaVersion        := "2.11.2",
    libraryDependencies ++= Dependencies.all,
    resolvers           += "JBoss repository" at "http://repository.jboss.org/nexus/content/groups/public/"
  )

  override lazy val settings =
    super.settings ++
      buildSettings ++
      Seq(
        shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
      )

  lazy val baseSettings = Defaults.defaultSettings

  lazy val defaultSettings = baseSettings ++ Seq(
    scalacOptions in Compile ++= Seq(
      "-encoding", "UTF-8", "-target:jvm-1.6", "-deprecation", "-feature",
      "-unchecked", "-Xlog-reflective-calls", "-Xlint")
  )

}

object Dependencies {

  object Versions {
    val ispnversion = "7.0.0.Final"
//    val jbmarVersion = "1.4.4.Final"
  }

  object Compile {
    import Versions._
    val netty         = "io.netty"               % "netty-all"        % "4.0.23.Final"
//    val jbmar       = "org.jboss.marshalling" % "jboss-marshalling"         % jbmarVersion
//    val jbmarRiver  = "org.jboss.marshalling" % "jboss-marshalling-river"   % jbmarVersion
  }

  object Test {
    import Versions._
    val scalatest   = "org.scalatest"          %% "scalatest"                % "2.2.1"      % "test"
    val hotrod      = "org.infinispan"         %  "infinispan-server-hotrod" % ispnversion  % "test"
    val hotrodtests = "org.infinispan"         %  "infinispan-server-hotrod" % ispnversion  % "test" classifier "tests"
    val ispncore    = "org.infinispan"         %  "infinispan-core"          % ispnversion  % "test" classifier "tests"
    val log4j       = "log4j"                  %  "log4j"                    % "1.2.16"     % "test"
    val async       = "org.scala-lang.modules" %% "scala-async"              % "0.9.2"      % "test"
  }

  import Compile._

  val test = List(Test.scalatest, Test.hotrod, Test.hotrodtests, Test.ispncore, Test.log4j, Test.async)

  val all = List(netty) ::: test

}