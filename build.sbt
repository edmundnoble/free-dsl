lazy val core = project.in(file("core")).settings(
  addCompilerPlugin("com.milessabin" % "si2712fix-plugin" % "1.2.0" cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.0" cross CrossVersion.binary),
  libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "0.7.2",
      "org.typelevel" %% "cats-kernel" % "0.7.2",
      "org.typelevel" %% "cats-macros" % "0.7.2",
      "org.typelevel" %% "cats-free" % "0.7.2",
      "org.atnos" %% "eff-cats" % "2.0.0-RC16",
      "org.atnos" %% "eff-cats-monix" % "2.0.0-RC16"
  ),
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature", "-deprecation", "-language:higherKinds")
)

lazy val root = project.in(file(".")).settings(
  addCompilerPlugin("com.milessabin" % "si2712fix-plugin" % "1.2.0" cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.0" cross CrossVersion.binary),
  scalaVersion := "2.11.8"
).aggregate(core)

