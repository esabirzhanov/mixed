val Http4sVersion = "0.19.0-M3"
val Specs2Version = "4.2.0"
val LogbackVersion = "1.2.3"
val CirceVersion = "0.10.0"
lazy val doobieVersion = "0.6.0"

lazy val root = (project in file("."))
  .settings(
    organization := "enterprise",
    name := "mixed",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-literal"       % CirceVersion,
      "io.circe"        %% "circe-parser"        % CirceVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
       "co.fs2" %% "fs2-io" % "1.0.0",
       "co.fs2" %% "fs2-core" % "1.0.0",
      "org.tpolecat" %% "doobie-specs2"   % doobieVersion,
      "org.tpolecat" %% "doobie-core"      % doobieVersion,
      "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
      "javax.xml.bind" % "jaxb-api" % "2.3.0"
    )
  )

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.6")
addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.2.4")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
)



