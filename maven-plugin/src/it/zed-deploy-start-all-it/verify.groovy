import java.io.*
import java.lang.*

def found=false
new File(basedir, "build.log").eachLine {
    line ->
        if (line.contains("has been started with PID")) {
            found=true
        }
}
assert found:"Process was not started."