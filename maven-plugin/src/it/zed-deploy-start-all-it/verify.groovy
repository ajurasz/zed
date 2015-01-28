import java.io.*

def expectedOutputLine = new File(basedir, "build.log").readLines().find {
    it.contains("has been started with PID")
}
assert expectedOutputLine != null

