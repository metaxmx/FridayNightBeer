lazy val webpack = TaskKey[Unit]("Run webpack when packaging the application")

def runWebpack(file: File) = {
  val operatingSystem = sys.props.getOrElse("os.name", "unknown")
  val cmd = if(operatingSystem contains "Windows")
    Process("cmd /c webpack", file)
  else
    Process("webpack", file)
  cmd.!
}

webpack := {
  if(runWebpack(baseDirectory.value) != 0) throw new Exception("Something goes wrong when running webpack.")
}

dist := (dist dependsOn webpack).value

stage := (stage dependsOn webpack).value

//test := ((test in Test) dependsOn webpack).value