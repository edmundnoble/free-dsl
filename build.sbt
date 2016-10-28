lazy val core = project.in(file("core")).settings(
  addCompilerPlugin("com.milessabin" % "si2712fix-plugin" % "1.2.0" cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.0" cross CrossVersion.binary),
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature", "-deprecation", "-language:higherKinds")
)

lazy val root = project.in(file(".")).settings(
  addCompilerPlugin("com.milessabin" % "si2712fix-plugin" % "1.2.0" cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.0" cross CrossVersion.binary),
  scalaVersion := "2.11.8"
).aggregate(core)

